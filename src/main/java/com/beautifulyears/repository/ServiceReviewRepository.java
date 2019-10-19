package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.repository.custom.ServiceReviewRepositoryCustom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceReviewRepository
        extends MongoRepository<ServiceReview, Serializable>, ServiceReviewRepositoryCustom {

    public List<ServiceReview> findAll();

    public List<ServiceReview> findByServiceId(String serviceId);
}
