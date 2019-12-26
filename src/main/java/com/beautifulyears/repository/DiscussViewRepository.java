/**
 * Dec 25, 2019
 * Pulkit
 * 10:30:00 AM
 */
package com.beautifulyears.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.beautifulyears.domain.DiscussView;

@Repository
public interface DiscussViewRepository extends MongoRepository<DiscussView, String>{
    public List<DiscussView> findByContentIdAndUserIdAndIpAddress(String contentId,String userId, String ipAddress);
}
