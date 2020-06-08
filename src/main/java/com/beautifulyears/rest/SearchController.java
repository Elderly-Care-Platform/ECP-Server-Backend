package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.domain.JustDailServices;
import com.beautifulyears.domain.JustDailSetting;
import com.beautifulyears.domain.JustDialServicesLogs;
import com.beautifulyears.domain.JustdialToken;
import com.beautifulyears.domain.ServiceCategories;
import com.beautifulyears.domain.ServiceCategoriesMapping;
import com.beautifulyears.domain.ServiceSubCategory;
import com.beautifulyears.domain.ServiceSubCategoryMapping;
import com.beautifulyears.domain.ServicesStatus;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.justdial.JustDialHandler;
import com.beautifulyears.repository.JustDialSerivcesRepository;
import com.beautifulyears.repository.JustDialSettingsRepository;
import com.beautifulyears.repository.JustDialTokenRepository;
import com.beautifulyears.repository.ServiceCategoriesMappingRepository;
import com.beautifulyears.repository.ServiceCategoriesRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussResponse;
import com.beautifulyears.rest.response.DiscussResponse.DiscussPage;
import com.beautifulyears.rest.response.HousingResponse;
import com.beautifulyears.rest.response.HousingResponse.HousingPage;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.rest.response.UserProfileResponse.UserProfilePage;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

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
	private ServiceCategoriesRepository serviceCategoriesRepository;
	private JustDialSettingsRepository justDialSettingsRepository;
	private JustDialSerivcesRepository justDialSerivcesRepository;
	private ServiceCategoriesMappingRepository serviceCategoriesMappingRepository;

	@Autowired
	public SearchController(JustDialTokenRepository justDialTokenRepository,
			ServiceCategoriesRepository serviceCategoriesRepository,
			JustDialSettingsRepository justDialSettingsRepository,
			JustDialSerivcesRepository justDialSerivcesRepository,
			ServiceCategoriesMappingRepository serviceCategoriesMappingRepository, MongoTemplate mongoTemplate) {
		SearchController.justDialTokenRepository = justDialTokenRepository;
		this.mongoTemplate = mongoTemplate;
		this.serviceCategoriesRepository = serviceCategoriesRepository;
		this.justDialSettingsRepository = justDialSettingsRepository;
		this.justDialSerivcesRepository = justDialSerivcesRepository;
		this.serviceCategoriesMappingRepository = serviceCategoriesMappingRepository;
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
	@RequestMapping(method = { RequestMethod.POST }, value = { "/servicePageSearch" }, produces = {
			"application/json" })
	@ResponseBody
	public Object getServicePage(@RequestParam(value = "term", required = false) String term,
			@RequestParam(value = "catName", required = false) String catName,
			@RequestParam(value = "catid", required = false, defaultValue = "0") int catId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		List<Integer> serviceTypes = new ArrayList<Integer>();
		serviceTypes.add(UserTypes.INSTITUTION_SERVICES);
		// serviceTypes.add(UserTypes.INSTITUTION_NGO);
		// serviceTypes.add(UserTypes.INSTITUTION_BRANCH);
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

			String searchText = null;

			if (catName != null && term == null) {
				searchText = catName;
			} else if (term != null) {
				searchText = term;
			}

			// TextCriteria criteria =
			// TextCriteria.forDefaultLanguage().matchingAny(searchText);

			// Query query = TextQuery.queryText(criteria).sortByScore();
			Query query = new Query();

			query.addCriteria(
					new Criteria().orOperator(Criteria.where("basicProfileInfo.firstName").regex(searchText, "i")));
			query.with(pageable);

			query.addCriteria(Criteria.where("userTypes").in(serviceTypes));
			query.addCriteria(
					Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));

			List<UserProfile> profiles = this.mongoTemplate.find(query, UserProfile.class);

			JSONObject justDailSearchResponse = new JSONObject();

			if (catId != 0) {
				justDailSearchResponse = getJustDialCategoryServices(searchText, catId, pageSize, pageIndex, request);
			} else if (catName != null) {
				ServiceCategories dbCategory = null;
				JSONArray jsonarray = new JSONArray();
				dbCategory = this.serviceCategoriesRepository.findByName(catName);
				if (dbCategory != null) {
					// for (ServiceSubCategory subCategory : dbCategory.getSubCategories()) {
					// JSONObject jdCategoryService = new JSONObject();
					// jdCategoryService = getJustDialCategoryServices("",
					// Integer.parseInt(subCategory.getId()), 10,
					// 1, request);

					// for (int i = 0; i < jdCategoryService.getJSONArray("services").length(); i++)
					// {
					// JSONObject jsonObject =
					// jdCategoryService.getJSONArray("services").getJSONObject(i);
					// jsonarray.put(jsonObject);
					// }
					// }
					// justDailSearchResponse.put("services", jsonarray);

					List<CompletableFuture<JSONObject>> allFutures = new ArrayList<>();
					for (ServiceSubCategory subCategory : dbCategory.getSubCategories()) {
						allFutures.add(getAsyncJustDialCategoryServices("", Integer.parseInt(subCategory.getId()), 10,
								1, request));
					}
					CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
					System.out.println("response: " + allFutures);
					for (CompletableFuture<JSONObject> futureResult : allFutures) {
						JSONObject jdCategoryService = new JSONObject();
						jdCategoryService = futureResult.get();
						for (int i = 0; i < jdCategoryService.getJSONArray("services").length(); i++) {
							JSONObject jsonObject = jdCategoryService.getJSONArray("services").getJSONObject(i);
							jsonarray.put(jsonObject);
						}
					}
					justDailSearchResponse.put("services", jsonarray);
				}
			} else {
				justDailSearchResponse = getJustDialSearchServicePage(pageIndex, pageSize, searchText, request);
			}

			JSONArray JDresult = null;
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

					jsonObject.put("ratingPercentage",
							UserProfileController.getDbServiceRating(Math.round(Float.parseFloat(jdRating))));

					if (term != null && catName != null) {
						if (jsonObject.getString("name").toLowerCase().contains(term.toLowerCase())) {
							DbserviceList.put(jsonObject);
						}
						continue;
					}

					DbserviceList.put(jsonObject);
				}
			}
			//
			JSONArray sortedArray = UserProfileController.sortJsonArray("ratingPercentage", DbserviceList);

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

	/**
	 * Fetch and store JD Services
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/fetchJustDailServices" }, produces = {
			"application/json" })
	@ResponseBody
	public Object fetchJustDailServices(HttpServletRequest request) throws Exception {

		// Record service log
		JustDialServicesLogs JdServiceLog = new JustDialServicesLogs();
		JdServiceLog.setExecutionStart(new Date());
		HashMap<String, Integer> jdResponse = new HashMap<>();
		// Get record limit from setting
		try {
			List<JustDailSetting> Jdsettings = justDialSettingsRepository.findAll();
			Integer limit = Jdsettings.get(0).getLimit();
			if (Jdsettings == null || limit == 0 || limit == null) {
				JdServiceLog.setError(BYErrorCodes.NO_JD_SETTINGS.getMsg());
				JdServiceLog.setStatus(ServicesStatus.FAILURE);
				JdServiceLog.setExecutionEnd(new Date());
				mongoTemplate.save(JdServiceLog);
				throw new BYException(BYErrorCodes.NO_JD_SETTINGS);
			}

			// Get all DB categories
			List<ServiceCategoriesMapping> allCategories = this.serviceCategoriesMappingRepository.findAll();
			// List<JustDailServices> justdailServiceList = new ArrayList<>();
			Integer newRec = 0;
			Integer updatedRec = 0;

			for (ServiceCategoriesMapping category : allCategories) {
				List<CompletableFuture<JSONObject>> allFutures = new ArrayList<>();
				for (ServiceSubCategoryMapping subCategory : category.getSubCategories()) {
					for (ServiceSubCategoryMapping.Source source : subCategory.getSource()) {
						if (source.getName().equals(BYConstants.SERVICE_SOURCE_JD)) {
							allFutures.add(getAsyncJustDialCategoryServices("", Integer.parseInt(source.getCatid()),
									limit, 1, request));
						}
					}
				}
				CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
				System.out.println("response: " + allFutures);
				for (CompletableFuture<JSONObject> futureResult : allFutures) {
					JSONObject jdCategoryService = new JSONObject();
					jdCategoryService = futureResult.get();
					for (int i = 0; i < jdCategoryService.getJSONArray("services").length(); i++) {
						JSONObject jsonObject = jdCategoryService.getJSONArray("services").getJSONObject(i);
						// Convert jsonObject to java Hashmap
						HashMap<String, Object> result = new ObjectMapper().readValue(jsonObject.toString(),
								HashMap.class);

						// Filter existing JD record
						Query query = new Query();
						query.addCriteria(Criteria.where("serviceInfo.docid").is(jsonObject.getString("docid")));
						JustDailServices existingSerivceProfiles = null;
						existingSerivceProfiles = this.mongoTemplate.findOne(query, JustDailServices.class);

						if (existingSerivceProfiles == null) {
							JustDailServices jdservice = new JustDailServices();
							jdservice.setServiceInfo(result);
							// justdailServiceList.add(jdservice);
							justDialSerivcesRepository.save(jdservice);
							newRec++;
						} else {
							existingSerivceProfiles.setServiceInfo(result);
							justDialSerivcesRepository.save(existingSerivceProfiles);
							updatedRec++;
						}

					}
				}

			}
			jdResponse.put("Total Records Added", newRec);
			jdResponse.put("Total Records Updated", updatedRec);

			JdServiceLog.setStatus(ServicesStatus.SUCCESS);
			JdServiceLog.setRecordsAdded(newRec);
			JdServiceLog.setRecordsUpdated(updatedRec);
			JdServiceLog.setExecutionEnd(new Date());
			mongoTemplate.save(JdServiceLog);
			// if (justdailServiceList.size() > 0) {
			// justDialSerivcesRepository.save(justdailServiceList);
			// }
		} catch (Exception e) {
			// throw e;
			JdServiceLog.setError(e.toString());
			JdServiceLog.setStatus(ServicesStatus.FAILURE);
			JdServiceLog.setExecutionEnd(new Date());
			mongoTemplate.save(JdServiceLog);

			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}
		// return jsonarray.toString();
		return BYGenericResponseHandler.getResponse(jdResponse);
		// justDailSearchResponse.put("services", jsonarray);
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
				String categoryKey = JDResponse.getString("keyword");
				String categoryId = JDResponse.getString("national_catid");
				JSONArray newDataList = new JSONArray();
				for (int i = 0; i < dataList.length(); i++) {
					JSONArray dataInfoList = dataList.getJSONArray(i);
					JSONObject dataInfoMap = new JSONObject();
					for (int j = 0; j < dataInfoList.length(); j++) {
						dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
					}
					dataInfoMap.put("categoryKey", categoryKey);
					dataInfoMap.put("categoryId", categoryId);
					newDataList.put(dataInfoMap);
				}
				response.put("services", newDataList);
			} else {
				response = null;
			}
			// response.put("JDResponse", JDResponse);
		} catch (Exception e) {
			e.printStackTrace();
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
			String categoryKey = JDResponse.getString("keyword");
			String categoryId = JDResponse.getString("national_catid");
			JSONArray newDataList = new JSONArray();
			for (int i = 0; i < dataList.length(); i++) {
				JSONArray dataInfoList = dataList.getJSONArray(i);
				JSONObject dataInfoMap = new JSONObject();
				for (int j = 0; j < dataInfoList.length(); j++) {
					dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
				}
				dataInfoMap.put("categoryKey", categoryKey);
				dataInfoMap.put("categoryId", categoryId);
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
	@Async
	public CompletableFuture<JSONObject> getAsyncJustDialCategoryServices(
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
			String categoryKey = JDResponse.getString("keyword");
			String categoryId = JDResponse.getString("national_catid");
			JSONArray newDataList = new JSONArray();
			for (int i = 0; i < dataList.length(); i++) {
				JSONArray dataInfoList = dataList.getJSONArray(i);
				JSONObject dataInfoMap = new JSONObject();
				for (int j = 0; j < dataInfoList.length(); j++) {
					dataInfoMap.put(columns.getString(j), dataInfoList.get(j));
				}
				dataInfoMap.put("categoryKey", categoryKey);
				dataInfoMap.put("categoryId", categoryId);
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
		return CompletableFuture.completedFuture(response);
	}

	/**
	 * JD categories list
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/serviceCategories" }, produces = { "application/json" })
	@ResponseBody
	public Object getServiceCategories(HttpServletRequest request) throws Exception {
		List<ServiceCategories> categories = null;
		try {

			categories = this.serviceCategoriesRepository.findAll();

		} catch (Exception e) {
			// throw e;
			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}

		return BYGenericResponseHandler.getResponse(categories);
	}

	/**
	 * JD Auto Complete list
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/autoComplete" }, produces = { "application/json" })
	@ResponseBody
	public Object getAutoComplete(@RequestParam(value = "search", required = true) String search,
			HttpServletRequest request) throws Exception {
		Object response = null;
		List<Integer> serviceTypes = new ArrayList<Integer>();
		serviceTypes.add(UserTypes.INDIVIDUAL_PROFESSIONAL);
		serviceTypes.add(UserTypes.INSTITUTION_NGO);
		serviceTypes.add(UserTypes.INSTITUTION_BRANCH);

		try {

			Direction sortDirection = Direction.DESC;
			Pageable pageable = new PageRequest(0, 5, sortDirection, "createdAt");
			TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(search);

			Query query = TextQuery.queryText(criteria).sortByScore();
			query.with(pageable);

			query.addCriteria(Criteria.where("userTypes").in(serviceTypes));
			query.addCriteria(
					Criteria.where("status").in(new Object[] { DiscussConstants.DISCUSS_STATUS_ACTIVE, null }));

			List<UserProfile> profiles = this.mongoTemplate.find(query, UserProfile.class);
			JSONArray DbServices = new JSONArray(profiles);

			JustDialHandler JDHandler = new JustDialHandler();
			JSONArray JDResult = new JSONArray();
			JDResult = JDHandler.getAutosuggest(search);

			for (int i = 0; i < DbServices.length(); i++) {
				JSONObject service = new JSONObject();
				service.put("id", DbServices.getJSONObject(i).getString("id"));
				service.put("value",
						DbServices.getJSONObject(i).getJSONObject("basicProfileInfo").getString("firstName"));
				service.put("type", 0);
				JDResult.put(service);
			}

			response = JDResult.toString();

		} catch (Exception e) {
			// throw e;
			Util.handleException(e);
			// throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
		}

		return response;
	}

}
