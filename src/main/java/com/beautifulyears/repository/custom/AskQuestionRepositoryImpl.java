package com.beautifulyears.repository.custom;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.rest.response.PageImpl;

public class AskQuestionRepositoryImpl implements AskQuestionRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<AskQuestion> getPage(String searchTxt, String askCategory, String askedBy, String answeredBy, Pageable pageable) {
		List<AskQuestion> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, askCategory, askedBy, answeredBy);
		query.with(pageable);
		
		stories = this.mongoTemplate.find(query, AskQuestion.class);
		long total = this.mongoTemplate.count(query, AskQuestion.class);
		PageImpl<AskQuestion> storyPage = new PageImpl<AskQuestion>(stories, pageable, total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, String askCategory, String askedBy, String answeredBy) {
		if (null != searchTxt && searchTxt!="") {
			q.addCriteria(Criteria.where("name").regex(searchTxt,"i"));
		}
		if (null != askedBy && askedBy!="") {
			q.addCriteria(Criteria.where("askedBy.$id").is(askedBy));
		}
		if (null != answeredBy && answeredBy!="") {
			q.addCriteria(Criteria.where("answeredBy.$id").is(answeredBy));
		}
		if (null != askCategory && askCategory!="") {
			q.addCriteria(Criteria.where("askCategory.$id").is(new ObjectId(askCategory) ));
		}
		return q;
	}

	@Override
	public long getCount(String searchTxt, String askCategory, String askedBy, String answeredBy) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, askCategory, askedBy, answeredBy);
		count = this.mongoTemplate.count(query, AskQuestion.class);
		return count;
	}

}