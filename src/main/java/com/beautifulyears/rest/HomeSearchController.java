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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.User;
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
		public JSONObject servicePage;
		
		public SearchSummary(DiscussPage discussPage, ProductPage productPage, EventPage eventPage , JSONObject servicePage) {
			this.discussPage = discussPage;
			this.productPage = productPage;
			this.eventPage = eventPage;
			this.servicePage = servicePage;
		}
	}
	
	@Autowired
	public HomeSearchController(
			DiscussRepository discussRepo,
			EventRepository eventRepo,
			ProductRepository productRepo,
			UserProfileRepository userProfileRepository,
			MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		this.discussRepo = discussRepo;
		this.eventRepo = eventRepo;
		this.productRepo = productRepo;
		this.userProfileRepository = userProfileRepository;
	}
	
	@RequestMapping(method = { RequestMethod.GET }, value = { "/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "lastModifiedAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request
		) throws Exception {
		
		User currentUser = Util.getSessionUser(request);
		ProductPage productPage = this.getProductPage(searchTxt, "", sort, dir, pageIndex, pageSize, currentUser);
		DiscussPage discussPage = this.getDiscussPage("P", searchTxt, null, null, null, sort, dir, pageIndex, pageSize, null, currentUser);
		EventPage	eventPage = this.getEventPage(searchTxt, 0, -1, null, sort, dir, pageIndex, pageSize, currentUser);
		JSONObject ServicePage = this.getAllServiceList(null, null, pageIndex, pageSize, null, sort, dir, currentUser);
		
		return BYGenericResponseHandler.getResponse( new SearchSummary( discussPage, productPage, eventPage, ServicePage ) );
	}
	
	
	public DiscussPage getDiscussPage(String discussType, String searchTxt, String userId, Boolean isFeatured,
			Boolean isPromotion, String sort, int dir, int pageIndex, int pageSize, List<String> tags,
			User currentUser ) throws Exception {
		
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

			Pageable pageable = new PageRequest(pageIndex, pageSize,
					sortDirection, sort);
			page = discussRepo.getPage(searchTxt, discussTypeArray, tagIds, userId, isFeatured, isPromotion, pageable);
			discussPage = DiscussResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return discussPage;
	}
	
	public EventPage getEventPage(
			String searchTxt, Integer eventType, Integer pastEvents, Long startDatetime,
			String sort, int dir, int pageIndex, int pageSize, User currentUser ) throws Exception {
		PageImpl<Event> page = null;
		EventPage eventPage = null;
		try {	
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = eventRepo.getPage(searchTxt,eventType, startDatetime,pastEvents, pageable);
			eventPage = EventResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return eventPage;
	}
	
	public ProductPage getProductPage(
			String searchTxt, String productCategory,
			String sort, int dir, int pageIndex, int pageSize, User currentUser ) throws Exception {
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

	public JSONObject getAllServiceList(String city, List<String> tags, int page, int size, Boolean isFeatured,
			String sort, int dir, User currentUser) throws Exception {
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("page = " + page);
		filterCriteria.add("size = " + size);
		filterCriteria.add("sort = " + sort);
		filterCriteria.add("dir = " + dir);
		filterCriteria.add("tags = " + tags);
		filterCriteria.add("isFeatured = " + isFeatured);
		filterCriteria.add("city = " + city);

		Integer[] userTypes = { UserTypes.INSTITUTION_HOUSING, UserTypes.INSTITUTION_BRANCH,
				UserTypes.INSTITUTION_PRODUCTS, UserTypes.INSTITUTION_NGO, UserTypes.INDIVIDUAL_PROFESSIONAL, 
				UserTypes.ASK_EXPERT };

		String JdsearchTerms = "care hospital clinics nursing home";
		LoggerUtil.logEntry();
		List<ObjectId> tagIds = new ArrayList<ObjectId>();
		
		UserProfileResponse.UserProfilePage profilePage = null;
		JSONObject response = new JSONObject();
		try {
			if (null != tags) {
				for (String tagId : tags) {
					tagIds.add(new ObjectId(tagId));
				}
			}

			/* setting page and sort criteria */
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(page, size, sortDirection, sort);
			List<String> fields = new ArrayList<String>();
			fields = UserProfilePrivacyHandler.getPublicFields(-1);
			profilePage = UserProfileResponse.getPage(userProfileRepository
					.getServiceProvidersByFilterCriteria(userTypes, city, tagIds, isFeatured, null, pageable, fields), currentUser);

			JSONObject justDailSearchResponse = SearchController.getJustDialSearchServicePage(page, size, JdsearchTerms, null);
			JSONArray JDresult = justDailSearchResponse.getJSONArray("services");
			JSONArray DbserviceList = new JSONArray(profilePage.getContent());
			for (int i = 0; i < JDresult.length(); i++) {
				JSONObject jsonObject = JDresult.getJSONObject(i);
				String totReviews = jsonObject.getString("totalReviews");
				if (totReviews.equals("")) {
					totReviews = "0";
				}
				jsonObject.put("reviewCount", Integer.parseInt(totReviews));
				DbserviceList.put(jsonObject);
			}

			JSONArray sortedArray = sortJsonArray("reviewCount", DbserviceList);

			long total = profilePage.getTotal() + 50;
			response.put("total", total);
			response.put("pageIndex", profilePage.getNumber());
			response.put("data", sortedArray);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return response;
	}
	
	public static JSONArray sortJsonArray(String field, JSONArray array) {
		JSONArray sortedJsonArray = new JSONArray();

		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			jsonValues.add(array.getJSONObject(i));
		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private final String KEY_NAME = field;

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = new String();
				String valB = new String();

				try {
					valA = String.valueOf(a.get(KEY_NAME));
					valB = String.valueOf(b.get(KEY_NAME));
				} catch (JSONException e) {
					// do something
					throw new RuntimeException("ERROR in sorting data. " + e);
				}

				return -valA.compareTo(valB);
				// if you want to change the sort order, simply use the following:
				// return -valA.compareTo(valB);
			}
		});

		for (int i = 0; i < array.length(); i++) {
			sortedJsonArray.put(jsonValues.get(i));
		}
		return sortedJsonArray;
	}
}