package com.beautifulyears.repository.custom;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.domain.AskQuestionReply;
import com.beautifulyears.rest.response.PageImpl;

public class AskQuestionReplyRepositoryImpl implements AskQuestionReplyRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<AskQuestionReply> getPage(String searchTxt, String questionId, Pageable pageable) {
		List<AskQuestionReply> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, questionId);
		query.with(pageable);
		
		stories = this.mongoTemplate.find(query, AskQuestionReply.class);
		long total = this.mongoTemplate.count(query, AskQuestionReply.class);
		PageImpl<AskQuestionReply> storyPage = new PageImpl<AskQuestionReply>(stories, pageable, total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, String questionId) {
		if (null != searchTxt && searchTxt != "") {
			q.addCriteria(Criteria.where("name").regex(searchTxt,"i"));
		}
		if(questionId!= null  && questionId != ""){
			q.addCriteria(Criteria.where("askQuestionId").is(questionId));
		}
		return q;
	}

	@Override
	public long getCount(String searchTxt, String questionId) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, questionId);
		count = this.mongoTemplate.count(query, AskQuestionReply.class);
		return count;
	}

}