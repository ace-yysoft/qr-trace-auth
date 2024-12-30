package com.qrtrace.api.service;

import com.qrtrace.api.model.QrCodeData;
import com.qrtrace.api.model.QrCodeRequest;
import com.qrtrace.api.model.TrackingHistory;
import com.qrtrace.api.repository.QrCodeRepository;
import com.qrtrace.api.repository.TrackingHistoryRepository;
import com.qrtrace.api.exception.QrCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final TrackingHistoryRepository trackingHistoryRepository;

    @Transactional
    public QrCodeData createQrCode(QrCodeRequest request) {
        try {
            // QR 코드 데이터 생성
            QrCodeData qrCodeData = new QrCodeData();
            
            // Base Layer 설정
            qrCodeData.setProductId(request.getBaseInfo().getProductId());
            qrCodeData.setProductName(request.getBaseInfo().getProductName());
            qrCodeData.setProductCategory(request.getBaseInfo().getProductCategory());
            qrCodeData.setManufacturer(request.getBaseInfo().getManufacturer());
            qrCodeData.setManufacturingDate(LocalDateTime.now());
            qrCodeData.setExpiryDate(LocalDateTime.now().plusMonths(12));
            
            // Auth Layer 설정
            qrCodeData.setSerialNumber(generateSerialNumber());
            qrCodeData.setAuthHash(generateAuthHash(request.getAuthInfo()));
            qrCodeData.setAuthenticated(true);
            qrCodeData.setAuthenticationHistory(new ArrayList<>());
            
            // Private Layer 설정
            QrCodeData.ManufacturerData manufacturerData = new QrCodeData.ManufacturerData();
            manufacturerData.setFacilityId(request.getPrivateInfo().getManufacturer().getFacilityId());
            manufacturerData.setProductionLine(request.getPrivateInfo().getManufacturer().getProductionLine());
            manufacturerData.setBatchNumber(request.getPrivateInfo().getManufacturer().getBatchNumber());
            manufacturerData.setQualityData(request.getPrivateInfo().getAdditionalData());
            qrCodeData.setManufacturerData(manufacturerData);
            
            log.debug("Saving QR code data: {}", qrCodeData);
            
            // MongoDB에 저장
            QrCodeData savedQrCode = qrCodeRepository.save(qrCodeData);
            log.info("QR code saved successfully with ID: {}", savedQrCode.getId());
            
            // 이력 추적 기록 생성
            TrackingHistory history = new TrackingHistory();
            history.setQrCodeId(savedQrCode.getId());
            history.setTimestamp(LocalDateTime.now());
            history.setAction("CREATED");
            history.setLocation("SYSTEM");  // 위치 정보 추가
            
            Map<String, String> metadata = new HashMap<>();
            metadata.put("productId", request.getBaseInfo().getProductId());
            metadata.put("manufacturer", request.getBaseInfo().getManufacturer());
            history.setMetadata(metadata);  // 메타데이터 추가
            
            TrackingHistory.ActorInfo actor = new TrackingHistory.ActorInfo();
            actor.setType("SYSTEM");
            actor.setId("QRCODE_GENERATOR");
            history.setActor(actor);
            
            trackingHistoryRepository.save(history);
            log.info("Tracking history saved successfully");
            
            return savedQrCode;
        } catch (Exception e) {
            log.error("Error creating QR code: ", e);
            throw new QrCodeException("Failed to create QR code", e);
        }
    }

    private String generateSerialNumber() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateAuthHash(QrCodeRequest.AuthLayerInfo authInfo) {
        // 실제 구현에서는 더 복잡한 해시 알고리즘 사용
        String authData = authInfo.getAuthType() + 
                         String.join("", authInfo.getAuthData().values());
        return UUID.nameUUIDFromBytes(authData.getBytes()).toString();
    }

    public QrCodeData getQrCodeBySerialNumber(String serialNumber) {
        return qrCodeRepository.findBySerialNumber(serialNumber)
            .orElseThrow(() -> new RuntimeException("QR Code not found"));
    }

    @Transactional
    public QrCodeData verifyQrCode(String serialNumber) {
        log.debug("Attempting to verify QR code with serial number: {}", serialNumber);
        
        QrCodeData qrCode = qrCodeRepository.findBySerialNumber(serialNumber)
            .orElseThrow(() -> {
                log.error("QR Code not found with serial number: {}", serialNumber);
                return new QrCodeException("QR Code not found with serial number: " + serialNumber);
            });

        if (!qrCode.isAuthenticated()) {
            log.error("QR Code is not authenticated: {}", serialNumber);
            throw new QrCodeException("QR Code is not authenticated: " + serialNumber);
        }

        log.debug("Found QR code: {}", qrCode);
        
        // 이력 추적은 tracking_history 컬렉션에만 저장
        TrackingHistory trackingHistory = new TrackingHistory();
        trackingHistory.setQrCodeId(qrCode.getId());
        trackingHistory.setTimestamp(LocalDateTime.now());
        trackingHistory.setAction("VERIFIED");
        trackingHistory.setLocation("SYSTEM");

        TrackingHistory.ActorInfo actor = new TrackingHistory.ActorInfo();
        actor.setType("USER");
        actor.setId("ANONYMOUS");
        trackingHistory.setActor(actor);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("verificationMethod", "SERIAL");
        metadata.put("serialNumber", serialNumber);
        trackingHistory.setMetadata(metadata);

        trackingHistoryRepository.save(trackingHistory);
        
        // QrCodeData에는 이력을 저장하지 않음
        return qrCode;
    }
} 