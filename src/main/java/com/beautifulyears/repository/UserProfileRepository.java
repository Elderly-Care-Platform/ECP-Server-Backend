package com.beautifulyears.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.repository.custom.UserProfileRepositoryCustom;


@Repository
public interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, String>,UserProfileRepositoryCustom{

	@Query("{'$and': [{'userId': ?0}, {'userTypes':{ $nin: [8] }} ]}")
	public List<UserProfile> findAllProfileByUserId(String userId);
	
	 @Query("{'userTypes':{$in:?0}}" )
	 public Page<UserProfile> getServiceProvidersByCriteria(Object[] userTypes, Pageable page);
	
}