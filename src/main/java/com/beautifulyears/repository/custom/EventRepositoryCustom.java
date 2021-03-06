package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import java.util.List;

import com.beautifulyears.domain.Event;
import com.beautifulyears.rest.response.PageImpl;

public interface EventRepositoryCustom {

	public PageImpl<Event> getPage(String searchTxt,Integer eventType,Long startDatetime,Integer pastEvents, Pageable pageable);

	public List<Event> getSuggestedEvents();
	public long getCount(String searchTxt,Integer eventType, Long startDatetime,Integer pastEvents);
}
