package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.rest.response.PageImpl;

public interface ProductReviewRepositoryCustom {

	public PageImpl<ProductReview> getPage(String searchTxt, String productId, Pageable pageable);

	public long getCount(String searchTxt, String productId);
}