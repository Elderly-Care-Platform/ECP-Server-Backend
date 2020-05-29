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
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.DiscussLikeRepository;
import com.beautifulyears.repository.DiscussRepository;
import com.beautifulyears.rest.HousingController;
import com.beautifulyears.rest.SessionController;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussResponse;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.Util;

/**
 * @author Nitin
 *
 */
@Controller
@RequestMapping("/discussLike")
public class DiscussLikeController extends LikeController<Discuss> {

	private DiscussRepository discussRepository;
	private Logger logger = Logger.getLogger(DiscussLikeController.class);

	@Autowired
	public DiscussLikeController(DiscussLikeRepository discussLikeRepository,
			DiscussRepository discussRepository, MongoTemplate mongoTemplate) {
		super(discussLikeRepository, mongoTemplate);
		this.discussRepository = discussRepository;
	}

	@Override
	@RequestMapping(method = { RequestMethod.POST }, params = "type=0")
	@ResponseBody
	Object likeContent(
			@RequestParam(value = "discussId", required = true) String id,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "url", required = true) String url,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		Object response = null;
		try {
			DiscussResponse discussResponse = new DiscussResponse();
			Discuss discuss = null;
			User user = Util.getSessionUser(req);

			if (null == user) {
				throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
			} else if (SessionController.checkCurrentSessionFor(req, "LIKE")) {

				discuss = (Discuss) discussRepository.findOne(id);
				if (discuss != null) {
					if (discuss.getLikedBy().contains(user.getId())) {
						throw new BYException(
								BYErrorCodes.DISCUSS_ALREADY_LIKED_BY_USER);
					} else {
						submitLike(user, id,
								DiscussConstants.CONTENT_TYPE_DISCUSS);
						discuss.getLikedBy().add(user.getId());
						sendMailForLike(discuss, user, url);
						discussRepository.save(discuss);
						logHandler.addLog(discuss,
								ActivityLogConstants.CRUD_TYPE_CREATE, req);
						Util.logStats(HousingController.staticMongoTemplate,
								req, "Like on content", user.getId(),
								user.getEmail(), discuss.getId(), null, null,
								null, "Like on content", "COMMUNITY");
						logger.debug("discuss content liked successfully");

						response = BYGenericResponseHandler
								.getResponse(discussResponse.getDiscussEntity(
										discuss, user));
					}
				} else {
					throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
				}

			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return response;
	}

	@Override
	@RequestMapping(method = { RequestMethod.PUT }, params = "type=0")
	@ResponseBody
	Object unlikeContent(
			@RequestParam(value = "discussId", required = true) String id,
			@RequestParam(value = "type", required = false) String type,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		Object response = null;
		try {
			DiscussResponse discussResponse = new DiscussResponse();
			Discuss discuss = null;
			User user = Util.getSessionUser(req);

			if (null == user) {
				throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
			} else if (SessionController.checkCurrentSessionFor(req, "LIKE")) {

				discuss = (Discuss) discussRepository.findOne(id);
				if (discuss != null) {
					if (discuss.getLikedBy().contains(user.getId())) {
						submitUnLike(user, id, DiscussConstants.CONTENT_TYPE_DISCUSS);
						discuss.getLikedBy().remove(user.getId());
						discussRepository.save(discuss);

						logHandler.addLog(discuss,
								ActivityLogConstants.CRUD_TYPE_CREATE, req);
						Util.logStats(HousingController.staticMongoTemplate, req, "Unlike on content", user.getId(),
								user.getEmail(), discuss.getId(), null, null, null, "Unlike on content", "COMMUNITY");
						logger.debug("discuss content liked successfully");

						response = BYGenericResponseHandler
								.getResponse(discussResponse.getDiscussEntity(
										discuss, user));
					} else {
						throw new BYException(BYErrorCodes.DISCUSS_ALREADY_UNLIKED_BY_USER);
						
					}
				} else {
					throw new BYException(BYErrorCodes.DISCUSS_NOT_FOUND);
				}
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return response;
	}

	@Override
	void sendMailForLike(Discuss LikedEntity, User user, String url) {
		LoggerUtil.logEntry();
		try {
			if (null != LikedEntity && null != LikedEntity.getUserId()
					&& !LikedEntity.getUserId().equals(user.getId())) {
				ResourceUtil resourceUtil = new ResourceUtil(
						"mailTemplate.properties");
				String title = !Util.isEmpty(LikedEntity.getTitle()) ? LikedEntity
						.getTitle() : LikedEntity.getText();
				if (Util.isEmpty(title) && LikedEntity != null
						&& LikedEntity.getLinkInfo() != null) {
					title = !Util.isEmpty(LikedEntity.getLinkInfo().getTitle()) ? LikedEntity
							.getLinkInfo().getTitle() : LikedEntity
							.getLinkInfo().getDescription();
					title = !Util.isEmpty(title) ? title : LikedEntity
							.getLinkInfo().getUrl();
				}
				if (Util.isEmpty(title)) {
					title = "<<Your post>>";
				}
				String userName = !Util.isEmpty(LikedEntity.getUsername()) ? LikedEntity
						.getUsername() : "Anonymous User";
				String likingUser = !Util.isEmpty(user.getUserName()) ? user
						.getUserName() : "Anonymous User";
				String body = MessageFormat.format(
						resourceUtil.getResource("likedBy"), userName,
						"content", title, likingUser, url, url);
				MailHandler.sendMailToUserId(LikedEntity.getUserId(),
						"Your content was liked on joyofage.org", body);
			}
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}
	}

}
