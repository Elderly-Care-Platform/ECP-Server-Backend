package com.beautifulyears.repository;

import java.util.List;

import com.beautifulyears.domain.JustDailServices;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JustDialSerivcesRepository extends MongoRepository<JustDailServices, String> {
    public List<JustDailServices> findAll();
    public JustDailServices findById(String id);
}
