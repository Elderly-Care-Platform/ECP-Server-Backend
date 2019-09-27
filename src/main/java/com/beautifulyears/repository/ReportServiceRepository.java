/**
 * 
 */
package com.beautifulyears.repository;

import java.util.List;

import com.beautifulyears.domain.ReportService;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportServiceRepository extends MongoRepository<ReportService, String> {
    public List<ReportService> findAll();

    public List<ReportService> findByServiceId(String serviceId);

    public ReportService findByUserId(String userId);
}
