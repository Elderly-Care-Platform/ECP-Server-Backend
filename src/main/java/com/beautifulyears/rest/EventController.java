package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.EventRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.EventResponse;
import com.beautifulyears.rest.response.EventResponse.EventPage;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.EventActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.SharedActivityLogHandler;

/**
 * The REST based service for managing "event"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/event" })
public class EventController {
	private static final Logger logger = Logger
			.getLogger(EventController.class);
	private EventRepository eventRepository;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Event> logHandler;
	ActivityLogHandler<Object> shareLogHandler;

	@Autowired
	public EventController(EventRepository eventRepository,
			MongoTemplate mongoTemplate) {
		this.eventRepository = eventRepository;
		this.mongoTemplate = mongoTemplate;
		logHandler = new EventActivityLogHandler(mongoTemplate);
		shareLogHandler = new SharedActivityLogHandler(mongoTemplate);
	}

	// @RequestMapping(consumes = { "application/json" }, value = { "/contactUs" })
	// @ResponseBody
	// public Object submitFeedback(@RequestBody Event event,
	// 		HttpServletRequest request, HttpServletResponse res)
	// 		throws Exception {
	// 	LoggerUtil.logEntry();
	// 	User currentUser = Util.getSessionUser(request);
	// 	if (null != currentUser) {
	// 		event.setUserId(currentUser.getId());
	// 		event.setUsername(currentUser.getUserName());
	// 		Query query = new Query();
	// 		query.addCriteria(Criteria.where("userId").is(currentUser.getId()));
	// 		UserProfile profile = mongoTemplate.findOne(query,
	// 				UserProfile.class);
	// 		event.setUserProfile(profile);
	// 	}
	// 	event.setEventType("F");

	// 	event = eventRepository.save(event);
	// 	logHandler.addLog(event, ActivityLogConstants.CRUD_TYPE_CREATE,
	// 			request);
	// 	try {
	// 		MailHandler.sendMultipleMail(BYConstants.ADMIN_EMAILS,
	// 				"New Feedback: " + event.getTitle(), event.getText());
	// 	} catch (Exception e) {
	// 		logger.error("error sending the mail for this feedback");
	// 	}

	// 	logger.info("new feedback entity created with ID: " + event.getId());
	// 	Util.logStats(mongoTemplate, request, "New Feedback",
	// 			event.getUserId(), null, event.getId(), null, null, null,
	// 			"new feedback added", "EVENT");
	// 	return BYGenericResponseHandler.getResponse(event);
	// }

	// @RequestMapping(method = { RequestMethod.GET }, value = { "/getLinkInfo" }, produces = { "application/json" })
	// @ResponseBody
	// public Object getLinkInfo(
	// 		@RequestParam(value = "url", required = true) String url,
	// 		HttpServletRequest req) throws Exception {
	// 	LinkInfo linkInfo = null;
	// 	WebPageParser parser = null;
	// 	try {
	// 		parser = new WebPageParser(url);
	// 	} catch (Exception e) {
	// 		Util.handleException(e);
	// 	} finally {
	// 		if (parser != null) {
	// 			linkInfo = parser.getUrlDetails();
	// 		}
	// 	}
	// 	Util.logStats(mongoTemplate, req, "Get link Info", null, null, url,
	// 			null, url, null, "submitting url to get the link preview",
	// 			"EVENT");
	// 	return BYGenericResponseHandler.getResponse(linkInfo);
	// }

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitEvent(@RequestBody Event event,
			HttpServletRequest request, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "POST")) {
			if (event != null && (Util.isEmpty(event.getId()))) {
				event.setOrganiser(currentUser.getId());
				Event eventExtracted = new Event(
					event.getTitle(), 
					event.getDatetime(), 
					event.getDescription(), 
					event.getEntryFee(), 
					event.getPerPerson(), 
					event.getEventType(), 
					event.getStatus(), 
					event.getEmail(), 
					event.getLocation(), 
					event.getLocLat(), 
					event.getLocLng(), 
					event.getLanguages(), 
					event.getPhone(), 
					event.getOrganiser());

				event = eventRepository.save(eventExtracted);
				logHandler.addLog(event, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new event entity created with ID: " + event.getId() + " by User " + event.getOrganiser());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW " + event.getEventType()
				+ " added.", event.getOrganiser(), currentUser.getEmail(),
				event.getId(), null, null, null,
				"new event entity is added", "EVENT");
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editEvent(@RequestBody Event event, HttpServletRequest request, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "POST")) {
			if (event != null && (!Util.isEmpty(event.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId())
						|| event.getOrganiser().equals(currentUser.getId())) {

					Event oldEvent = mongoTemplate.findById(new ObjectId(event.getId()), Event.class);
					oldEvent.setTitle(event.getTitle());
					oldEvent.setDatetime(event.getDatetime());
					oldEvent.setDescription(event.getDescription());
					oldEvent.setEntryFee(event.getEntryFee());
					oldEvent.setPerPerson(event.getPerPerson());
					oldEvent.setEventType(event.getEventType());
					oldEvent.setStatus(event.getStatus());
					oldEvent.setEmail(event.getEmail());
					oldEvent.setLocation(event.getLocation());
					oldEvent.setLocLat(event.getLocLat());
					oldEvent.setLocLng(event.getLocLat());
					oldEvent.setLanguages(event.getLanguages());
					oldEvent.setPhone(event.getPhone());
					oldEvent.setOrganiser(event.getOrganiser());
					oldEvent.setLastModifiedAt(new Date());
					
					event = eventRepository.save(oldEvent);
					logHandler.addLog(event,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("new event entity created with ID: "
							+ event.getId() + " by User "
							+ event.getOrganiser());

					Util.logStats(mongoTemplate, request,
							"EDIT " + event.getEventType()
									+ " event content.", event.getOrganiser(),
							currentUser.getEmail(), event.getId(), null,
							null, null, "new event entity is added",
							"EVENT");

				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "eventType", required = false) Integer eventType,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<Event> page = null;
		EventPage eventPage = null;
		try {
			
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = eventRepository.getPage(searchTxt,eventType, pageable);
			eventPage = EventResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(eventPage);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/count" }, produces = { "application/json" })
	@ResponseBody
	public Object eventByEventTypeTopicAndSubTopicCount(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "eventType", required = false) Integer eventType,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		Map<String, Long> obj = new HashMap<String, Long>();
		List<String> filterCriteria = new ArrayList<String>();
		try {

			Long allCount = null;
			Long outdoorCount = null;
			Long indoorCount = null;
			if (null!= searchTxt) {
				if(eventType == 0){
					allCount = eventRepository.getCount(searchTxt,0);
					filterCriteria.add("eventType = 0");
					obj.put("all", new Long(allCount));

					outdoorCount = eventRepository.getCount(searchTxt,1);
					filterCriteria.add("eventType = 1");
					obj.put("outdoor", new Long(outdoorCount));

					indoorCount = eventRepository.getCount(searchTxt,2);
					filterCriteria.add("eventType = 2");
					obj.put("indoor", new Long(indoorCount));
				}
				if(eventType == 1){
					outdoorCount = eventRepository.getCount(searchTxt,1);
					filterCriteria.add("eventType = 1");
					obj.put("outdoor", new Long(outdoorCount));
				}
				if(eventType == 2){
					indoorCount = eventRepository.getCount(searchTxt,2);
					filterCriteria.add("eventType = 2");
					obj.put("indoor", new Long(indoorCount));
				}
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "count query for event", null,
				null, null, null, null, filterCriteria,
				"querying count for event", "EVENT");
		return BYGenericResponseHandler.getResponse(obj);
	}
}
