package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.repository.DiscussRepository;
import com.beautifulyears.repository.EventRepository;
import com.beautifulyears.repository.ProductRepository;
import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussResponse;
import com.beautifulyears.rest.response.EventResponse;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.rest.response.ProductResponse;
import com.beautifulyears.rest.response.UserProfileResponse;
import com.beautifulyears.rest.response.DiscussResponse.DiscussPage;
import com.beautifulyears.rest.response.EventResponse.EventPage;
import com.beautifulyears.rest.response.ProductResponse.ProductPage;
import com.beautifulyears.rest.response.UserProfileResponse.UserProfilePage;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.UserProfilePrivacyHandler;
import com.beautifulyears.util.Util;

/**
 * The REST based service for managing "discuss"
 * 
 * @author Pulkit
 *
 */
@Controller
@RequestMapping(value = { "/homesearch" })
public class HomeSearchController {
	private DiscussRepository discussRepo;
	private EventRepository eventRepo;
	private ProductRepository productRepo;
	private UserProfileRepository userProfileRepository;

	private MongoTemplate mongoTemplate;

	private class SearchSummary {
		public DiscussPage discussPage;
		public ProductPage productPage;
		public EventPage eventPage;
		public String servicePage;
		public UserProfilePage expertPage;

		public SearchSummary(DiscussPage discussPage, ProductPage productPage, EventPage eventPage, String servicePage,
				UserProfilePage expertPage) {
			this.discussPage = discussPage;
			this.productPage = productPage;
			this.eventPage = eventPage;
			this.servicePage = servicePage;
			this.expertPage = expertPage;
		}
	}

	@Autowired
	public HomeSearchController(DiscussRepository discussRepo, EventRepository eventRepo, ProductRepository productRepo,
			UserProfileRepository userProfileRepository, MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		this.discussRepo = discussRepo;
		this.eventRepo = eventRepo;
		this.productRepo = productRepo;
		this.userProfileRepository = userProfileRepository;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(@RequestParam(value = "term", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "lastModifiedAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {

		User currentUser = Util.getSessionUser(request);
		ProductPage productPage = this.getProductPage(searchTxt, "", sort, dir, pageIndex, pageSize, currentUser);
		DiscussPage discussPage = this.getDiscussPage("P", searchTxt, null, null, null, sort, dir, pageIndex, pageSize,
				null, currentUser);
		EventPage eventPage = this.getEventPage(searchTxt, 0, -1, null, sort, dir, pageIndex, pageSize, currentUser);
		String ServicePage = this.getServicePage(searchTxt, sort, dir, pageIndex, pageSize, request);
		UserProfilePage expertPage = this.getExperts(searchTxt, sort, dir, pageIndex, pageSize);

		return BYGenericResponseHandler
				.getResponse(new SearchSummary(discussPage, productPage, eventPage, ServicePage, expertPage));
	}

	public UserProfilePage getExperts(@RequestParam(value = "term", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize) throws Exception {
		UserProfilePage userProfilePage = null;
		// List<String> filterCriteria = new ArrayList<String>();
		// filterCriteria.add("experties = " + experties);
		// filterCriteria.add("page = " + pageIndex);
		// filterCriteria.add("size = " + pageSize);
		// filterCriteria.add("sort = " + sort);
		// filterCriteria.add("dir = " + dir);
		Integer[] userTypes = { UserTypes.ASK_EXPERT };

		try {
			/* setting page and sort criteria */
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}
			List<String> fields = null;

			// List<String> fields = new ArrayList<String>();
			// fields.add("userId");
			// fields.add("age");
			// fields.add("workTitle");
			// fields.add("experties");
			// fields.add("userTypes");
			// fields.add("basicProfileInfo");

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			userProfilePage = UserProfileResponse.getPage(userProfileRepository.getServiceProvidersByFilterCriteria(
					searchTxt, userTypes, null, null, null, null, pageable, fields,null), null);
			if (userProfilePage.getContent().size() > 0) {
			}

		} catch (Exception e) {
			Util.handleException(e);
		}
		return userProfilePage;
	}

	public DiscussPage getDiscussPage(String discussType, String searchTxt, String userId, Boolean isFeatured,
			Boolean isPromotion, String sort, int dir, int pageIndex, int pageSize, List<String> tags, User currentUser)
			throws Exception {

		PageImpl<Discuss> page = null;
		List<ObjectId> tagIds = new ArrayList<ObjectId>();
		DiscussPage discussPage = null;
		try {
			List<String> discussTypeArray = new ArrayList<String>();
			if (null == discussType) {
				// discussTypeArray.add("A");
				discussTypeArray.add("Q");
				discussTypeArray.add("P");
			} else {
				discussTypeArray.add(discussType);
			}

			if (null != tags) {
				for (String tagId : tags) {
					tagIds.add(new ObjectId(tagId));
				}
			}

			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = discussRepo.getPage(searchTxt, discussTypeArray, tagIds, userId, isFeatured, isPromotion, pageable);
			discussPage = DiscussResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return discussPage;
	}

	public EventPage getEventPage(String searchTxt, Integer eventType, Integer pastEvents, Long startDatetime,
			String sort, int dir, int pageIndex, int pageSize, User currentUser) throws Exception {
		PageImpl<Event> page = null;
		EventPage eventPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = eventRepo.getPage(searchTxt, eventType, startDatetime, pastEvents, pageable);
			eventPage = EventResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return eventPage;
	}

	public ProductPage getProductPage(String searchTxt, String productCategory, String sort, int dir, int pageIndex,
			int pageSize, User currentUser) throws Exception {
		PageImpl<Product> page = null;
		ProductPage productPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = productRepo.getPage(searchTxt, productCategory, pageable);
			productPage = ProductResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return productPage;
	}

	public String getServicePage(String term, String sort, int dir, int pageIndex, int pageSize,
			HttpServletRequest request) throws Exception {
		List<Integer> serviceTypes = new ArrayList<Integer>();
		serviceTypes.add(UserTypes.INDIVIDUAL_PROFESSIONAL);
		serviceTypes.add(UserTypes.INSTITUTION_NGO);
		serviceTypes.add(UserTypes.INSTITUTION_BRANCH);
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("term = " + term);
		filterCriteria.add("sort = " + sort);
		filterCriteria.add("dir = " + dir);
		filterCriteria.add("pageIndex = " + Integer.toString(pageIndex));
		filterCriteria.add("pageSize = " + Integer.toString(pageSize));

		LoggerUtil.logEntry();
		JSONObject response = new JSONObject();
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);

			// TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(term);

			// Query query = TextQuery.queryText(criteria).sortByScore();
			Query query = new Query();

			query.addCriteria(
				new Criteria().orOperator(
					Criteria.where("basicProfileInfo.firstName").regex(term,"i")
				)
			);
			query.with(pageable);

			query.addCriteria(Criteria.where("userTypes").in(serviceTypes));
			query.addCriteria(
					Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));

			List<UserProfile> profiles = this.mongoTemplate.find(query, UserProfile.class);

			JSONObject justDailSearchResponse = SearchController.getJustDialSearchServicePage(pageIndex, 50, term,
					request);

			JSONArray JDresult = null;
			// JSONArray JDresult = justDailSearchResponse.getJSONArray("services");
			JSONArray DbserviceList = new JSONArray(profiles);
			for (int i = 0; i < DbserviceList.length(); i++) {
				JSONObject jsonDBObject = DbserviceList.getJSONObject(i);
				JSONArray totReviews = jsonDBObject.getJSONArray("reviewedBy");
				jsonDBObject.put("reviewCount", totReviews.length());
				Integer ratingPercent = jsonDBObject.getInt("aggrRatingPercentage");
				jsonDBObject.put("ratingPercentage", ratingPercent);
				DbserviceList.put(i, jsonDBObject);
			}
			// JD services
			if (justDailSearchResponse != null && justDailSearchResponse.length() > 0) {
				
				JDresult = justDailSearchResponse.getJSONArray("services");

				for (int i = 0; i < JDresult.length(); i++) {
					JSONObject jsonObject = JDresult.getJSONObject(i);
					String jdRating = jsonObject.getString("compRating");
					if (jdRating.equals("")) {
						jdRating = "0";
					}
					
					jsonObject.put("ratingPercentage", UserProfileController.getDbServiceRating(Math.round(Float.parseFloat(jdRating))));
					DbserviceList.put(jsonObject);
				}
			}
			JSONArray sortedArray = UserProfileController.sortJsonArray("ratingPercentage", DbserviceList);

			long total = this.mongoTemplate.count(query, UserProfile.class);
			if (justDailSearchResponse != null && justDailSearchResponse.length() > 0) {
				total += JDresult.length();
			}
			response.put("total", total);
			response.put("pageIndex", pageIndex);
			response.put("content", sortedArray);

			// PageImpl<UserProfile> storyPage = new PageImpl<UserProfile>(profiles,
			// pageable, total);

			// profilePage = UserProfileResponse.getPage(storyPage, currentUser);

		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "search services", null, null, null, null, term, filterCriteria,
				"search services for term = " + term, "SEARCH");
		// return BYGenericResponseHandler.getResponse(profilePage);
		return response.toString();
	}

	// public JSONObject getAllServiceList(String city, List<String> tags, int page,
	// int size, Boolean isFeatured,
	// String sort, int dir, User currentUser) throws Exception {
	// List<String> filterCriteria = new ArrayList<String>();
	// filterCriteria.add("page = " + page);
	// filterCriteria.add("size = " + size);
	// filterCriteria.add("sort = " + sort);
	// filterCriteria.add("dir = " + dir);
	// filterCriteria.add("tags = " + tags);
	// filterCriteria.add("isFeatured = " + isFeatured);
	// filterCriteria.add("city = " + city);

	// Integer[] userTypes = { UserTypes.INSTITUTION_HOUSING,
	// UserTypes.INSTITUTION_BRANCH,
	// UserTypes.INSTITUTION_PRODUCTS, UserTypes.INSTITUTION_NGO,
	// UserTypes.INDIVIDUAL_PROFESSIONAL,
	// UserTypes.ASK_EXPERT };

	// String JdsearchTerms = "care hospital clinics nursing home";
	// LoggerUtil.logEntry();
	// List<ObjectId> tagIds = new ArrayList<ObjectId>();

	// UserProfileResponse.UserProfilePage profilePage = null;
	// JSONObject response = new JSONObject();
	// try {
	// if (null != tags) {
	// for (String tagId : tags) {
	// tagIds.add(new ObjectId(tagId));
	// }
	// }

	// /* setting page and sort criteria */
	// Direction sortDirection = Direction.DESC;
	// if (dir != 0) {
	// sortDirection = Direction.ASC;
	// }

	// Pageable pageable = new PageRequest(page, size, sortDirection, sort);
	// List<String> fields = new ArrayList<String>();
	// fields = UserProfilePrivacyHandler.getPublicFields(-1);
	// profilePage = UserProfileResponse.getPage(userProfileRepository
	// .getServiceProvidersByFilterCriteria(null, userTypes, city, tagIds,
	// isFeatured,
	// null, pageable, fields), currentUser);

	// JSONObject justDailSearchResponse =
	// SearchController.getJustDialSearchServicePage(page, size, JdsearchTerms,
	// null);
	// JSONArray JDresult = justDailSearchResponse.getJSONArray("services");
	// JSONArray DbserviceList = new JSONArray(profilePage.getContent());
	// for (int i = 0; i < JDresult.length(); i++) {
	// JSONObject jsonObject = JDresult.getJSONObject(i);
	// String totReviews = jsonObject.getString("totalReviews");
	// if (totReviews.equals("")) {
	// totReviews = "0";
	// }
	// jsonObject.put("reviewCount", Integer.parseInt(totReviews));
	// DbserviceList.put(jsonObject);
	// }

	// JSONArray sortedArray = sortJsonArray("reviewCount", DbserviceList);

	// long total = profilePage.getTotal() + 50;
	// response.put("total", total);
	// response.put("pageIndex", profilePage.getNumber());
	// response.put("data", sortedArray);
	// } catch (Exception e) {
	// Util.handleException(e);
	// }
	// return response;
	// }

	// public static JSONArray sortJsonArray(String field, JSONArray array) {
	// 	JSONArray sortedJsonArray = new JSONArray();

	// 	List<JSONObject> jsonValues = new ArrayList<JSONObject>();
	// 	for (int i = 0; i < array.length(); i++) {
	// 		jsonValues.add(array.getJSONObject(i));
	// 	}
	// 	Collections.sort(jsonValues, new Comparator<JSONObject>() {
	// 		// You can change "Name" with "ID" if you want to sort by ID
	// 		private final String KEY_NAME = field;

	// 		@Override
	// 		public int compare(JSONObject a, JSONObject b) {
	// 			Integer valA = 0;
	// 			Integer valB = 0;
	// 			try {
	// 				valA = a.getInt(KEY_NAME);
	// 				valB = b.getInt(KEY_NAME);
	// 			} catch (JSONException e) {
	// 				// do something
	// 				throw new RuntimeException("ERROR in sorting data. " + e);
	// 			}

	// 			return -valA.compareTo(valB);
	// 			// if you want to change the sort order, simply use the following:
	// 			// return -valA.compareTo(valB);
	// 		}
	// 	});

	// 	for (int i = 0; i < array.length(); i++) {
	// 		sortedJsonArray.put(jsonValues.get(i));
	// 	}
	// 	return sortedJsonArray;
	// }
}
