package com.qrtrace.api.model;

import lombok.Data;

import java.util.Map;

@Data
public class QrCodeRequest {
    private BaseLayerInfo baseInfo;
    private AuthLayerInfo authInfo;
    private PrivateLayerInfo privateInfo;
    
    @Data
    public static class BaseLayerInfo {
        private String productId;
        private String productName;
        private String productCategory;
        private String manufacturer;
    }
    
    @Data
    public static class AuthLayerInfo {
        private String authType;
        private Map<String, String> authData;
    }
    
    @Data
    public static class PrivateLayerInfo {
        private ManufacturerInfo manufacturer;
        private Map<String, String> additionalData;
    }
    
    @Data
    public static class ManufacturerInfo {
        private String facilityId;
        private String productionLine;
        private String batchNumber;
    }
} 