package com.beautifulyears.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.Discuss;
import com.beautifulyears.rest.response.PageImpl;

public interface DiscussRepositoryCustom {

	public PageImpl<Discuss> getPage(String searchTxt, List<String> discussTypeArray,
			List<ObjectId> tagIds, String userId, Boolean isFeatured,
			Boolean isPromotion, Pageable pageable);

	public long getCount(String searchTxt, List<String> discussTypeArray, List<ObjectId> tagIds,
			String userId, Boolean isFeatured, Boolean isPromotion);
}
