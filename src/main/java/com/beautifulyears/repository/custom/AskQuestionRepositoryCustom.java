package com.beautifulyears.repository.custom;

import org.springframework.data.domain.Pageable;

import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.rest.response.PageImpl;

public interface AskQuestionRepositoryCustom {

	public PageImpl<AskQuestion> getPage(String searchTxt, String askCategory, String askedBy, String answeredBy, Pageable pageable);

	public long getCount(String searchTxt, String askCategory, String askedBy, String answeredBy);
}