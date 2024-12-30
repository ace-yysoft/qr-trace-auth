package com.qrtrace.api.repository;

import com.qrtrace.api.model.TrackingHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackingHistoryRepository extends MongoRepository<TrackingHistory, String> {
    List<TrackingHistory> findByQrCodeIdOrderByTimestampDesc(String qrCodeId);
}