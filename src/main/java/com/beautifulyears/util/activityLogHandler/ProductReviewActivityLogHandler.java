/**
 * 
 */
package com.beautifulyears.util.activityLogHandler;

import java.util.Date;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ActivityLog;
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class ProductReviewActivityLogHandler extends ActivityLogHandler<ProductReview> {

	public ProductReviewActivityLogHandler(MongoTemplate mongoTemplate) {
		super(mongoTemplate);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ActivityLog getEntityObject(ProductReview productRev, int crudType,
			User currentUser, String details) {
		ActivityLog log = new ActivityLog();
		if (productRev != null) {
			log.setActivityTime(new Date());
			log.setActivityType(ActivityLogConstants.ACTIVITY_TYPE_PRODUCT_REVIEW);
			log.setCrudType(crudType);
			log.setDetails("product cattegory id = " + productRev.getId() + "  " + (details == null ? "" : details));
			log.setEntityId(productRev.getId());
			log.setRead(false);
			log.setTitleToDisplay("Product Review");
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
