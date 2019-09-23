package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.repository.custom.ProductCategoryRepositoryCustom;

@Repository
public interface ProductCategoryRepository extends
		MongoRepository<ProductCategory, Serializable>, ProductCategoryRepositoryCustom {

	public List<ProductCategory> findAll();
	
}
