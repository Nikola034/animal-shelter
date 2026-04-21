package com.animalshelter.analytics.controller;

import com.animalshelter.analytics.config.UserContext;
import com.animalshelter.analytics.dto.ReportData;
import com.animalshelter.analytics.exception.AccessDeniedException;
import com.animalshelter.analytics.service.CsvExportService;
import com.animalshelter.analytics.service.ExcelExportService;
import com.animalshelter.analytics.service.PdfReportService;
import com.animalshelter.analytics.service.ReportDataService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private static final DateTimeFormatter FILE_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Set<String> VALID_SECTIONS = Set.of("all", "population", "activities", "feeding", "health");
    private static final Set<String> VALID_PERIODS = Set.of("monthly", "annual");

    private final ReportDataService reportDataService;
    private final PdfReportService pdfReportService;
    private final CsvExportService csvExportService;
    private final ExcelExportService excelExportService;
    private final UserContext userContext;

    public ReportController(ReportDataService reportDataService,
                            PdfReportService pdfReportService,
                            CsvExportService csvExportService,
                            ExcelExportService excelExportService,
                            UserContext userContext) {
        this.reportDataService = reportDataService;
        this.pdfReportService = pdfReportService;
        this.csvExportService = csvExportService;
        this.excelExportService = excelExportService;
        this.userContext = userContext;
    }

    // ══════════════════════════════════════════════════════════
    //  PDF REPORTS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdfReport(
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(defaultValue = "all") String section) {

        requireRole("Admin", "Caretaker", "Veterinarian");

        String normalizedPeriod = VALID_PERIODS.contains(period.toLowerCase()) ? period.toLowerCase() : "monthly";
        String normalizedSection = VALID_SECTIONS.contains(section.toLowerCase()) ? section.toLowerCase() : "all";

        ReportData data = reportDataService.generateReport(normalizedPeriod, normalizedSection);
        byte[] pdf = pdfReportService.generateReport(data);

        String filename = normalizedSection + "-" + normalizedPeriod + "-report-"
                + LocalDate.now().format(FILE_DATE_FMT) + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }

    // ══════════════════════════════════════════════════════════
    //  CSV EXPORTS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/export/activities/csv")
    public ResponseEntity<byte[]> exportActivitiesCsv() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] csv = csvExportService.exportActivities();
        String filename = "activities-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(csv.length)
                .body(csv);
    }

    @GetMapping("/export/feedings/csv")
    public ResponseEntity<byte[]> exportFeedingsCsv() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] csv = csvExportService.exportFeedings();
        String filename = "feedings-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(csv.length)
                .body(csv);
    }

    @GetMapping("/export/measurements/csv")
    public ResponseEntity<byte[]> exportMeasurementsCsv() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] csv = csvExportService.exportMeasurements();
        String filename = "measurements-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .contentLength(csv.length)
                .body(csv);
    }

    // ══════════════════════════════════════════════════════════
    //  EXCEL EXPORTS
    // ══════════════════════════════════════════════════════════

    @GetMapping("/export/activities/excel")
    public ResponseEntity<byte[]> exportActivitiesExcel() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] excel = excelExportService.exportActivities();
        String filename = "activities-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excel.length)
                .body(excel);
    }

    @GetMapping("/export/feedings/excel")
    public ResponseEntity<byte[]> exportFeedingsExcel() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] excel = excelExportService.exportFeedings();
        String filename = "feedings-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excel.length)
                .body(excel);
    }

    @GetMapping("/export/measurements/excel")
    public ResponseEntity<byte[]> exportMeasurementsExcel() {
        requireRole("Admin", "Caretaker", "Veterinarian");
        byte[] excel = excelExportService.exportMeasurements();
        String filename = "measurements-export-" + LocalDate.now().format(FILE_DATE_FMT) + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excel.length)
                .body(excel);
    }

    private void requireRole(String... allowedRoles) {
        String currentRole = userContext.getRole();
        for (String role : allowedRoles) {
            if (role.equals(currentRole)) {
                return;
            }
        }
        throw new AccessDeniedException("Access denied. Required role: " + String.join(" or ", allowedRoles));
    }
}
