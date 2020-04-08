package com.beautifulyears.repository.custom;



import java.util.List;

import com.beautifulyears.domain.JustDailServices;
import com.beautifulyears.rest.response.PageImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class JustDialServiceRepositoryImpl implements JustDialServiceRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<JustDailServices> getServiceProvidersByFilterCriteria(String name, String catId, String subCatId,
			Pageable page) {
		List<JustDailServices> justDailServiceList = null;
		Query q = new Query();

		q = getQuery(q, name, catId, subCatId);

		q.with(page);
		justDailServiceList = mongoTemplate.find(q, JustDailServices.class);

		long total = this.mongoTemplate.count(q, JustDailServices.class);
		PageImpl<JustDailServices> justDailServicePage = new PageImpl<JustDailServices>(justDailServiceList, page,
				total);

		return justDailServicePage;
	}

	@Override
	public long getServiceProvidersByFilterCriteriaCount(String name, String catId, String subCatId) {
		Query q = new Query();
		q = getQuery(q, name, catId, subCatId);
		long total = this.mongoTemplate.count(q, JustDailServices.class);
		return total;
	}

	private Query getQuery(Query q, String name, String catId, String subCatId) {

		if (null != subCatId && "" != subCatId) {
			q.addCriteria(Criteria.where("serviceInfo.categoryId").is(subCatId));
		}

		if (null != catId && "" != catId) {
			q.addCriteria(Criteria.where("serviceInfo.catId").is(catId));
		}

		if (null != name && "" != name) {
			// get service by name like %name%
			q.addCriteria(Criteria.where("serviceInfo.name").regex(name, "i"));
		} else {
			q.addCriteria(Criteria.where("serviceInfo.name").exists(true));
		}
		return q;
	}

	// @Override
	// public PageImpl<JustDailServices> findAllServices(Pageable pageable) {

	// 	List<JustDailServices> justDailServicesList = mongoTemplate.findAll(JustDailServices.class);
	// 	long total = justDailServicesList.size();
	// 	PageImpl<JustDailServices> justDailServicesPage = new PageImpl<JustDailServices>(justDailServicesList, pageable,
	// 			total);
	// 	return justDailServicesPage;
	// }

	// @Override
	// public JustDailServices findByServiceId(String docId) {
	// 	JustDailServices serivce = null;
	// 	Query q = new Query();
	// 	q.addCriteria(Criteria.where("serviceInfo.docid").is(docId));
	// 	serivce = mongoTemplate.findOne(q, JustDailServices.class);
	// 	return serivce;
	// }
}