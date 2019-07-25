package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.Event;
import com.beautifulyears.rest.response.PageImpl;

public interface EventRepositoryCustom {

	public PageImpl<Event> getPage(String searchTxt,Integer eventType, Pageable pageable);

	public long getCount(String searchTxt,Integer eventType);
}
