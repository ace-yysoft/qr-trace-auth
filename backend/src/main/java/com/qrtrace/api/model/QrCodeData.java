package com.qrtrace.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "qrcodes")
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeData {
    @Id
    private String id;
    
    // Base Layer (공개 정보)
    private String productId;
    private String productName;
    private String productCategory;
    private LocalDateTime manufacturingDate;
    private LocalDateTime expiryDate;
    private String manufacturer;
    
    // Auth Layer (인증 정보)
    private String serialNumber;
    private String authHash;
    private boolean authenticated;
    @Transient
    private List<TrackingHistory> authenticationHistory;
    
    // Private Layer (기업용 정보)
    private ManufacturerData manufacturerData;
    private List<DistributionData> distributionData;
    private RetailData retailData;
    
    @Data
    public static class ManufacturerData {
        private String facilityId;
        private String productionLine;
        private String batchNumber;
        private Map<String, String> qualityData;
    }
    
    @Data
    public static class DistributionData {
        private LocalDateTime timestamp;
        private String locationId;
        private String status;
        private Double temperature;
        private Double humidity;
    }
    
    @Data
    public static class RetailData {
        private String storeId;
        private LocalDateTime receivedDate;
        private String shelfLocation;
        private Map<String, String> additionalInfo;
    }
} 