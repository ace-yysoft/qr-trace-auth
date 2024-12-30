package com.qrtrace.api.repository;

import com.qrtrace.api.model.QrCodeData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QrCodeRepository extends MongoRepository<QrCodeData, String> {
    Optional<QrCodeData> findBySerialNumber(String serialNumber);
} 