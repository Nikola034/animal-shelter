package com.animalshelter.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "event_log")
public class AnalyticsEventLog {

    @Id
    private String id;

    @Indexed
    @Field("event_type")
    private String eventType;

    @Indexed
    @Field("animal_id")
    private String animalId;

    @Field("payload")
    private Map<String, Object> payload;

    @CreatedDate
    @Field("received_at")
    private Instant receivedAt;
}
