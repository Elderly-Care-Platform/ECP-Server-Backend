package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.rest.response.PageImpl;

public interface ProductCategoryRepositoryCustom {

	public PageImpl<ProductCategory> getPage(String searchTxt, Pageable pageable);

	public long getCount(String searchTxt);
}