package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.AskQuestionReply;
import com.beautifulyears.rest.response.PageImpl;

public interface AskQuestionReplyRepositoryCustom {

	public PageImpl<AskQuestionReply> getPage(String searchTxt, String questionId, Pageable pageable);

	public long getCount(String searchTxt, String questionId);
	
	public long getReplyCount(String questionId, String answeredById);
	public long getReplyCount(String answeredById);
}