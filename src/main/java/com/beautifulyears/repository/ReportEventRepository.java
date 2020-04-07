/**
 * 
 */
package com.beautifulyears.repository;

import java.util.List;

import com.beautifulyears.domain.ReportEvent;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportEventRepository extends MongoRepository<ReportEvent, String> {
    public List<ReportEvent> findAll();

    public List<ReportEvent> findByEventId(String serviceId);

    public ReportEvent findByUserId(String userId);
}
