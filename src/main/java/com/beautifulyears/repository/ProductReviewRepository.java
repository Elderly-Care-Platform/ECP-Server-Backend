package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.repository.custom.ProductReviewRepositoryCustom;

@Repository
public interface ProductReviewRepository extends
		MongoRepository<ProductReview, Serializable>, ProductReviewRepositoryCustom {

	public List<ProductReview> findAll();
	
}
