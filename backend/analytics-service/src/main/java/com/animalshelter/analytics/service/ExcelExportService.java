package com.animalshelter.analytics.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExcelExportService {

    private final MongoTemplate activityTrackingTemplate;

    public ExcelExportService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
    }

    public byte[] exportActivities() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "recorded_at"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "activities");

        String[] headers = {"ID", "Animal ID", "Activity Type", "Duration (min)", "Notes", "Recorded At", "Recorded By"};
        String[][] fieldDefs = {
                {"_id", "string"},
                {"animal_id", "string"},
                {"activity_type", "string"},
                {"duration_minutes", "number"},
                {"notes", "string"},
                {"recorded_at", "date"},
                {"recorded_by", "string"}
        };

        return writeExcel("Activities", headers, docs, fieldDefs);
    }

    public byte[] exportFeedings() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "meal_time"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "feedings");

        String[] headers = {"ID", "Animal ID", "Food Type", "Quantity (g)", "Meal Time", "Notes", "Recorded By"};
        String[][] fieldDefs = {
                {"_id", "string"},
                {"animal_id", "string"},
                {"food_type", "string"},
                {"quantity_grams", "number"},
                {"meal_time", "date"},
                {"notes", "string"},
                {"recorded_by", "string"}
        };

        return writeExcel("Feedings", headers, docs, fieldDefs);
    }

    public byte[] exportMeasurements() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "daily_measurements");

        String[] headers = {"ID", "Animal ID", "Date", "Weight (g)", "Energy Level", "Mood Level", "Notes", "Recorded By"};
        String[][] fieldDefs = {
                {"_id", "string"},
                {"animal_id", "string"},
                {"date", "date"},
                {"weight_grams", "number"},
                {"energy_level", "number"},
                {"mood_level", "number"},
                {"notes", "string"},
                {"recorded_by", "string"}
        };

        return writeExcel("Measurements", headers, docs, fieldDefs);
    }

    // ════════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ════════════════════════════════════════════════════════════

    private byte[] writeExcel(String sheetName, String[] headers, List<Document> docs, String[][] fieldDefs) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Alternate row style
            CellStyle altStyle = workbook.createCellStyle();
            altStyle.cloneStyleFrom(dataStyle);
            altStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Number style
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("#,##0.##"));

            // Write header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Write data rows
            for (int rowIdx = 0; rowIdx < docs.size(); rowIdx++) {
                Document doc = docs.get(rowIdx);
                Row row = sheet.createRow(rowIdx + 1);
                CellStyle rowStyle = rowIdx % 2 == 0 ? dataStyle : altStyle;

                for (int colIdx = 0; colIdx < fieldDefs.length; colIdx++) {
                    Cell cell = row.createCell(colIdx);
                    String fieldName = fieldDefs[colIdx][0];
                    String fieldType = fieldDefs[colIdx][1];
                    Object val = doc.get(fieldName);

                    if (val == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(rowStyle);
                        continue;
                    }

                    switch (fieldType) {
                        case "number" -> {
                            if (val instanceof Number num) {
                                cell.setCellValue(num.doubleValue());
                                cell.setCellStyle(numberStyle);
                            } else {
                                cell.setCellValue(val.toString());
                                cell.setCellStyle(rowStyle);
                            }
                        }
                        case "boolean" -> {
                            if (val instanceof Boolean b) {
                                cell.setCellValue(b ? "Yes" : "No");
                            } else {
                                cell.setCellValue(val.toString());
                            }
                            cell.setCellStyle(rowStyle);
                        }
                        case "date" -> {
                            cell.setCellValue(val.toString());
                            cell.setCellStyle(rowStyle);
                        }
                        default -> {
                            cell.setCellValue(val.toString());
                            cell.setCellStyle(rowStyle);
                        }
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Add a bit of padding
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(currentWidth + 512, 15000));
            }

            // Freeze header row
            sheet.createFreezePane(0, 1);

            // Auto-filter
            if (!docs.isEmpty()) {
                sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(
                        0, docs.size(), 0, headers.length - 1));
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel export", e);
        }
    }
}
