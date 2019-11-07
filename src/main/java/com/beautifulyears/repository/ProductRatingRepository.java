package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import com.beautifulyears.domain.ProductRating;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRatingRepository extends MongoRepository<ProductRating, Serializable> {

    public List<ProductRating> findAll();

    public List<ProductRating> findByProductId(String productId);

    public List<ProductRating> findByUserId(String userId);

}
