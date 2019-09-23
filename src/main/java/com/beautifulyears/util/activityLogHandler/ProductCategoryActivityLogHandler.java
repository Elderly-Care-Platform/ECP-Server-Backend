/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class ProductCategoryActivityLogHandler extends ActivityLogHandler<ProductCategory> {

	public ProductCategoryActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(ProductCategory productCat, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (productCat != null) {
			log.setActivityTime(new Date());
			log.setActivityType(ActivityLogConstants.ACTIVITY_TYPE_PRODUCT_CATEGORY);
			log.setCrudType(crudType);
			log.setDetails("product category id = " + productCat.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(productCat.getId());
			log.setRead(false);
			log.setTitleToDisplay(productCat.getName());
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
