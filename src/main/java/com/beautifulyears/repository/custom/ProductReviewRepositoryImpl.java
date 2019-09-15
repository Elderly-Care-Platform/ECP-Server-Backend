package com.beautifulyears.repository.custom;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.rest.response.PageImpl;

public class ProductReviewRepositoryImpl implements ProductReviewRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<ProductReview> getPage(String searchTxt, Pageable pageable) {
		List<ProductReview> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt);
		query.with(pageable);
		
		stories = this.mongoTemplate.find(query, ProductReview.class);
		long total = this.mongoTemplate.count(query, ProductReview.class);
		PageImpl<ProductReview> storyPage = new PageImpl<ProductReview>(stories, pageable, total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt) {
		if (null != searchTxt) {
			q.addCriteria(Criteria.where("name").regex(searchTxt,"i"));
		}
		return q;
	}

	@Override
	public long getCount(String searchTxt) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt);
		count = this.mongoTemplate.count(query, ProductReview.class);
		return count;
	}

}