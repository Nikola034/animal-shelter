package com.animalshelter.analytics.service;

import com.animalshelter.analytics.dto.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfReportService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Fonts
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(33, 37, 41));
    private static final Font SUBTITLE_FONT = new Font(Font.HELVETICA, 12, Font.NORMAL, new Color(108, 117, 125));
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 16, Font.BOLD, new Color(13, 110, 253));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
    private static final Font CELL_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(33, 37, 41));
    private static final Font BOLD_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(33, 37, 41));
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(33, 37, 41));
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(108, 117, 125));

    private static final Color PRIMARY_COLOR = new Color(13, 110, 253);
    private static final Color HEADER_BG = new Color(52, 58, 64);
    private static final Color STRIPE_COLOR = new Color(248, 249, 250);

    public byte[] generateReport(ReportData data) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 40, 40, 50, 40);
            PdfWriter.getInstance(document, baos);
            document.open();

            String section = data.getSection() != null ? data.getSection() : "all";
            boolean all = "all".equals(section);

            // ── Title Page ─────────────────────────────────────
            addTitlePage(document, data);

            // ── Population Section ─────────────────────────────
            if (all || "population".equals(section)) {
                if (data.getPopulationOverview() != null) {
                    document.newPage();
                    addPopulationSection(document, data);
                }
            }

            // ── Activity Section ───────────────────────────────
            if (all || "activities".equals(section)) {
                if (data.getActivityByType() != null) {
                    document.newPage();
                    addActivitySection(document, data);
                }
            }

            // ── Feeding Section ────────────────────────────────
            if (all || "feeding".equals(section)) {
                if (data.getFeedingByType() != null) {
                    document.newPage();
                    addFeedingSection(document, data);
                }
            }

            // ── Health Section ─────────────────────────────────
            if (all || "health".equals(section)) {
                if (data.getAverageWeight() != null && !data.getAverageWeight().isEmpty()) {
                    document.newPage();
                    addHealthSection(document, data);
                }
            }

            // ── Footer ─────────────────────────────────────────
            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generated: " + data.getGeneratedAt(), SMALL_FONT);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    // ════════════════════════════════════════════════════════════
    //  TITLE PAGE
    // ════════════════════════════════════════════════════════════

    private void addTitlePage(Document document, ReportData data) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Title
        Paragraph title = new Paragraph("Animal Shelter", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph reportTitle = new Paragraph(data.getReportTitle(), new Font(Font.HELVETICA, 18, Font.BOLD, PRIMARY_COLOR));
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        reportTitle.setSpacingBefore(10);
        document.add(reportTitle);

        // Divider line
        document.add(Chunk.NEWLINE);
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(60);
        PdfPCell dividerCell = new PdfPCell();
        dividerCell.setBorderWidth(0);
        dividerCell.setBorderWidthBottom(2);
        dividerCell.setBorderColorBottom(PRIMARY_COLOR);
        dividerCell.setFixedHeight(5);
        divider.addCell(dividerCell);
        document.add(divider);
        document.add(Chunk.NEWLINE);

        // Period info
        String periodText = String.format("Period: %s - %s",
                data.getPeriodStart().format(DATE_FMT),
                data.getPeriodEnd().format(DATE_FMT));
        Paragraph period = new Paragraph(periodText, SUBTITLE_FONT);
        period.setAlignment(Element.ALIGN_CENTER);
        document.add(period);

        Paragraph generated = new Paragraph("Generated: " + data.getGeneratedAt(), SUBTITLE_FONT);
        generated.setAlignment(Element.ALIGN_CENTER);
        generated.setSpacingBefore(5);
        document.add(generated);

        // Summary box
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        String section = data.getSection() != null ? data.getSection() : "all";
        boolean all = "all".equals(section);

        // Build summary cards based on which section is included
        java.util.List<String[]> cards = new java.util.ArrayList<>();
        if (all || "population".equals(section)) {
            if (data.getPopulationOverview() != null) {
                cards.add(new String[]{"Total Animals", String.valueOf(data.getPopulationOverview().getTotalAnimals())});
            }
        }
        if (all || "activities".equals(section)) {
            cards.add(new String[]{"Activities", String.valueOf(data.getTotalActivities())});
            cards.add(new String[]{"Activity Min", String.valueOf(data.getTotalActivityMinutes())});
        }
        if (all || "feeding".equals(section)) {
            cards.add(new String[]{"Feedings", String.valueOf(data.getTotalFeedings())});
            cards.add(new String[]{"Food (kg)", String.format("%.1f", data.getTotalFoodGrams() / 1000.0)});
        }

        if (!cards.isEmpty()) {
            int cols = Math.min(cards.size(), 4);
            PdfPTable summaryTable = new PdfPTable(cols);
            summaryTable.setWidthPercentage(90);
            float[] widths = new float[cols];
            java.util.Arrays.fill(widths, 1f);
            summaryTable.setWidths(widths);

            for (int i = 0; i < cols; i++) {
                addSummaryCard(summaryTable, cards.get(i)[0], cards.get(i)[1]);
            }
            document.add(summaryTable);
        }
    }

    private void addSummaryCard(PdfPTable table, String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        cell.setBorderColor(new Color(222, 226, 230));
        cell.setPadding(12);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(248, 249, 250));

        Paragraph valuePara = new Paragraph(value, new Font(Font.HELVETICA, 18, Font.BOLD, PRIMARY_COLOR));
        valuePara.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(valuePara);

        Paragraph labelPara = new Paragraph(label, new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(108, 117, 125)));
        labelPara.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(labelPara);

        table.addCell(cell);
    }

    // ════════════════════════════════════════════════════════════
    //  POPULATION SECTION
    // ════════════════════════════════════════════════════════════

    private void addPopulationSection(Document document, ReportData data) throws DocumentException {
        addSectionHeader(document, "Population Statistics");

        PopulationOverview pop = data.getPopulationOverview();

        Paragraph totalPara = new Paragraph(
                "Total animals in shelter: " + pop.getTotalAnimals(), BOLD_FONT);
        totalPara.setSpacingBefore(10);
        document.add(totalPara);
        document.add(Chunk.NEWLINE);

        // By Category
        if (pop.getByCategory() != null && !pop.getByCategory().isEmpty()) {
            document.add(new Paragraph("Animals by Category", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(new String[]{"Category", "Count"}, new float[]{3, 1});
            for (int i = 0; i < pop.getByCategory().size(); i++) {
                CategoryCount c = pop.getByCategory().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, c.getCategory(), String.valueOf(c.getCount()));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        // By Status
        if (pop.getByStatus() != null && !pop.getByStatus().isEmpty()) {
            document.add(new Paragraph("Animals by Status", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(new String[]{"Status", "Count"}, new float[]{3, 1});
            for (int i = 0; i < pop.getByStatus().size(); i++) {
                StatusCount s = pop.getByStatus().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, s.getStatus(), String.valueOf(s.getCount()));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        // By Gender
        if (pop.getByGender() != null && !pop.getByGender().isEmpty()) {
            document.add(new Paragraph("Animals by Gender", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(new String[]{"Gender", "Count"}, new float[]{3, 1});
            for (int i = 0; i < pop.getByGender().size(); i++) {
                GenderCount g = pop.getByGender().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, g.getGender(), String.valueOf(g.getCount()));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        // Age Distribution
        if (pop.getAgeDistribution() != null && !pop.getAgeDistribution().isEmpty()) {
            document.add(new Paragraph("Age Distribution", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(new String[]{"Age Range", "Count"}, new float[]{3, 1});
            for (int i = 0; i < pop.getAgeDistribution().size(); i++) {
                AgeGroupCount a = pop.getAgeDistribution().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, a.getRange(), String.valueOf(a.getCount()));
            }
            document.add(table);
        }
    }

    // ════════════════════════════════════════════════════════════
    //  ACTIVITY SECTION
    // ════════════════════════════════════════════════════════════

    private void addActivitySection(Document document, ReportData data) throws DocumentException {
        addSectionHeader(document, "Activity Analytics");

        // Summary
        Paragraph summary = new Paragraph(String.format(
                "Total activities: %d  |  Total minutes: %d  |  Avg minutes per activity: %.1f",
                data.getTotalActivities(),
                data.getTotalActivityMinutes(),
                data.getTotalActivities() > 0
                        ? (double) data.getTotalActivityMinutes() / data.getTotalActivities() : 0),
                NORMAL_FONT);
        summary.setSpacingBefore(10);
        document.add(summary);
        document.add(Chunk.NEWLINE);

        // By Type
        if (data.getActivityByType() != null && !data.getActivityByType().isEmpty()) {
            document.add(new Paragraph("Activities by Type", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(
                    new String[]{"Activity Type", "Total Minutes", "Count", "Avg Min/Activity"},
                    new float[]{3, 2, 1.5f, 2});

            for (int i = 0; i < data.getActivityByType().size(); i++) {
                ActivityTypeStats a = data.getActivityByType().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                double avg = a.getCount() > 0 ? (double) a.getTotalMinutes() / a.getCount() : 0;
                addStyledRow(table, bg,
                        formatEnumLabel(a.getActivityType()),
                        String.valueOf(a.getTotalMinutes()),
                        String.valueOf(a.getCount()),
                        String.format("%.1f", avg));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        // Daily Summary
        if (data.getActivityDailySummary() != null && !data.getActivityDailySummary().isEmpty()) {
            document.add(new Paragraph("Daily Activity Summary", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(
                    new String[]{"Date", "Total Minutes", "Count"},
                    new float[]{3, 2, 2});

            List<DailySummary> dailyData = data.getActivityDailySummary();
            // Show last 30 rows max for readability
            int startIdx = Math.max(0, dailyData.size() - 30);
            for (int i = startIdx; i < dailyData.size(); i++) {
                DailySummary d = dailyData.get(i);
                Color bg = (i - startIdx) % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, d.getDate(), String.valueOf(d.getTotalValue()), String.valueOf(d.getCount()));
            }
            document.add(table);
        }
    }

    // ════════════════════════════════════════════════════════════
    //  FEEDING SECTION
    // ════════════════════════════════════════════════════════════

    private void addFeedingSection(Document document, ReportData data) throws DocumentException {
        addSectionHeader(document, "Feeding Analytics");

        // Summary
        Paragraph summary = new Paragraph(String.format(
                "Total feedings: %d  |  Total food: %.1f kg",
                data.getTotalFeedings(),
                data.getTotalFoodGrams() / 1000.0
                ),
                NORMAL_FONT);
        summary.setSpacingBefore(10);
        document.add(summary);
        document.add(Chunk.NEWLINE);

        // By Type
        if (data.getFeedingByType() != null && !data.getFeedingByType().isEmpty()) {
            document.add(new Paragraph("Feeding by Food Type", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(
                    new String[]{"Food Type", "Total (g)", "Total (kg)", "Count", "Avg per Feeding (g)"},
                    new float[]{2.5f, 1.5f, 1.5f, 1, 2});

            for (int i = 0; i < data.getFeedingByType().size(); i++) {
                FoodTypeStats f = data.getFeedingByType().get(i);
                Color bg = i % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                double avg = f.getCount() > 0 ? f.getTotalGrams() / f.getCount() : 0;
                addStyledRow(table, bg,
                        formatEnumLabel(f.getFoodType()),
                        String.format("%.0f", f.getTotalGrams()),
                        String.format("%.2f", f.getTotalGrams() / 1000.0),
                        String.valueOf(f.getCount()),
                        String.format("%.1f", avg));
            }
            document.add(table);
            document.add(Chunk.NEWLINE);
        }

        // Daily Summary
        if (data.getFeedingDailySummary() != null && !data.getFeedingDailySummary().isEmpty()) {
            document.add(new Paragraph("Daily Feeding Summary", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            PdfPTable table = createStyledTable(
                    new String[]{"Date", "Total Grams", "Count"},
                    new float[]{3, 2, 2});

            List<DailySummary> dailyData = data.getFeedingDailySummary();
            int startIdx = Math.max(0, dailyData.size() - 30);
            for (int i = startIdx; i < dailyData.size(); i++) {
                DailySummary d = dailyData.get(i);
                Color bg = (i - startIdx) % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
                addStyledRow(table, bg, d.getDate(), String.valueOf(d.getTotalValue()), String.valueOf(d.getCount()));
            }
            document.add(table);
        }
    }

    // ════════════════════════════════════════════════════════════
    //  HEALTH SECTION
    // ════════════════════════════════════════════════════════════

    private void addHealthSection(Document document, ReportData data) throws DocumentException {
        addSectionHeader(document, "Health Overview");

        document.add(new Paragraph("Average Weight Trend (all animals)", BOLD_FONT));
        document.add(Chunk.NEWLINE);

        PdfPTable table = createStyledTable(
                new String[]{"Date", "Average Weight (g)"},
                new float[]{3, 2});

        List<WeightDataPoint> weightData = data.getAverageWeight();
        int startIdx = Math.max(0, weightData.size() - 30);
        for (int i = startIdx; i < weightData.size(); i++) {
            WeightDataPoint w = weightData.get(i);
            Color bg = (i - startIdx) % 2 == 0 ? Color.WHITE : STRIPE_COLOR;
            addStyledRow(table, bg, w.getDate(), String.format("%.1f", w.getWeightGrams()));
        }
        document.add(table);
    }

    // ════════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ════════════════════════════════════════════════════════════

    private void addSectionHeader(Document document, String title) throws DocumentException {
        Paragraph header = new Paragraph(title, SECTION_FONT);
        header.setSpacingBefore(5);
        header.setSpacingAfter(5);
        document.add(header);

        // Blue underline
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(0);
        cell.setBorderWidthBottom(2);
        cell.setBorderColorBottom(PRIMARY_COLOR);
        cell.setFixedHeight(3);
        divider.addCell(cell);
        document.add(divider);
    }

    private PdfPTable createStyledTable(String[] headers, float[] widths) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setWidths(widths);
        table.setSpacingBefore(5);

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, HEADER_FONT));
            cell.setBackgroundColor(HEADER_BG);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorderWidth(0);
            table.addCell(cell);
        }

        return table;
    }

    private void addStyledRow(PdfPTable table, Color bgColor, String... values) {
        for (String v : values) {
            PdfPCell cell = new PdfPCell(new Phrase(v, CELL_FONT));
            cell.setBackgroundColor(bgColor);
            cell.setPadding(6);
            cell.setBorderWidth(0);
            cell.setBorderWidthBottom(0.5f);
            cell.setBorderColorBottom(new Color(222, 226, 230));
            table.addCell(cell);
        }
    }

    private String formatEnumLabel(String enumValue) {
        if (enumValue == null) return "";
        return enumValue.replace("_", " ")
                .substring(0, 1).toUpperCase()
                + enumValue.replace("_", " ").substring(1).toLowerCase();
    }
}
