package com.beautifulyears.repository.custom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.ServiceCategoriesMapping;
import com.beautifulyears.domain.ServiceSubCategoryMapping;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.rest.response.PageImpl;

public class UserProfileRepositoryImpl implements UserProfileRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public PageImpl<UserProfile> getServiceProvidersByFilterCriteria(String name, Object[] userTypes, String city,
			List<ObjectId> tagIds, Boolean isFeatured, List<ObjectId> experties, Pageable page, List<String> fields,
			List<String> catId, String source, Boolean verified, Boolean searchBynameOrCatid) {
		List<UserProfile> userProfileList = null;
		Query q = new Query();

		q = getQuery(q, userTypes, city, tagIds, isFeatured, experties, name, catId, source, verified,
				searchBynameOrCatid);

		q.with(page);

		userProfileList = mongoTemplate.find(q, UserProfile.class);

		long total = this.mongoTemplate.count(q, UserProfile.class);
		PageImpl<UserProfile> userProfilePage = new PageImpl<UserProfile>(userProfileList, page, total);

		return userProfilePage;
	}

	@Override
	public long getServiceProvidersByFilterCriteriaCount(String name, Object[] userTypes, String city,
			List<ObjectId> tagIds, Boolean isFeatured, List<ObjectId> experties, List<String> catId, String source,
			Pageable page, Boolean verified, Boolean searchBynameOrCatid) {
		// List<UserProfile> userProfileList = null;
		Query q = new Query();
		q = getQuery(q, userTypes, city, tagIds, isFeatured, experties, name, catId, source, verified,
				searchBynameOrCatid);
		if (page != null) {
			q.with(page);
		}
		// userProfileList = mongoTemplate.find(q, UserProfile.class);

		long total = this.mongoTemplate.count(q, UserProfile.class);

		return total;
	}

	private Query getQuery(Query q, Object[] userTypes, String city, List<ObjectId> tagIds, Boolean isFeatured,
			List<ObjectId> experties, String name, List<String> catId, String source, Boolean verified,
			Boolean searchBynameOrCatid) {

		q.addCriteria(Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));
		if (null != userTypes && userTypes.length > 0) {
			q.addCriteria(Criteria.where((String) "userTypes").in(userTypes));
		}

		if (null != isFeatured) {
			q.addCriteria(Criteria.where("isFeatured").is(isFeatured));
		}

		if (null != verified && false != verified) {
			q.addCriteria(Criteria.where("verified").is(verified));
		}

		if (null != tagIds && tagIds.size() > 0) {
			q.addCriteria(Criteria.where("systemTags.$id").in(tagIds));
		}

		if (null != experties && experties.size() > 0) {
			q.addCriteria(Criteria.where("experties.$id").in(experties));
		}

		if (city != null) {
			Criteria criteria = new Criteria();
			criteria.orOperator(Criteria.where("basicProfileInfo.primaryUserAddress.city").regex(city, "i"), Criteria
					.where("basicProfileInfo.otherAddresses").elemMatch(Criteria.where("city").regex(city, "i")));

			q.addCriteria(criteria);
		}

		if (null != name && "" != name) {
			// get category list
			List<AskCategory> catList = null;
			Query query = new Query();
			query.addCriteria(Criteria.where("name").regex(name, "i"));

			catList = this.mongoTemplate.find(query, AskCategory.class);
			List<String> catStrList = new ArrayList<String>();
			if (catList != null && catList.size() > 0) {
				Iterator<AskCategory> catListIterator = catList.iterator();
				while (catListIterator.hasNext()) {
					catStrList.add(catListIterator.next().getId());
				}
				q.addCriteria(new Criteria().orOperator(Criteria.where("experties").in(catStrList),
						Criteria.where("basicProfileInfo.firstName").regex(name, "i")));
			} else if (searchBynameOrCatid) {
				// Services search by name starts here.

				Query categoryQuery = new Query();
				categoryQuery.addCriteria(Criteria.where("subCategories.category_name").regex(name, "i"));

				List<ServiceCategoriesMapping> searchCategories = mongoTemplate.find(categoryQuery,
						ServiceCategoriesMapping.class);
				if (searchCategories.size() > 0) {
					List<String> matchCategories = new ArrayList<String>();
					for (ServiceCategoriesMapping serviceCategory : searchCategories) {

						for (ServiceSubCategoryMapping subCategory : serviceCategory.getSubCategories()) {

							for (ServiceSubCategoryMapping.Source catSources : subCategory.getSource()) {
								if (subCategory.getName().toLowerCase().contains(name.toLowerCase())) {
									matchCategories.add(catSources.getCatid());
								}
							}

						}
					}

					q.addCriteria(
							new Criteria().orOperator(Criteria.where("serviceProviderInfo.catid").in(matchCategories),
									Criteria.where("basicProfileInfo.firstName").regex(name, "i")));

				} else {
					q.addCriteria(Criteria.where("basicProfileInfo.firstName").regex(name, "i"));
				}

			} else {
				q.addCriteria(Criteria.where("basicProfileInfo.firstName").regex(name, "i"));
			}

		} else {
			q.addCriteria(Criteria.where("basicProfileInfo.firstName").exists(true));
		}

		if (null != catId && catId.size() > 0) {
			q.addCriteria(Criteria.where("serviceProviderInfo.catid").in(catId));
		}

		if (null != source && "" != source) {
			q.addCriteria(Criteria.where("serviceProviderInfo.source").is(source));
		}
		return q;
	}

	@Override
	public PageImpl<UserProfile> findAllUserProfiles(Pageable pageable) {
		Query q = new Query();
		q.addCriteria(Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));
		List<UserProfile> userProfileList = mongoTemplate.find(q, UserProfile.class);
		long total = userProfileList.size();
		PageImpl<UserProfile> userProfilePage = new PageImpl<UserProfile>(userProfileList, pageable, total);
		return userProfilePage;
	}

	@Override
	public UserProfile findByUserId(String userId) {
		UserProfile profile = null;
		Query q = new Query();
		q.addCriteria(Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));
		List<Integer> userTypes = new ArrayList<Integer>();
		userTypes.add(UserTypes.INSTITUTION_BRANCH);
		q.addCriteria(Criteria.where((String) "userTypes").not().in(userTypes).and("userId").is(userId));
		profile = mongoTemplate.findOne(q, UserProfile.class);
		return profile;
	}
}