package com.beautifulyears.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.rest.response.PageImpl;

public interface UserProfileRepositoryCustom {
	public PageImpl<UserProfile> getServiceProvidersByFilterCriteria(String name, Object[] userTypes, String city, List<ObjectId> tagIds,Boolean isFeatured, List<ObjectId> experties, Pageable page,List<String> fields,List<String> catId,String source,Boolean verified,Boolean searchBynameOrCatid);
	public long getServiceProvidersByFilterCriteriaCount(String name, Object[] userTypes, String city, List<ObjectId> tagIds,Boolean isFeatured, List<ObjectId> experties,List<String> catId,String source,Pageable page,Boolean verified,Boolean searchBynameOrCatid);
	public PageImpl<UserProfile> findAllUserProfiles(Pageable pageable);
	public UserProfile findByUserId(String userId);
}