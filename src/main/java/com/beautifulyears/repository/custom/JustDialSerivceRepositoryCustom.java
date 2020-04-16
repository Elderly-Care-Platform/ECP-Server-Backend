package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.JustDailServices;
import com.beautifulyears.rest.response.PageImpl;


public interface JustDialSerivceRepositoryCustom {
    public PageImpl<JustDailServices> getPage(String name, String catId, String subCatId,Pageable page);

    public long getCount(String name, String catId, String subCatId);
    
}