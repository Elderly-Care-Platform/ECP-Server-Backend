package com.beautifulyears.repository.custom;

import java.util.List;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.constants.EventConstants;
import com.beautifulyears.domain.Event;
import com.beautifulyears.rest.response.PageImpl;

public class EventRepositoryImpl implements EventRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<Event> getPage(String searchTxt, Integer eventType,Long startDatetime, Pageable pageable) {
		List<Event> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, eventType, startDatetime);
		query.with(pageable);
		query.addCriteria(Criteria.where("status").is(EventConstants.EVENT_STATUS_ACTIVE));
		
		stories = this.mongoTemplate.find(query, Event.class);
		long total = this.mongoTemplate.count(query, Event.class);
		PageImpl<Event> storyPage = new PageImpl<Event>(stories, pageable,
				total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, Integer eventType, Long startDatetime) {
		if (null != searchTxt) {
			q.addCriteria(Criteria.where("title").regex(searchTxt,"i"));
		}
		if (null != startDatetime) {
			Date dt = new Date(startDatetime);			
			q.addCriteria(Criteria.where("datetime").gte(dt));
		}
		if(eventType !=null && eventType > 0){
			q.addCriteria(Criteria.where("eventType").is(eventType));
		}
		return q;
	}

	@Override
	public long getCount(String searchTxt, Integer eventType, Long startDatetime) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, eventType, startDatetime);
		query.addCriteria(Criteria.where("status").is(
				EventConstants.EVENT_STATUS_ACTIVE));
		
		count = this.mongoTemplate.count(query, Event.class);
		return count;
	}

}