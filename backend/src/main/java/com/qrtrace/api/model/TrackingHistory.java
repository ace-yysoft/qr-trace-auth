package com.qrtrace.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Document(collection = "tracking_history")
public class TrackingHistory {
    @Id
    private String id;
    @Indexed
    private String qrCodeId;
    private LocalDateTime timestamp;
    private String action;
    private ActorInfo actor;
    private String location;
    private Map<String, String> metadata;

    @Data
    public static class ActorInfo {
        private String type;
        private String id;
    }
} 