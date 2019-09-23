package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.Product;
import com.beautifulyears.rest.response.PageImpl;

public interface ProductRepositoryCustom {

	public PageImpl<Product> getPage(String searchTxt, String category, Pageable pageable);

	public long getCount(String searchTxt, String category);
}
