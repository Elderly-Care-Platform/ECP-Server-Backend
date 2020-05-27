/**
 * 
 */
package com.beautifulyears.rest.likeHandler;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.domain.DiscussReply;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.DiscussLikeRepository;
import com.beautifulyears.repository.DiscussReplyRepository;
import com.beautifulyears.rest.HousingController;
import com.beautifulyears.rest.SessionController;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.Util;

/**
 * @author Nitin
 *
 */
@Controller
@RequestMapping("/discussReplyLike")
public class DiscussReplyLikeController extends LikeController<DiscussReply> {

	private Logger logger = Logger.getLogger(DiscussReplyLikeController.class);
	private DiscussReplyRepository discussReplyRepository;

	@Autowired
	public DiscussReplyLikeController(
			DiscussLikeRepository discussLikeRepository,
			DiscussReplyRepository discussReplyRepository,
			MongoTemplate mongoTemplate) {
		super(discussLikeRepository, mongoTemplate);
		this.discussReplyRepository = discussReplyRepository;
	}

	@RequestMapping(method = { RequestMethod.POST }, params = "type=1")
	@ResponseBody
	public Object submitCommentLike(
			@RequestParam(value = "replyId", required = true) String replyId,
			@RequestParam(value = "url", required = true) String url,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(likeContent(replyId,
				String.valueOf(DiscussConstants.REPLY_TYPE_COMMENT), url, req,
				res));

	}

	@RequestMapping(method = { RequestMethod.POST }, params = "type=2")
	@ResponseBody
	public Object submitAnswerLike(
			@RequestParam(value = "replyId", required = true) String replyId,
			@RequestParam(value = "url", required = false) String url,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(likeContent(replyId,
				String.valueOf(DiscussConstants.REPLY_TYPE_ANSWER), url, req,
				res));

	}

	@RequestMapping(method = { RequestMethod.PUT }, params = "type=1")
	@ResponseBody
	public Object submitCommentUnLike(
			@RequestParam(value = "replyId", required = true) String replyId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(unlikeContent(replyId,
				String.valueOf(DiscussConstants.REPLY_TYPE_COMMENT), req, res));
	}

	@RequestMapping(method = { RequestMethod.PUT }, params = "type=2")
	@ResponseBody
	public Object submitAnswerUnLike(
			@RequestParam(value = "replyId", required = true) String replyId,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(unlikeContent(replyId,
				String.valueOf(DiscussConstants.REPLY_TYPE_ANSWER), req, res));

	}

	@Override
	Object likeContent(String id, String type, String url,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		int replyType = Integer.parseInt(type);
		DiscussReply reply = null;
		try {
			User user = Util.getSessionUser(req);
			reply = (DiscussReply) discussReplyRepository.findOne(id);
			if (null != reply) {
				if (null == user) {
					throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
				} else if (SessionController
						.checkCurrentSessionFor(req, "LIKE")) {
					if (reply.getReplyType() == replyType) {
						if (reply.getLikedBy().contains(user.getId())) {
							throw new BYException(
									BYErrorCodes.DISCUSS_ALREADY_LIKED_BY_USER);
						} else {
							submitLike(user, id,
									DiscussConstants.CONTENT_TYPE_DISCUSS);
							reply.getLikedBy().add(user.getId());
							sendMailForLike(reply, user, url);
							discussReplyRepository.save(reply);
							logHandler.addLog(reply,
									ActivityLogConstants.CRUD_TYPE_CREATE, req);

						}
					}
				}
				reply.setLikeCount(reply.getLikedBy().size());
				if (null != user && reply.getLikedBy().contains(user.getId())) {
					reply.setLikedByUser(true);
				}
			} else {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
			Util.logStats(HousingController.staticMongoTemplate, req,
					"Like on comment", user.getId(), user.getEmail(),
					reply.getId(), null, null, null,
					"Like on content with type " + reply.getContentType(),
					"COMMUNITY");
		} catch (Exception e) {
			Util.handleException(e);
		}

		return reply;
	}

	@Override
	Object unlikeContent(String id, String type,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		int replyType = Integer.parseInt(type);
		DiscussReply reply = null;
		try {
			User user = Util.getSessionUser(req);
			reply = (DiscussReply) discussReplyRepository.findOne(id);
			if (null != reply) {
				if (null == user) {
					throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
				} else if (SessionController
						.checkCurrentSessionFor(req, "LIKE")) {
					if (reply.getReplyType() == replyType) {
						if (reply.getLikedBy().contains(user.getId())) {
							submitUnLike(user, id, DiscussConstants.CONTENT_TYPE_DISCUSS);
							reply.getLikedBy().remove(user.getId());
							discussReplyRepository.save(reply);
							
							logHandler.addLog(reply, ActivityLogConstants.CRUD_TYPE_CREATE, req);
						} else {
							throw new BYException(BYErrorCodes.DISCUSS_ALREADY_UNLIKED_BY_USER);
						}
					}
				}
				reply.setLikeCount(reply.getLikedBy().size());
				reply.setLikedByUser(false);
			} else {
				throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
			}
			Util.logStats(HousingController.staticMongoTemplate, req,
					"Unlike on comment", user.getId(), user.getEmail(),
					reply.getId(), null, null, null,
					"Unlike on content with type " + reply.getContentType(),
					"COMMUNITY");
		} catch (Exception e) {
			Util.handleException(e);
		}

		return reply;
	}

	@Override
	void sendMailForLike(DiscussReply LikedEntity, User user, String url) {
		try {
			if (!LikedEntity.getUserId().equals(user.getId())) {
				ResourceUtil resourceUtil = new ResourceUtil(
						"mailTemplate.properties");
				String title = LikedEntity.getText();
				String userName = !Util.isEmpty(LikedEntity.getUserName()) ? LikedEntity
						.getUserName() : "Anonymous User";
				String replyTypeString = (LikedEntity.getReplyType() == DiscussConstants.REPLY_TYPE_ANSWER) ? "answer"
						: "comment";
				String likedBy = !Util.isEmpty(user.getUserName()) ? user
						.getUserName() : "Anonymous User";
				String body = MessageFormat.format(
						resourceUtil.getResource("likedBy"), userName,
						replyTypeString, title, likedBy, url, url);
				MailHandler.sendMailToUserId(LikedEntity.getUserId(), "Your "
						+ replyTypeString + " was liked on joyofage.org",
						body);
			}
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}
	}

}
