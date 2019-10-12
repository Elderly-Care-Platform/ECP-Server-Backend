package com.beautifulyears.rest;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
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
import com.beautifulyears.domain.DiscussReply;
import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.domain.UserRating;
import com.beautifulyears.domain.ServiceReview;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.repository.DiscussReplyRepository;
import com.beautifulyears.repository.HousingRepository;
import com.beautifulyears.repository.ServiceReviewRepository;
import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.repository.UserRatingRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.DiscussDetailResponse;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.rest.response.ServiceReviewResponse;
import com.beautifulyears.rest.response.ServiceReviewResponse.ServiceReviewPage;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ReplyActivityLogHandler;

@Controller
@RequestMapping("/reviewRate")
public class ReviewController {
	private Logger logger = Logger.getLogger(ReviewController.class);
	private DiscussReplyRepository discussReplyRepository;
	private UserRatingRepository userRatingRepository;
	private HousingRepository housingRepository;
	private MongoTemplate mongoTemplate;
	private UserProfileRepository userProfileRepository;
	private ActivityLogHandler<DiscussReply> logHandler;
	private ServiceReviewRepository serviceRevRepo;

	@Autowired
	public ReviewController(DiscussReplyRepository discussReplyRepository, UserRatingRepository userRatingRepository,
			UserProfileRepository userProfileRepository, HousingRepository housingRepository,
			ServiceReviewRepository serviceRevRepo, MongoTemplate mongoTemplate) {
		this.discussReplyRepository = discussReplyRepository;
		this.userRatingRepository = userRatingRepository;
		this.userProfileRepository = userProfileRepository;
		this.housingRepository = housingRepository;
		this.serviceRevRepo = serviceRevRepo;
		this.mongoTemplate = mongoTemplate;
		logHandler = new ReplyActivityLogHandler(mongoTemplate);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getReviewRate(@RequestParam(value = "reviewContentType", required = true) Integer contentType,
			@RequestParam(value = "associatedId", required = true) String associatedId,
			@RequestParam(value = "userId", required = false) String userId,
			@RequestParam(value = "verified", required = false) Boolean verified, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		List<DiscussReply> reviewsList = new ArrayList<DiscussReply>();
		DiscussDetailResponse responseHandler = new DiscussDetailResponse();
		List<String> filterCriteria = new ArrayList<String>();
		filterCriteria.add("reviewContentType = " + contentType);
		filterCriteria.add("associatedId = " + associatedId);
		filterCriteria.add("userId = " + userId);
		filterCriteria.add("verified = " + verified);

		if (null != contentType && null != associatedId) {
			Query q = new Query();
			q.addCriteria(Criteria.where("replyType").is(DiscussConstants.REPLY_TYPE_REVIEW).and("contentType")
					.is(contentType).and("discussId").is(associatedId));
			if (null != verified) {
				q.addCriteria(Criteria.where("verified").is(verified));
			}
			if (null != userId) {
				q.addCriteria(Criteria.where("userId").is(userId));
			}
			reviewsList = mongoTemplate.find(q, DiscussReply.class);
			Util.logStats(mongoTemplate, req, "get rate review", null, null, null, null, null, filterCriteria,
					"get rate and reviews ", "COMMUNITY");
		} else {
			throw new BYException(BYErrorCodes.MISSING_PARAMETER);
		}
		responseHandler.addReplies(reviewsList, Util.getSessionUser(req));
		return BYGenericResponseHandler.getResponse(responseHandler.getResponse());

	}

	@RequestMapping(method = { RequestMethod.POST }, value = "", consumes = { "application/json" })
	@ResponseBody
	public Object submitReviewRate(@RequestParam(value = "reviewContentType", required = true) Integer contentType,
			@RequestParam(value = "associatedId", required = true) String associatedId,
			@RequestBody DiscussReply reviewRate, HttpServletRequest req, HttpServletResponse res) throws Exception {

		LoggerUtil.logEntry();
		try {

			User user = Util.getSessionUser(req);

			DiscussReply newReview = reviewRate;
			if (null != user && SessionController.checkCurrentSessionFor(req, "RATE_REVIEW")) {
				if (null != contentType && null != associatedId && null != newReview) {
					List<String> filterCriteria = new ArrayList<String>();
					filterCriteria.add("reviewContentType = " + contentType);
					filterCriteria.add("associatedId = " + associatedId);
					if (isSelfAccessment(associatedId, contentType, user)) {
						throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
					}
					if (BYConstants.USER_ROLE_EDITOR.equals(user.getUserRoleId())
							|| BYConstants.USER_ROLE_SUPER_USER.equals(user.getUserRoleId())) {
						newReview.setVerified(true);
					}
					submitRating(contentType, associatedId, newReview, user);
					submitReview(contentType, associatedId, newReview, user);
					Util.logStats(mongoTemplate, req, "Submit Rating & review", user.getId(), user.getEmail(),
							associatedId, null, null, filterCriteria, "Submit new Rating & review", "COMMUNITY");
				} else {
					throw new BYException(BYErrorCodes.MISSING_PARAMETER);
				}
			} else {
				logger.debug("user must login to submit review aand ratings");
				throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return null;
	}

	private DiscussReply submitReview(Integer contentType, String associatedId, DiscussReply newReviewRate, User user) {
		DiscussReply review = null;
		review = this.getReview(contentType, associatedId, user);
		int operationType = ActivityLogConstants.CRUD_TYPE_CREATE;
		if (null == review) {
			review = new DiscussReply();
			review.setDiscussId(associatedId);
			review.setUrl(newReviewRate.getUrl());
			review.setContentType(contentType);
			review.setVerified(newReviewRate.isVerified());
			review.setUserRatingPercentage(newReviewRate.getUserRatingPercentage());
			review.setReplyType(DiscussConstants.REPLY_TYPE_REVIEW);
			review.setUserId(user.getId());
			review.setUserName(user.getUserName());
			Query query = new Query();
			query.addCriteria(Criteria.where("userId").is(user.getId()));
			UserProfile profile = mongoTemplate.findOne(query, UserProfile.class);
			review.setUserProfile(profile);
			sendMailForReview(review, user);
		} else {
			operationType = ActivityLogConstants.CRUD_TYPE_UPDATE;
		}
		review.setText(newReviewRate.getText());
		review.setUserRatingPercentage(newReviewRate.getUserRatingPercentage());
		discussReplyRepository.save(review);
		logHandler.addLog(review, operationType, "changing the review", user);
		updateAllDependantEntities(contentType, review, user);
		return review;
	}

	private UserRating submitRating(Integer contentType, String associatedId, DiscussReply reviewRate, User user) {
		UserRating rating = null;
		int operationType = ActivityLogConstants.CRUD_TYPE_CREATE;
		if (null != contentType && null != reviewRate && null != user) {
			rating = this.getRating(contentType, associatedId, user);
			if (null == rating && null != reviewRate.getUserRatingPercentage()) {
				rating = new UserRating();
				rating.setAssociatedId(associatedId);
				rating.setAssociatedContentType(contentType);
				rating.setUserId(user.getId());
				rating.setUserName(user.getUserName());
			} else {
				operationType = ActivityLogConstants.CRUD_TYPE_UPDATE;
			}
			if (null != reviewRate.getUserRatingPercentage()
					&& (reviewRate.getUserRatingPercentage() < 0 || reviewRate.getUserRatingPercentage() > 100)) {
				throw new BYException(BYErrorCodes.RATING_VALUE_INVALID);
			}
			rating.setRatingPercentage(reviewRate.getUserRatingPercentage());
			userRatingRepository.save(rating);
			// logHandler.addLog(reviewRate,
			// operationType,"changing the rating", user);
			updateAllDependantEntities(contentType, rating, user);
		} else {
			logger.debug("not updating any rating");
		}
		return rating;
	}

	private UserRating getRating(Integer contentType, String associatedId, User user) {
		Query query = new Query();
		query.addCriteria(Criteria.where("associatedContentType").is(contentType).and("associatedId").is(associatedId)
				.and("userId").is(user.getId()));
		return this.mongoTemplate.findOne(query, UserRating.class);
	}

	private DiscussReply getReview(Integer reviewContentType, String associatedId, User user) {
		Query query = new Query();
		query.addCriteria(Criteria.where("replyType").is(DiscussConstants.REPLY_TYPE_REVIEW).and("contentType")
				.is(reviewContentType).and("discussId").is(associatedId).and("userId").is(user.getId()));
		return this.mongoTemplate.findOne(query, DiscussReply.class);
	}

	private void updateAllDependantEntities(Integer contentType, UserRating rating, User user) {
		switch (contentType) {
		case DiscussConstants.CONTENT_TYPE_INDIVIDUAL_PROFESSIONAL:
		case DiscussConstants.CONTENT_TYPE_INSTITUTION_SERVICES:
			updateInstitutionRating(rating, user);
			break;
		case DiscussConstants.CONTENT_TYPE_INSTITUTION_HOUSING:
			updateHousingRating(rating, user);
			break;
		case DiscussConstants.CONTENT_TYPE_PRODUCT:
			updateProductRating(rating, user);
			break;
		default:
			throw new BYException(BYErrorCodes.REVIEW_TYPE_INVALID);
		}
	}

	private void updateAllDependantEntities(Integer contentType, DiscussReply review, User user) {
		switch (contentType) {
		case DiscussConstants.CONTENT_TYPE_INDIVIDUAL_PROFESSIONAL:
		case DiscussConstants.CONTENT_TYPE_INSTITUTION_SERVICES:
			updateInstitutionReviews(review, user);
			break;
		case DiscussConstants.CONTENT_TYPE_INSTITUTION_HOUSING:
			updateHousingReviews(review, user);
			break;
		case DiscussConstants.CONTENT_TYPE_PRODUCT:
			updateProductReviews(review, user);
			break;
		default:
			throw new BYException(BYErrorCodes.REVIEW_TYPE_INVALID);
		}
	}

	private void updateProductRating(UserRating rating, User currentUser) {

	}

	private void updateProductReviews(DiscussReply review, User currentUser) {

	}

	private void updateHousingRating(UserRating rating, User currentUser) {
		HousingFacility housing = this.housingRepository.findOne(rating.getAssociatedId());
		if (null != housing) {
			if (null == rating.getRatingPercentage() || 0 == rating.getRatingPercentage()) {
				housing.getRatedBy().remove(rating.getUserId());
			} else if (!housing.getRatedBy().contains(rating.getUserId())) {
				housing.getRatedBy().add(rating.getUserId());
			}
			TypedAggregation<UserRating> aggregation = newAggregation(UserRating.class,
					match(Criteria.where("associatedId").is(rating.getAssociatedId()).and("ratingPercentage").gt(0)
							.and("associatedContentType").is(rating.getAssociatedContentType())),
					group("associatedId").avg("ratingPercentage").as("ratingPercentage"));

			AggregationResults<UserRating> result = mongoTemplate.aggregate(aggregation, UserRating.class);
			List<UserRating> ratingAggregated = result.getMappedResults();
			if (ratingAggregated.size() > 0) {
				housing.setAggrRatingPercentage(ratingAggregated.get(0).getRatingPercentage());
			}
			if (currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_EDITOR)
					|| currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_SUPER_USER)) {
				housing.setVerified(true);
			}
			this.housingRepository.save(housing);
		}

	}

	private void updateInstitutionRating(UserRating rating, User currentUser) {
		UserProfile profile = this.userProfileRepository.findOne(rating.getAssociatedId());
		if (null != profile) {
			if (null == rating.getRatingPercentage() || 0 == rating.getRatingPercentage()) {
				profile.getRatedBy().remove(rating.getUserId());
			} else if (!profile.getRatedBy().contains(rating.getUserId())) {
				profile.getRatedBy().add(rating.getUserId());
			}
			TypedAggregation<UserRating> aggregation = newAggregation(UserRating.class,
					match(Criteria.where("associatedId").is(rating.getAssociatedId()).and("ratingPercentage").gt(0)
							.and("associatedContentType").is(rating.getAssociatedContentType())),
					group("associatedId").avg("ratingPercentage").as("ratingPercentage"));

			AggregationResults<UserRating> result = mongoTemplate.aggregate(aggregation, UserRating.class);
			List<UserRating> ratingAggregated = result.getMappedResults();
			if (ratingAggregated.size() > 0) {
				profile.setAggrRatingPercentage(ratingAggregated.get(0).getRatingPercentage());
			}
			if (currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_EDITOR)
					|| currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_SUPER_USER)) {
				profile.setVerified(true);
			}
			this.userProfileRepository.save(profile);
		}
	}

	private void updateHousingReviews(DiscussReply review, User currentUser) {
		HousingFacility housing = this.housingRepository.findOne(review.getDiscussId());
		if (null != housing) {
			if (Util.isEmpty(review.getText())) {
				housing.getReviewedBy().remove(review.getUserId());
			} else if (!housing.getReviewedBy().contains(review.getUserId())) {
				housing.getReviewedBy().add(review.getUserId());
			}
			if (currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_EDITOR)
					|| currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_SUPER_USER)) {
				housing.setVerified(true);
			}
			this.housingRepository.save(housing);

		}
	}

	private void updateInstitutionReviews(DiscussReply review, User currentUser) {
		UserProfile profile = this.userProfileRepository.findOne(review.getDiscussId());
		if (null != profile) {
			if (Util.isEmpty(review.getText())) {
				profile.getReviewedBy().remove(review.getUserId());
			} else if (!profile.getReviewedBy().contains(review.getUserId())) {
				profile.getReviewedBy().add(review.getUserId());
			}
			if (currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_EDITOR)
					|| currentUser.getUserRoleId().equals(BYConstants.USER_ROLE_SUPER_USER)) {
				profile.setVerified(true);
			}
			this.userProfileRepository.save(profile);

		}
	}

	private boolean isSelfAccessment(String associatedId, Integer contentType, User user) throws Exception {
		boolean isSelf = false;
		try {
			switch (contentType) {
			case DiscussConstants.CONTENT_TYPE_INDIVIDUAL_PROFESSIONAL:
			case DiscussConstants.CONTENT_TYPE_INSTITUTION_SERVICES:
				UserProfile userProfile = this.userProfileRepository.findByUserId(user.getId());
				if (null != userProfile && userProfile.getId().equals(associatedId)) {
					isSelf = true;
				}
				break;

			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return isSelf;
	}

	void sendMailForReview(DiscussReply review, User user) {
		try {
			switch (review.getContentType()) {
			case DiscussConstants.CONTENT_TYPE_INDIVIDUAL_PROFESSIONAL:
			case DiscussConstants.CONTENT_TYPE_INSTITUTION_SERVICES:
			case DiscussConstants.CONTENT_TYPE_INSTITUTION_HOUSING:
				UserProfile reviewedEntity = this.userProfileRepository.findOne(review.getDiscussId());
				if (!reviewedEntity.getUserId().equals(user.getId())) {
					ResourceUtil resourceUtil = new ResourceUtil("mailTemplate.properties");
					User profileUser = UserController.getUser(reviewedEntity.getUserId());
					String userName = !Util.isEmpty(profileUser.getUserName()) ? profileUser.getUserName()
							: "Anonymous User";
					String replyTypeString = "profile";
					String path = review.getUrl();
					String body = MessageFormat.format(resourceUtil.getResource("reviewOnProfile"), userName, path);
					MailHandler.sendMailToUserId(reviewedEntity.getUserId(),
							"Your " + replyTypeString + " was reviewed on beautifulYears.com", body);
				}
				break;
			default:

			}

		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}
	}

	/**
	 * Get reviews for services
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/service" }, produces = { "application/json" })
	@ResponseBody
	public Object getReviewPage(@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "serviceId", required = false) String serviceId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<ServiceReview> page = null;
		ServiceReviewPage serviceReviewPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = serviceRevRepo.getPage(searchTxt, serviceId, pageable);
			serviceReviewPage = ServiceReviewResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(serviceReviewPage);
	}

	/**
	 * Add service rating and reviews
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = { "/addServiceReview" }, consumes = { "application/json" })
	@ResponseBody
	public Object submitServiceReview(@RequestBody ServiceReview serviceReview, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (serviceReview != null && (Util.isEmpty(serviceReview.getId()))) {
				ServiceReview serviceRevExtracted = new ServiceReview(serviceReview.getServiceId(),
						serviceReview.getRating(), serviceReview.getReview(), serviceReview.getLikeCount(),
						serviceReview.getUnLikeCount(), serviceReview.getStatus(), currentUser.getUserName(),
						currentUser.getId(), serviceReview.getParentReviewId());

				Query query = new Query();
				query.addCriteria(Criteria.where("id").is(serviceRevExtracted.getServiceId()));
				UserProfile userProfile = null;
				userProfile = mongoTemplate.findOne(query, UserProfile.class);

				if (userProfile != null) {
					userProfile.getReviewedBy().add(currentUser.getId());
					float totRating = userProfile.getAggrRatingPercentage();
					totRating = (totRating + serviceRevExtracted.getRating()) / userProfile.getReviewedBy().size();
					userProfile.setAggrRatingPercentage(totRating);
					mongoTemplate.save(userProfile);
				}

				serviceReview = serviceRevRepo.save(serviceRevExtracted);
				// logHandlerRev.addLog(productReview, ActivityLogConstants.CRUD_TYPE_CREATE,
				// request);
				logger.info("new service review entity created with ID: " + serviceReview.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Service Review added.", currentUser.getId(), currentUser.getEmail(),
				serviceReview.getId(), null, null, null, "new service review entity is added", "SERVICE_REVIEW");
		return BYGenericResponseHandler.getResponse(serviceReview);
	}

	/**
	 * Add service rating and reviews
	 */
	@RequestMapping(method = { RequestMethod.PUT }, value = { "/service/likeUnlikeReview" }, consumes = {
			"application/json" })
	@ResponseBody
	public Object serviceLikeUnlikeReview(@RequestParam(value = "reviewId", required = true) String reviewId,
			@RequestParam(value = "like", required = true) Boolean isLike, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		ServiceReview reviewUpdated = null;
		if (null != currentUser) {

			Query query = new Query();
			query.addCriteria(Criteria.where("id").is(reviewId));
			ServiceReview serviceRevExtracted = null;
			serviceRevExtracted = mongoTemplate.findOne(query, ServiceReview.class);
			List<String> users = new ArrayList<String>();
			if (serviceRevExtracted != null) {

				if (isLike) {
					if (serviceRevExtracted.getLikeCount() != null
							&& serviceRevExtracted.getLikeCount().contains(currentUser.getId())) {
						throw new BYException(BYErrorCodes.USER_ALREADY_EXIST);
					} else {
						if (serviceRevExtracted.getLikeCount() != null) {
							users = serviceRevExtracted.getLikeCount();
						}
						users.add(currentUser.getId());
						serviceRevExtracted.setLikeCount(users);
					}
				} else {
					if (serviceRevExtracted.getUnLikeCount() != null
							&& serviceRevExtracted.getUnLikeCount().contains(currentUser.getId())) {
						throw new BYException(BYErrorCodes.USER_ALREADY_EXIST);
					} else {
						if (serviceRevExtracted.getUnLikeCount() != null) {
							users = serviceRevExtracted.getUnLikeCount();
						}
						users.add(currentUser.getId());
						serviceRevExtracted.setUnLikeCount(users);
					}
				}

				reviewUpdated = serviceRevRepo.save(serviceRevExtracted);
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}

		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}

		Util.logStats(mongoTemplate, request, "Service Review Like/Unliked.", currentUser.getId(),
				currentUser.getEmail(), reviewUpdated.getId(), null, null, null, "new Service Review Like/Unliked.",
				"SERVICE_REVIEW");
		return BYGenericResponseHandler.getResponse(reviewUpdated);
	}

}
