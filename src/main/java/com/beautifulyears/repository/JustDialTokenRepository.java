/**
 * 
 */
package com.beautifulyears.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.JustdialToken;


@Repository
public interface JustDialTokenRepository extends MongoRepository<JustdialToken, String> {
	public List<JustdialToken> findAll();
}
