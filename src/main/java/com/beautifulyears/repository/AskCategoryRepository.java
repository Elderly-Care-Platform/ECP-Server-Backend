package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.repository.custom.AskCategoryRepositoryCustom;

@Repository
public interface AskCategoryRepository extends
		MongoRepository<AskCategory, Serializable>, AskCategoryRepositoryCustom {

	public List<AskCategory> findAll();
	
}
