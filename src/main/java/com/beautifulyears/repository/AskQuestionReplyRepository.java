package com.beautifulyears.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.beautifulyears.domain.AskQuestionReply;
import com.beautifulyears.repository.custom.AskQuestionReplyRepositoryCustom;

@Repository
public interface AskQuestionReplyRepository extends
		MongoRepository<AskQuestionReply, Serializable>, AskQuestionReplyRepositoryCustom {

	public List<AskQuestionReply> findAll();
	
}
