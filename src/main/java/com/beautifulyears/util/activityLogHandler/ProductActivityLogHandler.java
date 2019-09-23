/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class ProductActivityLogHandler extends ActivityLogHandler<Product> {

	public ProductActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(Product product, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (product != null) {
			log.setActivityTime(new Date());
			log.setActivityType(ActivityLogConstants.ACTIVITY_TYPE_PRODUCT);
			log.setCrudType(crudType);
			log.setDetails("product id = " + product.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(product.getId());
			log.setRead(false);
			log.setTitleToDisplay(product.getName());
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
