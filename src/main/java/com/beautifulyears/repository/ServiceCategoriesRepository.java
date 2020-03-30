package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import com.beautifulyears.domain.ServiceCategories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoriesRepository extends MongoRepository<ServiceCategories, Serializable> {

    public List<ServiceCategories> findAll();

    public ServiceCategories findById(String Id);
    public ServiceCategories findByName(String name);

}
