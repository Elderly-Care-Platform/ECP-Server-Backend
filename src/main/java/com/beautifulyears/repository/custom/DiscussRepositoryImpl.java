package com.beautifulyears.repository.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.menu.Menu;
import com.beautifulyears.rest.response.PageImpl;

public class DiscussRepositoryImpl implements DiscussRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<Discuss> getPage(String searchTxt, List<String> discussTypeArray,
			List<ObjectId> tagIds, String userId, Boolean isFeatured,
			Boolean isPromotion, Pageable pageable) {
		List<Discuss> stories = null;

		Query query = new Query();
		query = getQuery(query, searchTxt, discussTypeArray, tagIds, userId, isFeatured,
				isPromotion);
		query.with(new Sort(new Order(Direction.DESC, "isPromotion")));
		query.with(pageable);
		query.addCriteria(Criteria.where("status").is(
				DiscussConstants.DISCUSS_STATUS_ACTIVE));

		stories = this.mongoTemplate.find(query, Discuss.class);
		long total = this.mongoTemplate.count(query, Discuss.class);
		PageImpl<Discuss> storyPage = new PageImpl<Discuss>(stories, pageable,
				total);

		return storyPage;
	}

	private Query getQuery(Query q, String searchTxt, List<String> discussTypeArray,
			List<ObjectId> tagIds, String userId, Boolean isFeatured,
			Boolean isPromotion) {

		if(searchTxt != null && !searchTxt.isEmpty()){

			// get category list
			List<Menu> catList = null;
			Query query = new Query();
			query.addCriteria(Criteria.where("parentMenuId").is("564071623e60f5b66f62df27"));
			if(searchTxt != null && !searchTxt.isEmpty()){
				query.addCriteria(Criteria.where("displayMenuName").regex(searchTxt,"i"));
			}
			catList = this.mongoTemplate.find(query, Menu.class);
			List<ObjectId> catObjList = new ArrayList<ObjectId>();
			if(catList != null){
				Iterator<Menu> catListIterator = catList.iterator();
				while (catListIterator.hasNext()) {
					Menu m = catListIterator.next();
					if(m.getTags() != null){
						catObjList.add(new ObjectId( m.getTags().get(0).getId() ) );	
					}
				}
				q.addCriteria(
					new Criteria().orOperator(
						Criteria.where("systemTags.$id").in(catObjList),
						Criteria.where("title").regex(searchTxt,"i")
					)
				);	
			}
			else{
				q.addCriteria(Criteria.where("title").regex(searchTxt,"i"));
			}
		}
		if (discussTypeArray != null && discussTypeArray.size() > 0) {
			q.addCriteria(Criteria.where((String) "discussType").in(
					discussTypeArray));
		}
		if (null != tagIds && tagIds.size() > 0) {
			q.addCriteria(Criteria.where("systemTags.$id").in(tagIds));
		}
		if (null != isFeatured) {
			q.addCriteria(Criteria.where("isFeatured").is(isFeatured));
		}
		if (null != isPromotion) {
			q.addCriteria(Criteria.where("isPromotion").is(isPromotion));
		}
		if (null != userId) {
			q.addCriteria(Criteria.where("userId").is(userId));
		}
		
		return q;
	}

	@Override
	public long getCount(String searchTxt, List<String> discussTypeArray, List<ObjectId> tagIds,
			String userId, Boolean isFeatured, Boolean isPromotion) {
		long count = 0;
		Query query = new Query();
		query = getQuery(query, searchTxt, discussTypeArray, tagIds, userId, isFeatured,
				isPromotion);
		query.addCriteria(Criteria.where("status").is(
				DiscussConstants.DISCUSS_STATUS_ACTIVE));
		count = this.mongoTemplate.count(query, Discuss.class);

		return count;
	}

}