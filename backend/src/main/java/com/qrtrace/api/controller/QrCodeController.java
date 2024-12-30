package com.qrtrace.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qrtrace.api.model.QrCodeData;
import com.qrtrace.api.model.QrCodeRequest;
import com.qrtrace.api.service.QrCodeService;
import com.qrtrace.api.exception.QrCodeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/qrcodes")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class QrCodeController {

    private final QrCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<QrCodeData> createQrCode(@RequestBody QrCodeRequest request) {
        log.info("Creating QR code for product: {}", request.getBaseInfo().getProductId());
        try {
            QrCodeData qrCode = qrCodeService.createQrCode(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(qrCode);
        } catch (QrCodeException e) {
            log.error("Failed to create QR code: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<QrCodeData> getQrCode(@PathVariable String serialNumber) {
        log.info("Retrieving QR code with serial number: {}", serialNumber);
        try {
            QrCodeData qrCode = qrCodeService.getQrCodeBySerialNumber(serialNumber);
            return ResponseEntity.ok(qrCode);
        } catch (QrCodeException e) {
            log.error("Failed to retrieve QR code: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/verify/serial")
    public ResponseEntity<QrCodeData> verifyQrCodeBySerial(@RequestBody Map<String, String> request) {
        String serialNumber = request.get("serialNumber");
        log.info("Verifying QR code with serial number: {}", serialNumber);
        
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new QrCodeException("Serial number is required");
        }
        
        try {
            QrCodeData qrCode = qrCodeService.verifyQrCode(serialNumber.trim());
            return ResponseEntity.ok(qrCode);
        } catch (QrCodeException e) {
            log.error("Failed to verify QR code: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{serialNumber}/qr-image")
    public ResponseEntity<Map<String, String>> generateQrImage(@PathVariable String serialNumber) {
        log.info("Generating QR image for serial number: {}", serialNumber);
        try {
            QrCodeData qrCodeData = qrCodeService.getQrCodeBySerialNumber(serialNumber);
            String qrContent = objectMapper.writeValueAsString(qrCodeData);
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 300, 300);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            Map<String, String> response = new HashMap<>();
            response.put("qrImage", Base64.getEncoder().encodeToString(outputStream.toByteArray()));
            response.put("serialNumber", serialNumber);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to generate QR image: {}", e.getMessage());
            throw new QrCodeException("QR 이미지 생성에 실패했습니다", e);
        }
    }
} 