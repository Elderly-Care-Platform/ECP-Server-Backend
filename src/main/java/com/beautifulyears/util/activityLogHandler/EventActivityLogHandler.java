/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;

import org.jsoup.Jsoup;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.EventConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.User;
import com.beautifulyears.util.Util;

/**
 * @author Nitin
 *
 */
public class EventActivityLogHandler extends ActivityLogHandler<Event> {

	public EventActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(Event event, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (event != null) {
			log.setActivityTime(new Date());
			int eventType = event.getEventType();
			
			log.setActivityType(eventType);
			log.setCrudType(crudType);
			log.setDetails("event id = " + event.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(event.getId());
			log.setRead(false);
			log.setTitleToDisplay(event.getTitle());
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
