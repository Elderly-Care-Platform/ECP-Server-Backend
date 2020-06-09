package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.EventConstants;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.ReportEvent;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.EventRepository;
import com.beautifulyears.repository.ReportEventRepository;
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
	private ReportEventRepository reportEventRepository;
	private UserRepository userRepository;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Event> logHandler;
	ActivityLogHandler<Object> shareLogHandler;

	@Autowired
	public EventController(EventRepository eventRepository, UserRepository userRepository,
			ReportEventRepository reportEventRepository, MongoTemplate mongoTemplate) {
		this.eventRepository = eventRepository;
		this.userRepository = userRepository;
		this.mongoTemplate = mongoTemplate;
		this.reportEventRepository = reportEventRepository;
		logHandler = new EventActivityLogHandler(mongoTemplate);
		shareLogHandler = new SharedActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the event detail for provided eventId
	 * 
	 * @param req
	 * @param eventId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getEventDetail(HttpServletRequest req,
			@RequestParam(value = "eventId", required = true) String eventId)
			throws Exception {
		LoggerUtil.logEntry();
		Util.logStats(mongoTemplate, req, "get detail of discuss item", null,
				null, eventId, null, null,
				Arrays.asList("eventId = " + eventId),
				"get detail page for eventId " + eventId, "COMMUNITY");

		Event event = eventRepository.findOne(eventId);
		try {
			if (null == event) {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(event);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitEvent(@RequestBody Event event, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "POST")) {
			if (event != null && (Util.isEmpty(event.getId()))) {
				//event.setOrganiser(currentUser.getUserName());
				Event eventExtracted = new Event(
					event.getTitle(), 
					event.getDatetime(), 
					event.getDescription(),
					event.getCapacity(), 
					event.getEntryFee(), 
					event.getEventType(), 
					EventConstants.EVENT_STATUS_SUGGESTED, 
					event.getAddress(), 
					event.getLandmark(), 
					event.getLanguages(), 
					event.getOrganiser(), 
					event.getOrgPhone(), 
					event.getOrgEmail(),
					currentUser.getId());

				event = eventRepository.save(eventExtracted);
				logHandler.addLog(event, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new event entity created with ID: " + event.getId() + " for Organiser " + event.getOrganiser());
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
	public Object editEvent(@RequestBody Event event, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "POST")) {
			if (event != null && (!Util.isEmpty(event.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

					Event oldEvent = mongoTemplate.findById(new ObjectId(event.getId()), Event.class);
					oldEvent.setTitle(event.getTitle()); 
					oldEvent.setDatetime(event.getDatetime());
					oldEvent.setDescription(event.getDescription());
					oldEvent.setCapacity(event.getCapacity());
					oldEvent.setEntryFee(event.getEntryFee());
					oldEvent.setEventType(event.getEventType());
					oldEvent.setStatus(event.getStatus());
					oldEvent.setAddress(event.getAddress());
					oldEvent.setLandmark(event.getLandmark());
					oldEvent.setLanguages(event.getLanguages());
					oldEvent.setOrganiser(event.getOrganiser()); 
					oldEvent.setOrgPhone(event.getOrgPhone()); 
					oldEvent.setOrgEmail(event.getOrgEmail());
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
			@RequestParam(value = "pastEvents", required = false) Integer pastEvents,
			@RequestParam(value = "startDatetime", required = false) Long startDatetime,
			@RequestParam(value = "sort", required = false, defaultValue = "datetime") String sort,
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
			page = eventRepository.getPage(searchTxt,eventType, startDatetime,pastEvents, pageable);
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
			@RequestParam(value = "startDatetime", required = false) Long startDatetime,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		Map<String, Long> obj = new HashMap<String, Long>();
		List<String> filterCriteria = new ArrayList<String>();
		try {

			Long allCount = null;
			Long past = null;
			Long upcoming = null;
			if (null!= searchTxt) {
				
				allCount = eventRepository.getCount(searchTxt,0,startDatetime,0);
				filterCriteria.add("isPast = 0");
				obj.put("all", new Long(allCount));

				past = eventRepository.getCount(searchTxt,0,startDatetime,1);
				filterCriteria.add("isPast = 1");
				obj.put("past", new Long(past));

				upcoming = eventRepository.getCount(searchTxt,0,startDatetime,-1);
				filterCriteria.add("isPast = -1");
				obj.put("upcoming", new Long(upcoming));
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "count query for event", null,
				null, null, null, null, filterCriteria,
				"querying count for event", "EVENT");
		return BYGenericResponseHandler.getResponse(obj);
	}

	@RequestMapping(method = { RequestMethod.POST },value = { "/markfav" }, produces = { "application/json" })
	@ResponseBody
	public Object markEventFav(
		@RequestParam(value = "eventId", required = true) String eventId,
		@RequestParam(value = "userId", required = true) String userId,
		@RequestParam(value = "markIt", required = true) Boolean markIt,
		HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "LIKE")) {
			List<String> eventIds = currentUser.getFavEvents();
			if(!eventIds.contains(eventId) && markIt == true){
				eventIds.add(eventId);
				currentUser.setFavEvents(eventIds);
				this.userRepository.save(currentUser);
			}
			if(eventIds.contains(eventId) && markIt == false){
				eventIds.remove(eventId);
				currentUser.setFavEvents(eventIds);
				this.userRepository.save(currentUser);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(currentUser.getFavEvents());
	}

	/**
	 * Report service provider
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = { "/reportEvent" }, consumes = { "application/json" })
	@ResponseBody
	public Object submitReportEvent(@RequestBody ReportEvent reportEvent, HttpServletRequest request,
			HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "POST")) {
			if (reportEvent != null && (!Util.isEmpty(reportEvent.getEventId()))) {
				try {
					Query query = new Query();
					query.addCriteria(Criteria.where("userId").is(reportEvent.getUserId()));
					UserProfile userProfile = mongoTemplate.findOne(query, UserProfile.class);

					Query queryE = new Query();
					queryE.addCriteria(Criteria.where("id").is(reportEvent.getEventId()));

					Event event = mongoTemplate.findOne(queryE, Event.class);

					if (userProfile != null) {
						ReportEvent reportEventExtra = new ReportEvent(reportEvent.getEventId(),
								currentUser.getId(), reportEvent.getComment());

						reportEvent = reportEventRepository.save(reportEventExtra);

						MailHandler.sendMultipleMail(BYConstants.ADMIN_EMAILS,
								"Alert: An Event Organizer has been reported by a member!",
								"The event organizer "+ event.getOrganiser() +" for the event " + event.getTitle() + " has been reported by " + currentUser.getUserName() + ".  Please log into the Administrator panel to review the report."+
								"<br/><br/>Based on your review please take necessary actions and inform "+ currentUser.getUserName() +" the actions that you are taking.  If necessary please inform the Event Organizer "+ event.getOrganiser() +" about the report against the event."+ 
								"<br/><br/>Sincerely,"+
								"<br/>Bot@JoyofAge" +
								"<br/><img style=\"background-color:#212942;padding:5px\" src=\"https://dev.joyofage.org/assets/images/JOA_Logo_Light_RGB.svg\" alt=\"Logo JoyOfAge\">" +
								"<br/>PS: Please ignore this email alert if you have already responded to this question.");
					}
				} catch (Exception e) {
					Util.handleException(e);
				}

				logger.info("new service report entity created with ID: " + reportEvent.getId() + " by User "
						+ reportEvent.getUserId());

			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW " + reportEvent.getEventId() + " added.",
		reportEvent.getUserId(), currentUser.getEmail(), reportEvent.getId(), null, null, null,
				"new  service report entity is added", "SERVICE");
		return BYGenericResponseHandler.getResponse(reportEvent);

	}

	/**
	 * Send mail to admin regardign all suggested events
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/fetchSuggestedEvents" }, produces = {
		"application/json" })
	@ResponseBody
	public Object fetchSuggestedEvents(HttpServletRequest request) throws Exception {
		try {
			String message = "";
			List<Event> events = eventRepository.getSuggestedEvents();
			for (Event event : events) {
				message += event.getTitle() + "<br/>";
			}
			if(message.equals("")){
				message = "No events found.";
			}
			MailHandler.sendMultipleMail(BYConstants.ADMIN_EMAILS,
								"Alert: Suggested events waiting for approval",
								"List of evetns waiting for approval:"+
								"<br/><br/>"+
								message+
								"<br/><br/>Sincerely,"+
								"<br/>Bot@JoyofAge" +
								"<br/><img style=\"background-color:#212942;padding:5px\" src=\"https://dev.joyofage.org/assets/images/JOA_Logo_Light_RGB.svg\" alt=\"Logo JoyOfAge\">" +
								"<br/>PS: Please ignore this email alert if you have already responded to this question.");
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse("success");
	}
}
