package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.rest.response.PageImpl;

public interface AskCategoryRepositoryCustom {

	public PageImpl<AskCategory> getPage(String searchTxt, Pageable pageable);

	public long getCount(String searchTxt);
}