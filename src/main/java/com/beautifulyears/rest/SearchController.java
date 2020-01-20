package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
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
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.domain.JustdialToken;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.justdial.JustDialHandler;
import com.beautifulyears.repository.JustDialTokenRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussResponse;
import com.beautifulyears.rest.response.DiscussResponse.DiscussPage;
import com.beautifulyears.rest.response.HousingResponse;
import com.beautifulyears.rest.response.HousingResponse.HousingPage;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.rest.response.UserProfileResponse;
import com.beautifulyears.rest.response.UserProfileResponse.UserProfilePage;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;

/**
 * The REST based service for managing "search"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/search" })
public class SearchController {
	private MongoTemplate mongoTemplate;
	private static JustDialTokenRepository justDialTokenRepository;

	@Autowired
	public SearchController(MongoTemplate mongoTemplate, JustDialTokenRepository justDialTokenRepository) {
		SearchController.justDialTokenRepository = justDialTokenRepository;
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/discussPageSearch" }, produces = { "application/json" })
	@ResponseBody
	public Object getDiscussPage(@RequestParam(value = "term", required = true) String term,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		DiscussPage discussPage = null;
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("term = " + term);
		filterCriteria.add("sort = " + sort);
		filterCriteria.add("dir = " + dir);
		filterCriteria.add("pageIndex = " + Integer.toString(pageIndex));
		filterCriteria.add("pageSize = " + Integer.toString(pageSize));
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			List<String> discussTypeArray = new ArrayList<String>();
			discussTypeArray.add("Q");
			discussTypeArray.add("P");

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);

			TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(term).matchingAny(term);

			Query query = TextQuery.queryText(criteria).sortByScore();
			query.addCriteria(Criteria.where("status").is(DiscussConstants.DISCUSS_STATUS_ACTIVE));
			query.with(pageable);
			query.addCriteria(Criteria.where((String) "discussType").in(discussTypeArray));

			System.out.println("search term  = " + term);

			List<Discuss> stories = this.mongoTemplate.find(query, Discuss.class);

			long total = this.mongoTemplate.count(query, Discuss.class);
			PageImpl<Discuss> storyPage = new PageImpl<Discuss>(stories, pageable, total);

			discussPage = DiscussResponse.getPage(storyPage, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "search discuss", null, null, null, null, term, filterCriteria,
				"search discuss for term = " + term, "SEARCH");
		return BYGenericResponseHandler.getResponse(discussPage);
	}

	/**
	 * Search Service (autocomplete)
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/servicePageSearch" }, produces = { "application/json" })
	@ResponseBody
	public Object getServicePage(@RequestParam(value = "term", required = true) String term,
			@RequestParam(value = "catid", required = false, defaultValue = "0") int catId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
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
		User currentUser = Util.getSessionUser(request);
		UserProfilePage profilePage = null;
		JSONObject response = new JSONObject();
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);

			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(term);

			Query query = TextQuery.queryText(criteria).sortByScore();
			query.with(pageable);

			query.addCriteria(Criteria.where("userTypes").in(serviceTypes));
			query.addCriteria(
					Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));

			List<UserProfile> profiles = this.mongoTemplate.find(query, UserProfile.class);

			JSONObject justDailSearchResponse = new JSONObject();

			if (catId != 0) {
				justDailSearchResponse = getJustDialCategoryServices(term, catId, pageSize, pageIndex, request);
			} else {
				justDailSearchResponse = getJustDialSearchServicePage(pageIndex, 50, term, request);
			}
			JSONArray JDresult=null;
			JSONArray DbserviceList = new JSONArray(profiles);
			for (int i = 0; i < DbserviceList.length(); i++) {
				JSONObject jsonDBObject = DbserviceList.getJSONObject(i);
				JSONArray totReviews = jsonDBObject.getJSONArray("reviewedBy");
				jsonDBObject.put("reviewCount", totReviews.length());
				DbserviceList.put(i, jsonDBObject);
			}
			//JD services
			if (justDailSearchResponse != null && justDailSearchResponse.length() > 0) {

				JDresult = justDailSearchResponse.getJSONArray("services");

				for (int i = 0; i < JDresult.length(); i++) {
					JSONObject jsonObject = JDresult.getJSONObject(i);
					String totReviews = jsonObject.getString("totalReviews");
					if (totReviews.equals("")) {
						totReviews = "0";
					}
					jsonObject.put("reviewCount", Integer.parseInt(totReviews));
					DbserviceList.put(jsonObject);
				}
			}
			//
			JSONArray sortedArray = UserProfileController.sortJsonArray("reviewCount", DbserviceList);

			long total = this.mongoTemplate.count(query, UserProfile.class);
			if (justDailSearchResponse != null && justDailSearchResponse.length() > 0) {
			
				total += JDresult.length();
			}
			response.put("total", total);
			response.put("pageIndex", pageIndex);
			response.put("data", sortedArray);

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

	@RequestMapping(method = { RequestMethod.GET }, value = { "/housingPageSearch" }, produces = { "application/json" })
	@ResponseBody
	public Object getHousingPage(@RequestParam(value = "term", required = true) String term,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {

		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		HousingPage housingPage = null;
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("term = " + term);
		filterCriteria.add("sort = " + sort);
		filterCriteria.add("dir = " + dir);
		filterCriteria.add("pageIndex = " + Integer.toString(pageIndex));
		filterCriteria.add("pageSize = " + Integer.toString(pageSize));
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);

			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(term);

			Query query = TextQuery.queryText(criteria).sortByScore();
			query.with(pageable);
			query.addCriteria(
					Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));

			List<HousingFacility> housings = this.mongoTemplate.find(query, HousingFacility.class);

			long total = this.mongoTemplate.count(query, HousingFacility.class);
			PageImpl<HousingFacility> page = new PageImpl<HousingFacility>(housings, pageable, total);

			housingPage = HousingResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "search housing", null, null, null, null, term, filterCriteria,
				"search housing for term = " + term, "SEARCH");
		return BYGenericResponseHandler.getResponse(housingPage);
	}

	/**
	 * Get all service
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/justdialService" }, produces = { "application/json" })
	@ResponseBody
	public Object getJustDialServicePage(
			// String category, Integer catID, Integer max, int pageNo
			@RequestParam(value = "category", required = true) String category,
			@RequestParam(value = "catID", required = true) int catID,
			@RequestParam(value = "max", required = false, defaultValue = "10") int max,
			@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
			HttpServletRequest request) throws Exception {
		JSONObject response = new JSONObject();
		try {

			JustdialToken JDtoken = null;
			List<JustdialToken> JDtokenList = null;
			JDtokenList = justDialTokenRepository.findAll();
			if (JDtokenList.size() > 0) {
				JDtoken = JDtokenList.get(0);
			}
			// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date cuurentdate = new Date();
			// Date expireDate = new Date(JDtoken.getExpires()*1000);
			// int comp = cuurentdate.compareTo(expireDate);
			JustDialHandler JDHandler = new JustDialHandler();
			Long currentTime = cuurentdate.getTime() / 1000;

			if (null == JDtoken || currentTime >= JDtoken.getExpires()) {
				JustdialToken JDNewtoken = JDHandler.getNewToken();
				JDtoken.setToken(JDNewtoken.getToken());
				JDtoken.setExpires(JDNewtoken.getExpires());
				justDialTokenRepository.save(JDtoken);
			}

			JSONObject JDResponse = JDHandler.getServiceList(JDtoken.getToken(), category, catID, max, pageNo);
			JSONObject resultsObject = JDResponse.getJSONObject("results");
			JSONArray columns = resultsObject.getJSONArray("columns");
			JSONArray dataList = resultsObject.getJSONArray("data");
			JSONArray newDataList = new JSONArray();
			for (int i = 0; i < dataList.length(); i++) {
				JSONArray dataInfoList = dataList.getJSONArray(i);
				JSONObject dataInfoMap = new JSONObject();
				for (int j = 0; j < dataInfoList.length(); j++) {
					dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
				}
				newDataList.put(dataInfoMap);
			}
			response.put("services", newDataList);
			response.put("JDResponse", JDResponse);
		} catch (Exception e) {
			// throw e;
			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// Util.logStats(mongoTemplate, request, "search services", null, null, null,
		// null, term, filterCriteria,
		// "search services for term = " + term, "SEARCH");
		// String newResponse = response.toString();
		// return BYGenericResponseHandler.getResponse(response.toString());
		return response.toString();
	}

	/**
	 * JD service detail
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/justdialServiceDetail" }, produces = {
			"application/json" })
	@ResponseBody
	public Object getJustDialServiceDetailPage(
			// String category, Integer catID, Integer max, int pageNo
			@RequestParam(value = "service", required = true) String service,
			@RequestParam(value = "docID", required = true) String docID, HttpServletRequest request) throws Exception {
		String response = null;
		try {

			JustdialToken JDtoken = null;
			List<JustdialToken> JDtokenList = null;
			JDtokenList = justDialTokenRepository.findAll();
			if (JDtokenList.size() > 0) {
				JDtoken = JDtokenList.get(0);
			}
			// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date cuurentdate = new Date();
			// Date expireDate = new Date(JDtoken.getExpires()*1000);
			// int comp = cuurentdate.compareTo(expireDate);
			JustDialHandler JDHandler = new JustDialHandler();
			Long currentTime = cuurentdate.getTime() / 1000;

			if (null == JDtoken || currentTime >= JDtoken.getExpires()) {
				JustdialToken JDNewtoken = JDHandler.getNewToken();
				JDtoken.setToken(JDNewtoken.getToken());
				JDtoken.setExpires(JDNewtoken.getExpires());
				justDialTokenRepository.save(JDtoken);
			}

			response = JDHandler.getServiceDetail(JDtoken.getToken(), service, docID);

		} catch (Exception e) {
			// throw e;
			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// Util.logStats(mongoTemplate, request, "search services", null, null, null,
		// null, term, filterCriteria,
		// "search services for term = " + term, "SEARCH");
		// String newResponse = response.toString();
		// return BYGenericResponseHandler.getResponse(response);
		return response;
	}

	/**
	 * JD service search
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/justdialSearchService" }, produces = {
			"application/json" })
	@ResponseBody
	public static JSONObject getJustDialSearchServicePage(@RequestParam(value = "pageNo", required = true) int pageNo,
			@RequestParam(value = "max", required = false, defaultValue = "10") int max,
			@RequestParam(value = "search", required = true) String search, HttpServletRequest request)
			throws Exception {
		JSONObject response = new JSONObject();
		try {

			JustdialToken JDtoken = null;
			List<JustdialToken> JDtokenList = null;
			JDtokenList = justDialTokenRepository.findAll();
			if (JDtokenList.size() > 0) {
				JDtoken = JDtokenList.get(0);
			}
			// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date cuurentdate = new Date();
			// Date expireDate = new Date(JDtoken.getExpires()*1000);
			// int comp = cuurentdate.compareTo(expireDate);
			JustDialHandler JDHandler = new JustDialHandler();
			Long currentTime = cuurentdate.getTime() / 1000;

			if (null == JDtoken || currentTime >= JDtoken.getExpires()) {
				JustdialToken JDNewtoken = JDHandler.getNewToken();
				JDtoken.setToken(JDNewtoken.getToken());
				JDtoken.setExpires(JDNewtoken.getExpires());
				justDialTokenRepository.save(JDtoken);
			}

			JSONObject JDResponse = JDHandler.getSearchServiceList(JDtoken.getToken(), search, max, pageNo);
			if (JDResponse != null) {

				JSONObject resultsObject = JDResponse.getJSONObject("results");
				JSONArray columns = resultsObject.getJSONArray("columns");
				JSONArray dataList = resultsObject.getJSONArray("data");
				JSONArray newDataList = new JSONArray();
				for (int i = 0; i < dataList.length(); i++) {
					JSONArray dataInfoList = dataList.getJSONArray(i);
					JSONObject dataInfoMap = new JSONObject();
					for (int j = 0; j < dataInfoList.length(); j++) {
						dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
					}
					newDataList.put(dataInfoMap);
				}
				response.put("services", newDataList);
			} else {
				response = null;
			}
			// response.put("JDResponse", JDResponse);
		} catch (Exception e) {
			throw e;
			// Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// Util.logStats(mongoTemplate, request, "search services", null, null, null,
		// null, term, filterCriteria,
		// "search services for term = " + term, "SEARCH");
		// String newResponse = response.toString();
		// return BYGenericResponseHandler.getResponse(response.toString());
		return response;
	}

	/**
	 * Search JD services by categories
	 * 
	 * @param category
	 * @param catID
	 * @param max
	 * @param pageNo
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject getJustDialCategoryServices(
			// String category, Integer catID, Integer max, int pageNo
			@RequestParam(value = "category", required = true) String category,
			@RequestParam(value = "catID", required = true) int catID,
			@RequestParam(value = "max", required = false, defaultValue = "10") int max,
			@RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
			HttpServletRequest request) throws Exception {
		JSONObject response = new JSONObject();
		try {

			JustdialToken JDtoken = null;
			List<JustdialToken> JDtokenList = null;
			JDtokenList = justDialTokenRepository.findAll();
			if (JDtokenList.size() > 0) {
				JDtoken = JDtokenList.get(0);
			}
			// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date cuurentdate = new Date();
			// Date expireDate = new Date(JDtoken.getExpires()*1000);
			// int comp = cuurentdate.compareTo(expireDate);
			JustDialHandler JDHandler = new JustDialHandler();
			Long currentTime = cuurentdate.getTime() / 1000;

			if (null == JDtoken || currentTime >= JDtoken.getExpires()) {
				JustdialToken JDNewtoken = JDHandler.getNewToken();
				JDtoken.setToken(JDNewtoken.getToken());
				JDtoken.setExpires(JDNewtoken.getExpires());
				justDialTokenRepository.save(JDtoken);
			}

			JSONObject JDResponse = JDHandler.getServiceList(JDtoken.getToken(), category, catID, max, pageNo);
			JSONObject resultsObject = JDResponse.getJSONObject("results");
			JSONArray columns = resultsObject.getJSONArray("columns");
			JSONArray dataList = resultsObject.getJSONArray("data");
			JSONArray newDataList = new JSONArray();
			for (int i = 0; i < dataList.length(); i++) {
				JSONArray dataInfoList = dataList.getJSONArray(i);
				JSONObject dataInfoMap = new JSONObject();
				for (int j = 0; j < dataInfoList.length(); j++) {
					dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
				}
				newDataList.put(dataInfoMap);
			}
			response.put("services", newDataList);
			// response.put("JDResponse", JDResponse);
		} catch (Exception e) {
			// throw e;
			// Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// Util.logStats(mongoTemplate, request, "search services", null, null, null,
		// null, term, filterCriteria,
		// "search services for term = " + term, "SEARCH");
		// String newResponse = response.toString();
		// return BYGenericResponseHandler.getResponse(response.toString());
		return response;
	}

	/**
	 * JD categories list
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/justdailCategories" }, produces = {
			"application/json" })
	@ResponseBody
	public Object getJustDialCategories(HttpServletRequest request) throws Exception {
		Object response = null;
		try {

			JustdialToken JDtoken = null;
			List<JustdialToken> JDtokenList = null;
			JDtokenList = justDialTokenRepository.findAll();
			if (JDtokenList.size() > 0) {
				JDtoken = JDtokenList.get(0);
			}
			// TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
			Date cuurentdate = new Date();
			// Date expireDate = new Date(JDtoken.getExpires()*1000);
			// int comp = cuurentdate.compareTo(expireDate);
			JustDialHandler JDHandler = new JustDialHandler();
			Long currentTime = cuurentdate.getTime() / 1000;

			if (null == JDtoken || currentTime >= JDtoken.getExpires()) {
				JustdialToken JDNewtoken = JDHandler.getNewToken();
				JDtoken.setToken(JDNewtoken.getToken());
				JDtoken.setExpires(JDNewtoken.getExpires());
				justDialTokenRepository.save(JDtoken);
			}

			response = JDHandler.getServiceCategories(JDtoken.getToken());

		} catch (Exception e) {
			// throw e;
			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// Util.logStats(mongoTemplate, request, "search services", null, null, null,
		// null, term, filterCriteria,
		// "search services for term = " + term, "SEARCH");
		// String newResponse = response.toString();
		// return BYGenericResponseHandler.getResponse(response);
		return response.toString();
	}

}
