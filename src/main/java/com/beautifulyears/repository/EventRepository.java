package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.Event;
import com.beautifulyears.repository.custom.EventRepositoryCustom;

@Repository
public interface EventRepository extends
		MongoRepository<Event, Serializable>, EventRepositoryCustom {

	public List<Event> findAll();
	
}
