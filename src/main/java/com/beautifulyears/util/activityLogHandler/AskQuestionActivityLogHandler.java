/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class AskQuestionActivityLogHandler extends ActivityLogHandler<AskQuestion> {

	public AskQuestionActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(AskQuestion askQuestion, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (askQuestion != null) {
			log.setActivityTime(new Date());
			log.setActivityType(ActivityLogConstants.ACTIVITY_TYPE_PRODUCT);
			log.setCrudType(crudType);
			log.setDetails("question id = " + askQuestion.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(askQuestion.getId());
			log.setRead(false);
			log.setTitleToDisplay(askQuestion.getQuestion());
			if (null != currentUser) {
				log.setUserId(currentUser.getId());
				if(currentUser.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL){
					log.setCurrentUserEmailId(currentUser.getEmail());
				}else if(currentUser.getUserIdType() == BYConstants.USER_ID_TYPE_PHONE){
					log.setCurrentUserEmailId(currentUser.getPhoneNumber());
				}
			}
		}
		return log;
	}
}
