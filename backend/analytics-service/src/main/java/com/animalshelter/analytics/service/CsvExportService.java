package com.animalshelter.analytics.service;

import com.opencsv.CSVWriter;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class CsvExportService {

    private final MongoTemplate activityTrackingTemplate;

    public CsvExportService(
            @Qualifier("activityTrackingTemplate") MongoTemplate activityTrackingTemplate) {
        this.activityTrackingTemplate = activityTrackingTemplate;
    }

    public byte[] exportActivities() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "recorded_at"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "activities");

        return writeCsv(
                new String[]{"ID", "Animal ID", "Activity Type", "Duration (min)", "Notes", "Recorded At", "Recorded By"},
                docs,
                doc -> new String[]{
                        getStringOrEmpty(doc, "_id"),
                        getStringOrEmpty(doc, "animal_id"),
                        getStringOrEmpty(doc, "activity_type"),
                        getNumberAsString(doc, "duration_minutes"),
                        getStringOrEmpty(doc, "notes"),
                        getDateAsString(doc, "recorded_at"),
                        getStringOrEmpty(doc, "recorded_by")
                }
        );
    }

    public byte[] exportFeedings() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "meal_time"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "feedings");

        return writeCsv(
                new String[]{"ID", "Animal ID", "Food Type", "Quantity (g)", "Meal Time", "Notes", "Recorded By"},
                docs,
                doc -> new String[]{
                        getStringOrEmpty(doc, "_id"),
                        getStringOrEmpty(doc, "animal_id"),
                        getStringOrEmpty(doc, "food_type"),
                        getNumberAsString(doc, "quantity_grams"),
                        getDateAsString(doc, "meal_time"),
                        getStringOrEmpty(doc, "notes"),
                        getStringOrEmpty(doc, "recorded_by")
                }
        );
    }

    public byte[] exportMeasurements() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date"));
        List<Document> docs = activityTrackingTemplate.find(query, Document.class, "daily_measurements");

        return writeCsv(
                new String[]{"ID", "Animal ID", "Date", "Weight (g)", "Energy Level", "Mood Level", "Notes", "Recorded By"},
                docs,
                doc -> new String[]{
                        getStringOrEmpty(doc, "_id"),
                        getStringOrEmpty(doc, "animal_id"),
                        getDateAsString(doc, "date"),
                        getNumberAsString(doc, "weight_grams"),
                        getNumberAsString(doc, "energy_level"),
                        getNumberAsString(doc, "mood_level"),
                        getStringOrEmpty(doc, "notes"),
                        getStringOrEmpty(doc, "recorded_by")
                }
        );
    }

    // ════════════════════════════════════════════════════════════
    //  HELPER METHODS
    // ════════════════════════════════════════════════════════════

    @FunctionalInterface
    private interface RowMapper {
        String[] map(Document doc);
    }

    private byte[] writeCsv(String[] headers, List<Document> docs, RowMapper mapper) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {

            // BOM for Excel UTF-8 compatibility
            baos.write(0xEF);
            baos.write(0xBB);
            baos.write(0xBF);

            writer.writeNext(headers);

            for (Document doc : docs) {
                writer.writeNext(mapper.map(doc));
            }

            writer.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV export", e);
        }
    }

    private String getStringOrEmpty(Document doc, String key) {
        Object val = doc.get(key);
        return val != null ? val.toString() : "";
    }

    private String getNumberAsString(Document doc, String key) {
        Object val = doc.get(key);
        if (val instanceof Number num) {
            if (val instanceof Double || val instanceof Float) {
                return String.format("%.2f", num.doubleValue());
            }
            return String.valueOf(num.longValue());
        }
        return val != null ? val.toString() : "";
    }

    private String getDateAsString(Document doc, String key) {
        Object val = doc.get(key);
        return val != null ? val.toString() : "";
    }

    private String getBooleanAsString(Document doc, String key) {
        Object val = doc.get(key);
        if (val instanceof Boolean b) {
            return b ? "Yes" : "No";
        }
        return val != null ? val.toString() : "";
    }
}
