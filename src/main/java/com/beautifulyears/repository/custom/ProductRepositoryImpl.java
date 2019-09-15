package com.beautifulyears.repository.custom;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.constants.ProductConstants;
import com.beautifulyears.domain.Product;
import com.beautifulyears.rest.response.PageImpl;

public class ProductRepositoryImpl implements ProductRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<Product> getPage(String searchTxt, String category, Pageable pageable) {
		List<Product> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, category);
		query.with(pageable);
		query.addCriteria(Criteria.where("status").is(ProductConstants.PRODUCT_STATUS_ACTIVE));
		
		stories = this.mongoTemplate.find(query, Product.class);
		long total = this.mongoTemplate.count(query, Product.class);
		PageImpl<Product> storyPage = new PageImpl<Product>(stories, pageable, total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, String category) {
		if (null != searchTxt) {
			q.addCriteria(
				new Criteria().orOperator(
					Criteria.where("name").regex(searchTxt,"i"),
					Criteria.where("shortDescription").regex(searchTxt,"i")
				)
			);
		}
		
		return q;
	}

	@Override
	public long getCount(String searchTxt, String category) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, category);
		query.addCriteria(Criteria.where("status").is(
				ProductConstants.PRODUCT_STATUS_ACTIVE));
		
		count = this.mongoTemplate.count(query, Product.class);
		return count;
	}

}