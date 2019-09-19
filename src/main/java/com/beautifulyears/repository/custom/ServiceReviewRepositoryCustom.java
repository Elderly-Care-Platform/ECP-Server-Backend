package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.rest.response.PageImpl;

public interface ServiceReviewRepositoryCustom {

	public PageImpl<ServiceReview> getPage(String searchTxt, String serviceId, Pageable pageable);

	public long getCount(String searchTxt, String serviceId);
}