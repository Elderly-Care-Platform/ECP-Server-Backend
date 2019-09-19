package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.repository.custom.ServiceReviewRepositoryCustom;


@Repository
public interface ServiceReviewRepository
        extends MongoRepository<ServiceReview, Serializable>, ServiceReviewRepositoryCustom {

    public List<ServiceReview> findAll();

}
