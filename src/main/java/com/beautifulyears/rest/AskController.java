package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.domain.AskQuestionReply;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.AskCategoryRepository;
import com.beautifulyears.repository.AskQuestionReplyRepository;
import com.beautifulyears.repository.AskQuestionRepository;
import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.rest.response.AskCategoryResponse;
import com.beautifulyears.rest.response.AskQuestionReplyResponse;
import com.beautifulyears.rest.response.AskQuestionResponse;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.rest.response.UserProfileResponse;
import com.beautifulyears.rest.response.AskCategoryResponse.AskCategoryPage;
import com.beautifulyears.rest.response.AskQuestionReplyResponse.AskQuestionReplyPage;
import com.beautifulyears.rest.response.AskQuestionResponse.AskQuestionPage;
import com.beautifulyears.rest.response.UserProfileResponse.UserProfilePage;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.AskCategoryActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.AskQuestionActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.AskQuestionReplyActivityLogHandler;

/**
 * The REST based service for managing "product"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/ask" })
public class AskController {
	private static final Logger logger = Logger
			.getLogger(AskController.class);
	private AskQuestionRepository askQuesRepo;
	private AskCategoryRepository askCatRepo;
	private UserProfileRepository userProfileRepo;
	private AskQuestionReplyRepository quesReplyRepo;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<AskQuestion> logHandler;
	ActivityLogHandler<AskCategory> logHandlerCat;
	ActivityLogHandler<AskQuestionReply> logHandlerRep;
	// ActivityLogHandler<Object> shareLogHandler;

	@Autowired
	public AskController(AskQuestionRepository askQuesRepo, UserRepository userRepository, 
			AskCategoryRepository askCatRepo,
			UserProfileRepository userProfileRepo,
			AskQuestionReplyRepository quesReplyRepo,
			MongoTemplate mongoTemplate) {
		this.askQuesRepo = askQuesRepo;
		this.askCatRepo = askCatRepo;
		this.userProfileRepo = userProfileRepo;
		this.quesReplyRepo = quesReplyRepo;
		this.mongoTemplate = mongoTemplate;
		logHandler = new AskQuestionActivityLogHandler(mongoTemplate);
		logHandlerCat = new AskCategoryActivityLogHandler(mongoTemplate);
		logHandlerRep = new AskQuestionReplyActivityLogHandler(mongoTemplate);
		// shareLogHandler = new SharedActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the product detail for provided productId
	 * 
	 * @param req
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getAskQuestionDetail(HttpServletRequest req,
			@RequestParam(value = "askQuesId", required = true) String askQuesId)
			throws Exception {
		LoggerUtil.logEntry();
		Util.logStats(mongoTemplate, req, "get detail of ask question item", null,
				null, askQuesId, null, null,
				Arrays.asList("askQuesId = " + askQuesId),
				"get detail page for askQuesId " + askQuesId, "ASK_QUESTION");

		AskQuestion askQuestion = askQuesRepo.findOne(askQuesId);
		try {
			if (null == askQuestion) {
				throw new BYException(BYErrorCodes.ASK_QUESTION_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(askQuestion);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitAskQuestion(@RequestBody AskQuestion askQues, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		UserProfile askQuesExpert = null;
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "ASK")) {
			if (askQues != null && (Util.isEmpty(askQues.getId()))) {
				
				AskQuestion askQuesExtracted = new AskQuestion(
					askQues.getQuestion(),
					askQues.getDescription(),
					askQues.getAskCategory(),
					askQues.getAskedBy(),
					askQues.getAnsweredBy(),
					askQues.getAnswered()
					);

				askQues = askQuesRepo.save(askQuesExtracted);
				askQuesExpert = userProfileRepo.findOne(askQues.getAnsweredBy().getId());
				logHandler.addLog(askQues, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new ask question entity created with ID: " + askQues.getId());
				MailHandler.sendMailToUserId(askQuesExpert.getUserId(), "ECP - New Question for you", 
				"Hi,<br/>"+

				"This is to inform that a new question has been asked by one of elders who is seeking help / some informtion.<br/>"+
				" Question Asked is '" + askQues.getQuestion() + "'<br/>"+
				"Requesting you to please respond.<br/><br/>"+
				"Best Regards<br/>"+
				"ECP Team");
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Ask Question added.", currentUser.getId(), currentUser.getEmail(),
				askQues.getId(), null, null, null,
				"new ask question entity is added", "ASK_QUESTION");
		return BYGenericResponseHandler.getResponse(askQues);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editAskQuestion(@RequestBody AskQuestion askQues, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "ASK")) {
			if (askQues != null && (!Util.isEmpty(askQues.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

					AskQuestion oldAskQues = mongoTemplate.findById(new ObjectId(askQues.getId()), AskQuestion.class);
					oldAskQues.setAnswered(askQues.getAnswered());
					oldAskQues.setDescription(askQues.getDescription());
					oldAskQues.setAskCategory(askQues.getAskCategory());
					oldAskQues.setAskedBy(askQues.getAskedBy());
					oldAskQues.setAnsweredBy(askQues.getAnsweredBy());
					oldAskQues.setAnswered(askQues.getAnswered());
					oldAskQues.setQuestion(askQues.getQuestion());

					askQues = askQuesRepo.save(oldAskQues);
					logHandler.addLog(askQues,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old ask question entity updated for ID: "
							+ askQues.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request,
							"EDIT " + askQues.getQuestion()
									+ " ask question content.", currentUser.getId(),
							currentUser.getEmail(), askQues.getId(), null,
							null, null, "old ask question entity updated",
							"ASK_QUESTION");
				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askQues);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "askCategory", required = false) String askCategory,
			@RequestParam(value = "askedBy", required = false) String askedBy,
			@RequestParam(value = "answeredBy", required = false) String answeredBy,
			@RequestParam(value = "answered", required = false) Boolean answered,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<AskQuestion> page = null;
		AskQuestionPage askQuesPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = askQuesRepo.getPage(searchTxt, askCategory, askedBy, answeredBy, answered , pageable);
			askQuesPage = AskQuestionResponse.getPage(page, currentUser,quesReplyRepo, userProfileRepo);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(askQuesPage);
	}	

	@RequestMapping(method = { RequestMethod.GET }, value = { "/count" }, produces = { "application/json" })
	@ResponseBody
	public Object askQuesCount(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "askCategory", required = false) String askCategory,
			@RequestParam(value = "askedBy", required = false) String askedBy,
			@RequestParam(value = "answeredBy", required = false) String answeredBy,
			@RequestParam(value = "answered", required = false) Boolean answered,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		Map<String, Long> obj = new HashMap<String, Long>();
		List<String> filterCriteria = new ArrayList<String>();
		try {
			Long allCount = null;
			allCount = askQuesRepo.getCount(searchTxt, askCategory, askedBy, answeredBy, answered);
			obj.put("all", new Long(allCount));
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "count query for ask question", null,
				null, null, null, null, filterCriteria,
				"querying count for  ask question", "ASK_QUESTION");
		return BYGenericResponseHandler.getResponse(obj);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/category/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getCategoryPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<AskCategory> page = null;
		AskCategoryPage askCatPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = askCatRepo.getPage(searchTxt, pageable);
			askCatPage = AskCategoryResponse.getPage(page, currentUser,userProfileRepo, searchTxt);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(askCatPage);
	}

	@RequestMapping(value = { "/category" }, method = { RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	@ResponseBody
	public Object submitCategory(@RequestBody AskCategory askCategory, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "ASK")) {
			if (askCategory != null && (Util.isEmpty(askCategory.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {
					AskCategory askCatExtracted = new AskCategory( askCategory.getName() );

					askCategory = askCatRepo.save(askCatExtracted);
					logHandlerCat.addLog(askCategory, ActivityLogConstants.CRUD_TYPE_CREATE, request);
					logger.info("new ask entity created with ID: " + askCategory.getId());

				}
				else{
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}
				
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Ask Category added.", currentUser.getId(), currentUser.getEmail(),
			askCategory.getId(), null, null, null,
				"new ask category entity is added", "ASK_CATEGORY");
		return BYGenericResponseHandler.getResponse(askCategory);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/category" }, consumes = { "application/json" })
	@ResponseBody
	public Object editCategory(@RequestBody AskCategory askCat, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "ASK")) {
			if (askCat != null && (!Util.isEmpty(askCat.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

					AskCategory oldAskCat = mongoTemplate.findById(new ObjectId(askCat.getId()), AskCategory.class);
					oldAskCat.setName(askCat.getName());
					
					askCat = askCatRepo.save(oldAskCat);
					logHandlerCat.addLog(askCat,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old ask category entity updated for ID: "
							+ askCat.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request,
							"EDIT " + askCat.getName()
									+ " ask category content.", currentUser.getId(),
							currentUser.getEmail(), askCat.getId(), null,
							null, null, "old ask category entity updated",
							"ASK_CATEGORY");
				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(askCat);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/reply/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getReviewPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "questionId", required = false) String questionId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<AskQuestionReply> page = null;
		AskQuestionReplyPage quesReplyPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = quesReplyRepo.getPage(searchTxt, questionId, pageable);
			quesReplyPage = AskQuestionReplyResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(quesReplyPage);
	}

	@RequestMapping(method = { RequestMethod.POST }, value = { "/reply" }, consumes = { "application/json" })
	@ResponseBody
	public Object submitAskQuestionReply(@RequestBody AskQuestionReply askQuestionReply, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "ASK")) {
			if (askQuestionReply != null && (Util.isEmpty(askQuestionReply.getId()))) {
				AskQuestionReply askQuestionReplyExtracted = new AskQuestionReply(
					askQuestionReply.getAskQuestionId(), 
					askQuestionReply.getReply(), 
					askQuestionReply.getUser()
				);

				askQuestionReply = quesReplyRepo.save(askQuestionReplyExtracted);

				AskQuestion question = askQuesRepo.findOne(askQuestionReply.getAskQuestionId());
				if(question.getAskedBy().getId().equals(askQuestionReplyExtracted.getUser().getId())){
					question.setAnswered(false);
					MailHandler.sendMailToUserId(question.getAnsweredBy().getUserId(), "ECP - Response from user", 
						"Hi,<br/>"+
						"This is to inform that questioner responded to question answered by you .<br/>"+
						" Question was '" + question.getQuestion() + "'<br/>"+
						"Requesting you to please respond.<br/><br/>"+
						"Best Regards<br/>"+
						"ECP Team");
				}
				else{
					question.setAnswered(true);
					MailHandler.sendMailToUserId(question.getAskedBy().getId(), "ECP - Response from expert", 
						"Hi,<br/>"+
						"This is to inform that expert replied to question asked by you.<br/>"+
						" Question was '" + question.getQuestion() + "'<br/>"+
						"Best Regards<br/>"+
						"ECP Team");
				}
				askQuesRepo.save(question);
				logHandlerRep.addLog(askQuestionReply, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new ask question reply entity created with ID: " + askQuestionReply.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Ask question reply added.", currentUser.getId(), currentUser.getEmail(),
			askQuestionReply.getId(), null, null, null,
				"new ask question reply entity is added", "ASK_QUESTION_REPLY");
		return BYGenericResponseHandler.getResponse(askQuestionReply);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/experts/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getExperts(
		@RequestParam(value = "searchTxt", required = false) String searchTxt,
		@RequestParam(value = "experties", required = false, defaultValue = "") List<ObjectId> experties,
		@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
		@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
		@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
		@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
		HttpServletRequest req,
		HttpServletResponse res) throws Exception {
		UserProfilePage userProfilePage = null;
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("experties = " + experties);
		filterCriteria.add("page = " + pageIndex);
		filterCriteria.add("size = " + pageSize);
		filterCriteria.add("sort = " + sort);
		filterCriteria.add("dir = " + dir);
		Integer[] userTypes = { UserTypes.ASK_EXPERT};
		logger.debug("trying to get ask question experts profiles");

		try {
			logger.debug("page" + pageIndex + ",size" + pageSize);
			/* setting page and sort criteria */
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			List<String> fields = null;
			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			userProfilePage = UserProfileResponse.getPage(
					userProfileRepo.getServiceProvidersByFilterCriteria(
						searchTxt, userTypes, null, null, null, experties, pageable, fields),
					null,
					this.askQuesRepo
				);
			if (userProfilePage.getContent().size() > 0) {
				logger.debug("did not find any ask question expert");
			}

		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, req, "get ask question experts", null, null, null, null, null, filterCriteria,
				"get ask question experts", "SERVICE");
		return BYGenericResponseHandler.getResponse(userProfilePage);
	}
}
