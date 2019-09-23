/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class AskCategoryActivityLogHandler extends ActivityLogHandler<AskCategory> {

	public AskCategoryActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(AskCategory askCat, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (askCat != null) {
			log.setActivityTime(new Date());
			log.setActivityType(ActivityLogConstants.ACTIVITY_TYPE_PRODUCT_CATEGORY);
			log.setCrudType(crudType);
			log.setDetails("ask category id = " + askCat.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(askCat.getId());
			log.setRead(false);
			log.setTitleToDisplay(askCat.getName());
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
