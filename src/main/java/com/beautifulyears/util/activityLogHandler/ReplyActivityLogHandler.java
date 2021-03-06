/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;

import org.jsoup.Jsoup;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.DiscussReply;
import com.beautifulyears.domain.User;
import com.beautifulyears.util.Util;

/**
 * @author Nitin
 *
 */
public class ReplyActivityLogHandler extends ActivityLogHandler<DiscussReply> {

	public ReplyActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(DiscussReply entity, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		DiscussReply reply = (DiscussReply) entity;
		log.setActivityTime(new Date());
		int replyType = ActivityLogConstants.ACTIVITY_TYPE_REPLY_COMMENT;
		if (DiscussConstants.REPLY_TYPE_COMMENT == reply.getReplyType()) {
			log.setEntityId(reply.getDiscussId());
			replyType = ActivityLogConstants.ACTIVITY_TYPE_REPLY_COMMENT;
		} else if (DiscussConstants.REPLY_TYPE_ANSWER == reply.getReplyType()) {
			log.setEntityId(reply.getDiscussId());
			replyType = ActivityLogConstants.ACTIVITY_TYPE_REPLY_ANSWER;
		} else if (DiscussConstants.REPLY_TYPE_REVIEW == reply.getReplyType()
				&& DiscussConstants.CONTENT_TYPE_INSTITUTION_HOUSING == reply
						.getContentType()) {
			log.setEntityId(reply.getDiscussId());
			replyType = ActivityLogConstants.ACTIVITY_TYPE_REPLY_HOUSING_REVIEW;
		} else if (DiscussConstants.REPLY_TYPE_REVIEW == reply.getReplyType()
				&& (DiscussConstants.CONTENT_TYPE_INSTITUTION_SERVICES == reply
						.getContentType() || DiscussConstants.CONTENT_TYPE_INDIVIDUAL_PROFESSIONAL == reply
						.getContentType())) {
			log.setEntityId(reply.getDiscussId());
			replyType = ActivityLogConstants.ACTIVITY_TYPE_REPLY_PROFILE_REVIEW;
		}

		log.setActivityType(replyType);
		log.setCrudType(crudType);
		log.setDetails("reply id = " + reply.getId() + "  "
				+ (details == null ? "" : details));
		log.setRead(false);
		log.setTitleToDisplay(getReplyTitle(reply));
		if (null != currentUser) {
			log.setUserId(currentUser.getId());
			if(currentUser.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL){
				log.setCurrentUserEmailId(currentUser.getEmail());
			}else if(currentUser.getUserIdType() == BYConstants.USER_ID_TYPE_PHONE){
				log.setCurrentUserEmailId(currentUser.getPhoneNumber());
			}
		}
		return log;
	}

	private String getReplyTitle(DiscussReply reply) {
		String title = "--------";

		if (!Util.isEmpty(reply.getText())) {
			org.jsoup.nodes.Document doc = Jsoup.parse(reply.getText());
			String domText = doc.text();
			title = domText;
			if (domText.length() > DiscussConstants.DISCUSS_TRUNCATION_LENGTH) {
				title = domText;
			}
		}

		return Util.truncateText(title);
	}

}
