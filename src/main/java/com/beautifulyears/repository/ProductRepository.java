package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.Product;
import com.beautifulyears.repository.custom.ProductRepositoryCustom;

@Repository
public interface ProductRepository extends
		MongoRepository<Product, Serializable>, ProductRepositoryCustom {

	public List<Product> findAll();
	
}
