package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import com.beautifulyears.domain.ServiceRatings;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRatingsRepository extends MongoRepository<ServiceRatings, Serializable> {

    public List<ServiceRatings> findAll();

    public List<ServiceRatings> findByServiceId(String serviceId);
    public List<ServiceRatings> findByUserId(String userId);

}
