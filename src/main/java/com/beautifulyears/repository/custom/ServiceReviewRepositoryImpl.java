package com.beautifulyears.repository.custom;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.rest.response.PageImpl;

public class ServiceReviewRepositoryImpl implements ServiceReviewRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<ServiceReview> getPage(String searchTxt, String serviceId, Pageable pageable) {
		List<ServiceReview> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, serviceId);
		query.with(pageable);
		
		stories = this.mongoTemplate.find(query, ServiceReview.class);
		long total = this.mongoTemplate.count(query, ServiceReview.class);
		PageImpl<ServiceReview> storyPage = new PageImpl<ServiceReview>(stories, pageable, total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, String serviceId) {
		if (null != searchTxt && searchTxt != "") {
			q.addCriteria(Criteria.where("name").regex(searchTxt,"i"));
		}
		if(serviceId!= null  && serviceId != ""){
			q.addCriteria(Criteria.where("serviceId").is(serviceId));
		}
		return q;
	}

	@Override
	public long getCount(String searchTxt, String serviceId) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, serviceId);
		count = this.mongoTemplate.count(query, ServiceReview.class);
		return count;
	}

}