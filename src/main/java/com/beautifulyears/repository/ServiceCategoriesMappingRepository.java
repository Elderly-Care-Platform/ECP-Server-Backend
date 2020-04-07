package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import com.beautifulyears.domain.ServiceCategoriesMapping;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoriesMappingRepository extends MongoRepository<ServiceCategoriesMapping, Serializable> {

    public List<ServiceCategoriesMapping> findAll();

    public ServiceCategoriesMapping findById(String Id);
    public ServiceCategoriesMapping findByName(String name);

}
