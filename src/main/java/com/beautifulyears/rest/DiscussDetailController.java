package com.beautifulyears.rest;

import java.util.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.DiscussReply;
import com.beautifulyears.domain.DiscussView;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.DiscussReplyRepository;
import com.beautifulyears.repository.DiscussRepository;
import com.beautifulyears.repository.DiscussViewRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussDetailResponse;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ReplyActivityLogHandler;

/**
 * Controller to handle all the discuss detail related API 1. getting full
 * discuss detail (discuss + replies) 2. Posting comment 3. Posting answer
 * 
 * @author Nitin
 * 
 *
 */
@Controller
@RequestMapping("/discussDetail")
public class DiscussDetailController {
	private static final Logger logger = Logger.getLogger(DiscussDetailController.class);
	private MongoTemplate mongoTemplate;
	private DiscussRepository discussRepository;
	private DiscussReplyRepository discussReplyRepository;
	private DiscussViewRepository discussViewRepository;
	private ActivityLogHandler<DiscussReply> logHandler;

	@Autowired
	public DiscussDetailController(MongoTemplate mongoTemplate, DiscussRepository discussRepository,
			DiscussReplyRepository discussReplyRepository,
			DiscussViewRepository discussViewRepository
			) {
		this.discussRepository = discussRepository;
		this.mongoTemplate = mongoTemplate;
		this.discussReplyRepository = discussReplyRepository;
		this.discussViewRepository = discussViewRepository;
		logHandler = new ReplyActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the discuss detail for provided discussId
	 * 
	 * @param req
	 * @param res
	 * @param discussId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getDiscussDetail(HttpServletRequest req, HttpServletResponse res,
			@RequestParam(value = "discussId", required = true) String discussId) throws Exception {
		LoggerUtil.logEntry();
		Discuss discuss = discussRepository.findOne(discussId);
		User user = Util.getSessionUser(req);
		List<DiscussView> views = null;
		DiscussView view = null;
		String userId = "";
		if(user != null){
			userId = user.getId();
		}
		
		views = discussViewRepository.findByContentIdAndUserIdAndIpAddress(discuss.getId(), userId, req.getRemoteAddr());

		if(views!= null && views.size() > 0){
			view = views.get(0);
			view.setViewAt(new Date());
			discussViewRepository.save(view);
		}
		else{
			view = new DiscussView(discuss.getId(), userId, new Date(), req.getRemoteAddr());
			discussViewRepository.save(view);
			if(discuss != null){
				discuss.setViewCount(discuss.getViewCount() + 1);
				this.discussRepository.save(discuss);
			}
		}
		
		Util.logStats(mongoTemplate, req, "get detail of discuss item", null, null, discussId, null, null,
				Arrays.asList("discussId = " + discussId), "get detail page for discussId " + discussId, "COMMUNITY");
		return BYGenericResponseHandler.getResponse(getDiscussDetailById(discussId, req));

	}

	/**
	 * API for posting a reply of type comment
	 * 
	 * @param comment
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.POST }, params = "type=0", consumes = { "application/json" })
	@ResponseBody
	public Object submitComment(@RequestBody DiscussReply comment, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		String discussId = comment.getDiscussId();
		User user = null;
		DiscussReply parentComment = null;
		try {
			Discuss discuss = discussRepository.findOne(discussId);
			List<DiscussReply> ancestors = null;
			if (null != discuss) {
				comment.setDiscussId(discuss.getId());
				comment.setContentType(Util.getDiscussContentType(discuss.getDiscussType()));
				comment.setReplyType(DiscussConstants.REPLY_TYPE_COMMENT);
				user = Util.getSessionUser(req);
				if (null != user && SessionController.checkCurrentSessionFor(req, "COMMENT")) {
					comment.setUserId(user.getId());
					comment.setUserName(user.getUserName());
					Query query = new Query();
					query.addCriteria(Criteria.where("userId").is(user.getId()));
					UserProfile profile = mongoTemplate.findOne(query, UserProfile.class);
					comment.setUserProfile(profile);
				} else {
					throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
				}
				if (!Util.isEmpty(comment.getParentReplyId())) {
					// if nested comment
					parentComment = discussReplyRepository.findOne(comment.getParentReplyId());
					if (null != parentComment) {
						parentComment.setUrl(comment.getUrl());
						parentComment.setDirectChildrenCount(parentComment.getDirectChildrenCount() + 1);
						comment.getAncestorsId().addAll(parentComment.getAncestorsId());
						comment.getAncestorsId().add(parentComment.getId());
						comment.setParentReplyId(parentComment.getId());
						mongoTemplate.save(parentComment);
					}
					Query query = new Query();
					query.addCriteria(Criteria.where("id").in(comment.getAncestorsId()));
					ancestors = this.mongoTemplate.find(query, DiscussReply.class);
					for (DiscussReply ancestor : ancestors) {
						ancestor.setChildrenCount(ancestor.getChildrenCount() + 1);
						mongoTemplate.save(ancestor);
					}
					sendMailForReplyOnReply(parentComment, user);

				} else {
					discuss.setDirectReplyCount(discuss.getDirectReplyCount() + 1);
					sendMailForReplyOnDiscuss(discuss, user, comment);
				}

				discuss.setAggrReplyCount(discuss.getAggrReplyCount() + 1);
				mongoTemplate.save(discuss);
				mongoTemplate.save(comment);
				Util.logStats(mongoTemplate, req, "new Comment", user.getId(), user.getEmail(), discuss.getId(),
						parentComment != null ? parentComment.getId() : null, null, null, "new comment added",
						"COMMUNITY");
				logHandler.addLog(comment, ActivityLogConstants.CRUD_TYPE_CREATE, req);

				logger.debug("new answer posted successfully with replyId = " + comment.getId());
			} else {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(getDiscussDetailById(discussId, req));

	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/editReply" }, consumes = { "application/json" })
	@ResponseBody
	public Object editReply(@RequestBody DiscussReply comment, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		User user = Util.getSessionUser(req);
		DiscussReply oldComment = mongoTemplate.findById(new ObjectId(comment.getId()), DiscussReply.class);
		if (null == oldComment) {
			throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
		}
		if (null == user || (!BYConstants.USER_ROLE_EDITOR.equals(user.getUserRoleId())
				&& !BYConstants.USER_ROLE_SUPER_USER.equals(user.getUserRoleId())
				&& !oldComment.getUserId().equals(user.getId()))) {
			throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
		}
		try {
			if (SessionController.checkCurrentSessionFor(req, "COMMENT")) {
				Query query = new Query();
				query.addCriteria(Criteria.where("userId").is(user.getId()));
				UserProfile profile = mongoTemplate.findOne(query, UserProfile.class);
				// oldComment.setUserProfile(profile);
				oldComment.setText(comment.getText());
			} else {
				throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
			}

			Util.logStats(mongoTemplate, req, "Edit comment", oldComment.getId(), user.getEmail(),
					comment.getDiscussId(), null, null, null, "editing the comment", "COMMUNITY");
			mongoTemplate.save(oldComment);
			logHandler.addLog(oldComment, ActivityLogConstants.CRUD_TYPE_UPDATE, req);

			logger.debug("new answer posted successfully with replyId = " + comment.getId());
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(getDiscussDetailById(oldComment.getDiscussId(), req));
	}

	/**
	 * API for posting a reply of type answer
	 * 
	 * @param answer
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.POST }, params = "type=1", consumes = { "application/json" })
	@ResponseBody
	public Object submitAnswer(@RequestBody DiscussReply answer, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		String discussId = answer.getDiscussId();
		try {
			Discuss discuss = discussRepository.findOne(discussId);
			if (null != discuss) {
				answer.setDiscussId(discuss.getId());
				answer.setReplyType(DiscussConstants.REPLY_TYPE_ANSWER);
				answer.setContentType(Util.getDiscussContentType(discuss.getDiscussType()));
				answer.setParentReplyId(null);
				User user = Util.getSessionUser(req);
				if (null != user && SessionController.checkCurrentSessionFor(req, "ANSWER")) {
					answer.setUserId(user.getId());
					answer.setUserName(user.getUserName());
					Query query = new Query();
					query.addCriteria(Criteria.where("userId").is(user.getId()));
					UserProfile profile = mongoTemplate.findOne(query, UserProfile.class);
					answer.setUserProfile(profile);
				} else {
					throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
				}
				discuss.setAggrReplyCount(discuss.getAggrReplyCount() + 1);
				discuss.setDirectReplyCount(discuss.getDirectReplyCount() + 1);
				mongoTemplate.save(discuss);
				mongoTemplate.save(answer);
				Util.logStats(mongoTemplate, req, "Add new answer", user.getId(), user.getEmail(), discuss.getId(),
						null, null, null, "adding new answer", "COMMUNITY");
				logHandler.addLog(answer, ActivityLogConstants.CRUD_TYPE_CREATE, req);
				sendMailForReplyOnDiscuss(discuss, user, answer);
				logger.debug("new answer posted successfully with replyId = " + answer.getId());
			} else {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(getDiscussDetailById(discussId, req));
	}

	private DiscussDetailResponse getDiscussDetailById(String discussId,
			HttpServletRequest req) throws Exception {
		
		DiscussDetailResponse response = new DiscussDetailResponse();
		try {
			List<DiscussReply> replies = null;
			List<DiscussReply> topLevelReplies = null;
			Map<String, List<DiscussReply>> replyGroup = null;

			Discuss discuss = discussRepository.findOne(discussId);
			if (null != discuss) {
				response.addDiscuss(discuss, Util.getSessionUser(req));

				Query query = new Query();
				query.addCriteria(Criteria.where("discussId").is(discussId))
						.addCriteria(
								Criteria.where("status").is(
										DiscussConstants.REPLY_STATUS_ACTIVE));
				query.with(new Sort(Sort.Direction.DESC,
						new String[] { "createdAt" }));
				replies = this.mongoTemplate.find(query,
						DiscussReply.class);

				if(replies.size() > 0){
					// Convert all replies into groups as per parent id 
					replyGroup = replies.stream()
							.collect(Collectors.groupingBy(DiscussReply::getParentReplyId));
					// Since group is based on parent ids, all top level replies will be in a seprate group
					topLevelReplies = replyGroup.get("");
				}
				if(topLevelReplies != null){
					// Add top level replies in there respective groups and if there are no sub replies 
					// then create a new group for those top level replies and add then in that group 
					for(DiscussReply topReply : topLevelReplies){
						if( replyGroup.get(topReply.getId()) !=null){
							replyGroup.get(topReply.getId()).add(topReply);
							Collections.sort(replyGroup.get(topReply.getId()));
						}
						else{
							List<DiscussReply> tempList = new ArrayList<DiscussReply>();
							tempList.add(topReply);
							replyGroup.put(topReply.getId(), tempList);
						}
					}
					// Since all replies are added in respective groups,
					// now we can delete top level replies to remove duplicacy
					replyGroup.remove("");
					
					Map<String, List <DiscussReply> > repliesSorted = replyGroup.entrySet().stream()
					.sorted(Collections.reverseOrder( new Comparator< Map.Entry< String, List<DiscussReply> > >() { 
						@Override
						public int compare(Entry<String, List<DiscussReply>> o1, Entry<String, List<DiscussReply>> o2) {
							DiscussReply reply1 = o1.getValue().get( o1.getValue().size() - 1);
							DiscussReply reply2 = o2.getValue().get( o2.getValue().size() - 1);
							return reply1.compareTo(reply2);
						} 
					}))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
							(oldValue, newValue) -> oldValue, LinkedHashMap::new));

					response.addReplies(replies, Util.getSessionUser(req));
					response.addSortedReplies(repliesSorted, Util.getSessionUser(req));
				}
			} else {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return response.getResponse();
	}

	private void sendMailForReplyOnDiscuss(Discuss discuss, User user,
			DiscussReply reply) {
		try {
			if (!discuss.getUserId().equals(user.getId())) {
				ResourceUtil resourceUtil = new ResourceUtil(
						"mailTemplate.properties");
				String title = !Util.isEmpty(discuss.getTitle()) ? discuss
						.getTitle() : discuss.getText();
				if (Util.isEmpty(title) && discuss.getLinkInfo() != null) {
					title = !Util.isEmpty(discuss.getLinkInfo().getTitle()) ? discuss
							.getLinkInfo().getTitle() : discuss.getLinkInfo()
							.getDescription();
					title = !Util.isEmpty(title) ? title : discuss
							.getLinkInfo().getUrl();
				}
				if (Util.isEmpty(title)) {
					title = "<<Your post>>";
				}
				String userName = !Util.isEmpty(discuss.getUsername()) ? discuss
						.getUsername() : "Anonymous User";
				String commentedBy = !Util.isEmpty(user.getUserName()) ? user
						.getUserName() : "Anonymous User";
				String replyTypeString = (reply.getReplyType() == DiscussConstants.REPLY_TYPE_ANSWER) ? "an answer"
						: "comment";
				String path = reply.getUrl();
				String body = MessageFormat.format(
						resourceUtil.getResource("contentCommentedBy"),
						userName, commentedBy, title, path, path);
				MailHandler
						.sendMailToUserId(
								discuss.getUserId(),
								replyTypeString
										+ " is posted on your content at beautifulYears.com",
								body);
			}
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}

	}

	private void sendMailForReplyOnReply(DiscussReply reply, User user) {
		try {
			if (!reply.getUserId().equals(user.getId())) {
				ResourceUtil resourceUtil = new ResourceUtil(
						"mailTemplate.properties");
				String userName = !Util.isEmpty(reply.getUserName()) ? reply
						.getUserName() : "Anonymous User";
				String commentedBy = !Util.isEmpty(user.getUserName()) ? user
						.getUserName() : "Anonymous User";
				String replyString = "previous comment";
				String path = reply.getUrl();
				String replyText = Util.isEmpty(reply.getText()) ? "<<Your reply>>"
						: reply.getText();
				String body = MessageFormat.format(
						resourceUtil.getResource("replyCommentedBy"), userName,
						commentedBy, replyString, replyText, path, path);
				MailHandler
						.sendMailToUserId(
								reply.getUserId(),
								"A comment is posted on your comment at beautifulYears.com",
								body);
			}
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}

	}

}
