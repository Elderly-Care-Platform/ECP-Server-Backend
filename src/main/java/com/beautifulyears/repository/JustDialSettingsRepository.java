/**
 * 
 */
package com.beautifulyears.repository;

import java.util.List;

import com.beautifulyears.domain.JustDailSetting;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface JustDialSettingsRepository extends MongoRepository<JustDailSetting, String> {
	public List<JustDailSetting> findAll();
}
