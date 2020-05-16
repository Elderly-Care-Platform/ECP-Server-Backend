package com.beautifulyears.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
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
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.domain.ProductRating;
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.ProductRepository;
import com.beautifulyears.repository.ProductCategoryRepository;
import com.beautifulyears.repository.ProductRatingRepository;
import com.beautifulyears.repository.ProductReviewRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.ProductResponse;
import com.beautifulyears.rest.response.ProductCategoryResponse;
import com.beautifulyears.rest.response.ProductCategoryResponse.ProductCategoryPage;
import com.beautifulyears.rest.response.ProductResponse.ProductPage;
import com.beautifulyears.rest.response.ProductReviewResponse;
import com.beautifulyears.rest.response.ProductReviewResponse.ProductReviewPage;
import com.beautifulyears.rest.response.PageImpl;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ProductActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ProductCategoryActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.ProductReviewActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.SharedActivityLogHandler;

/**
 * The REST based service for managing "product"
 * 
 * @author jumpstart
 *
 */
@Controller
@RequestMapping(value = { "/product" })
public class ProductController {
	private static final Logger logger = Logger.getLogger(ProductController.class);
	private ProductRepository productRepository;
	private ProductCategoryRepository productCatRepo;
	private ProductReviewRepository productRevRepo;
	private ProductRatingRepository productRatingRepo;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Product> logHandler;
	ActivityLogHandler<ProductCategory> logHandlerCat;
	ActivityLogHandler<ProductReview> logHandlerRev;
	ActivityLogHandler<Object> shareLogHandler;

	@Autowired
	public ProductController(ProductRepository productRepository, UserRepository userRepository,
			ProductCategoryRepository productCatRepo, ProductReviewRepository productRevRepo,
			ProductRatingRepository productRatingRepo, MongoTemplate mongoTemplate) {
		this.productRepository = productRepository;
		this.productCatRepo = productCatRepo;
		this.productRevRepo = productRevRepo;
		this.productRatingRepo = productRatingRepo;
		this.mongoTemplate = mongoTemplate;
		logHandler = new ProductActivityLogHandler(mongoTemplate);
		logHandlerCat = new ProductCategoryActivityLogHandler(mongoTemplate);
		logHandlerRev = new ProductReviewActivityLogHandler(mongoTemplate);
		shareLogHandler = new SharedActivityLogHandler(mongoTemplate);
	}

	/**
	 * API to get the product detail for provided productId
	 * 
	 * @param req
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "" }, produces = { "application/json" })
	@ResponseBody
	public Object getProductDetail(HttpServletRequest req,
			@RequestParam(value = "productId", required = true) String productId) throws Exception {
		LoggerUtil.logEntry();
		Util.logStats(mongoTemplate, req, "get detail of product item", null, null, productId, null, null,
				Arrays.asList("productId = " + productId), "get detail page for productId " + productId, "PRODUCT");

		Product product = productRepository.findOne(productId);
		try {
			if (null == product) {
				throw new BYException(BYErrorCodes.PRODUCT_NOT_FOUND);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public Object submitProduct(@RequestBody Product product, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (product != null && (Util.isEmpty(product.getId()))) {

				Product productExtracted = new Product(product.getName(), product.getProductCategory(),
						product.getShortDescription(), product.getDescription(), product.getIsFeatured(),
						product.getRating(), product.getReviews(), product.getPrice(), product.getStatus(),
						product.getBuyLink(), product.getBuyFrom(), product.getImages());

				product = productRepository.save(productExtracted);
				logHandler.addLog(product, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new product entity created with ID: " + product.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Product added.", currentUser.getId(), currentUser.getEmail(),
				product.getId(), null, null, null, "new product entity is added", "PRODUCT");
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.PUT }, consumes = { "application/json" })
	@ResponseBody
	public Object editProduct(@RequestBody Product product, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (product != null && (!Util.isEmpty(product.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId())) {

					Product oldProduct = mongoTemplate.findById(new ObjectId(product.getId()), Product.class);
					oldProduct.setName(product.getName());
					oldProduct.setProductCategory(product.getProductCategory());
					oldProduct.setShortDescription(product.getShortDescription());
					oldProduct.setDescription(product.getDescription());
					oldProduct.setIsFeatured(product.getIsFeatured());
					oldProduct.setRating(product.getRating());
					oldProduct.setReviews(product.getReviews());
					oldProduct.setPrice(product.getPrice());
					oldProduct.setStatus(product.getStatus());
					oldProduct.setBuyLink(product.getBuyLink());
					oldProduct.setBuyFrom(product.getBuyFrom());
					oldProduct.setImages(product.getImages());
					oldProduct.setLastModifiedAt(new Date());

					product = productRepository.save(oldProduct);
					logHandler.addLog(product, ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old product entity updated for ID: " + product.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request, "EDIT " + product.getName() + " product content.",
							currentUser.getId(), currentUser.getEmail(), product.getId(), null, null, null,
							"old product entity updated", "PRODUCT");

				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(product);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getPage(@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productCategory", required = false) String productCategory,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<Product> page = null;
		ProductPage productPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = productRepository.getPage(searchTxt, productCategory, pageable);
			productPage = ProductResponse.getPage(page, currentUser,productRatingRepo);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productPage);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/count" }, produces = { "application/json" })
	@ResponseBody
	public Object productCount(@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productCategory", required = false) String productCategory,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		Map<String, Long> obj = new HashMap<String, Long>();
		List<String> filterCriteria = new ArrayList<String>();
		try {
			Long allCount = null;
			allCount = productRepository.getCount(searchTxt, productCategory);
			obj.put("all", new Long(allCount));
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "count query for product", null, null, null, null, null, filterCriteria,
				"querying count for product", "PRODUCT");
		return BYGenericResponseHandler.getResponse(obj);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/category/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getCategoryPage(@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<ProductCategory> page = null;
		ProductCategoryPage productCatPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = productCatRepo.getPage(searchTxt, pageable);
			productCatPage = ProductCategoryResponse.getPage(page, currentUser, productRepository, searchTxt);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productCatPage);
	}

	@RequestMapping(value = { "/category" }, method = { RequestMethod.POST }, consumes = {
			"application/json" }, produces = { "application/json" })
	@ResponseBody
	public Object submitProductCategory(@RequestBody ProductCategory productCategory, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productCategory != null && (Util.isEmpty(productCategory.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId())) {
					ProductCategory productCatExtracted = new ProductCategory(productCategory.getName());

					productCategory = productCatRepo.save(productCatExtracted);
					logHandlerCat.addLog(productCategory, ActivityLogConstants.CRUD_TYPE_CREATE, request);
					logger.info("new product entity created with ID: " + productCategory.getId());

				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Product Category added.", currentUser.getId(),
				currentUser.getEmail(), productCategory.getId(), null, null, null,
				"new product category entity is added", "PRODUCT_CATEGORY");
		return BYGenericResponseHandler.getResponse(productCategory);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/category" }, consumes = { "application/json" })
	@ResponseBody
	public Object editProductCategory(@RequestBody ProductCategory productCat, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productCat != null && (!Util.isEmpty(productCat.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId())) {

					ProductCategory oldProductCat = mongoTemplate.findById(new ObjectId(productCat.getId()),
							ProductCategory.class);
					oldProductCat.setName(productCat.getName());

					productCat = productCatRepo.save(oldProductCat);
					logHandlerCat.addLog(productCat, ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old product category entity updated for ID: " + productCat.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request, "EDIT " + productCat.getName() + " product category content.",
							currentUser.getId(), currentUser.getEmail(), productCat.getId(), null, null, null,
							"old product category entity updated", "PRODUCT");
				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(productCat);
	}

	@RequestMapping(method = { RequestMethod.GET }, value = { "/review/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getReviewPage(@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productId", required = false) String productId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		PageImpl<ProductReview> page = null;
		ProductReviewPage productRevPage = null;
		try {
			Direction sortDirection = Direction.DESC;
			if (dir != 0) {
				sortDirection = Direction.ASC;
			}

			Pageable pageable = new PageRequest(pageIndex, pageSize, sortDirection, sort);
			page = productRevRepo.getPage(searchTxt, productId, pageable);
			productRevPage = ProductReviewResponse.getPage(page, currentUser,mongoTemplate);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productRevPage);
	}

	@RequestMapping(method = { RequestMethod.POST }, value = { "/review" }, consumes = { "application/json" })
	@ResponseBody
	public Object submitProductReview(@RequestBody ProductReview productReview, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productReview != null && (Util.isEmpty(productReview.getId()))) {
				ProductReview productRevExtracted = new ProductReview(productReview.getProductId(),
						currentUser.getId(), productReview.getReview(), productReview.getLikeCount(),
						productReview.getUnLikeCount(), productReview.getTitle(), productReview.getParentReviewId());

				productReview = productRevRepo.save(productRevExtracted);

				Product product = null;
				product = productRepository.findOne(productRevExtracted.getProductId());
				product.setReviews(product.getReviews()+1);
				productRepository.save(product);
				logHandlerRev.addLog(productReview, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new product review entity created with ID: " + productReview.getId());
			} else {
				// Edit review
				// Query query = new Query();
				// query.addCriteria(Criteria.where("id").is(serviceReview.getId()));
				// mongoTemplate.findOne(query, ServiceReview.class);
				ProductReview productReviewExtracted = null;
				productReviewExtracted = productRevRepo.findOne(productReview.getId());
				if (productReviewExtracted != null) {

					productReviewExtracted.setTitle(productReview.getTitle());
					productReviewExtracted.setReview(productReview.getReview());
					productReview = productRevRepo.save(productReviewExtracted);
					logger.info("products review entity updated with ID: " + productReview.getId());

				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Product Review added.", currentUser.getId(), currentUser.getEmail(),
				productReview.getId(), null, null, null, "new product review entity is added", "PRODUCT_REVIEW");
		return BYGenericResponseHandler.getResponse(productReview);
	}

	// @RequestMapping(method = { RequestMethod.PUT }, value = { "/review" },
	// consumes = { "application/json" })
	// @ResponseBody
	// public Object editProductReview(@RequestBody ProductReview productRev,
	// HttpServletRequest request) throws Exception {
	// LoggerUtil.logEntry();
	// User currentUser = Util.getSessionUser(request);
	// if (null != currentUser && SessionController.checkCurrentSessionFor(request,
	// "PRODUCT")) {
	// if (productRev != null && (!Util.isEmpty(productRev.getId()))) {
	// if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
	// || BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

	// ProductReview oldProductRev = mongoTemplate.findById(new
	// ObjectId(productRev.getId()), ProductReview.class);
	// oldProductRev.setProductId(productRev.getProductId());
	// oldProductRev.setRating(productRev.getRating());
	// oldProductRev.setReview(productRev.getReview());
	// oldProductRev.setLikeCount(productRev.getLikeCount());
	// oldProductRev.setUnLikeCount(productRev.getUnLikeCount());
	// oldProductRev.setStatus(productRev.getStatus());
	// oldProductRev.setUserName(productRev.getUserName());
	// oldProductRev.setParentReviewId(productRev.getParentReviewId());

	// productRev = productRevRepo.save(oldProductRev);
	// logHandlerRev.addLog(productRev,
	// ActivityLogConstants.CRUD_TYPE_UPDATE, request);
	// logger.info("old product review entity updated for ID: "
	// + productRev.getId() + " by User "
	// + currentUser.getId());

	// Util.logStats(mongoTemplate, request,
	// "EDIT " + productRev.getId()
	// + " product review content.", currentUser.getId(),
	// currentUser.getEmail(), productRev.getId(), null,
	// null, null, "old product review entity updated",
	// "PRODUCT");
	// } else {
	// throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
	// }
	// } else {
	// throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
	// }
	// } else {
	// throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
	// }
	// return BYGenericResponseHandler.getResponse(productRev);
	// }

	/**
	 * Add new product rating
	 */
	@RequestMapping(method = { RequestMethod.POST }, value = { "/addRating" }, consumes = { "application/json" })
	@ResponseBody
	public Object addProductRating(@RequestBody ProductRating productRating, HttpServletRequest request)
			throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser) {
			if (productRating != null && (Util.isEmpty(productRating.getId()))) {

				ProductRating productRateExtracted = new ProductRating(productRating.getProductId(),
						currentUser.getId(), productRating.getRating());

				Query query = new Query();
				query.addCriteria(Criteria.where("userId").is(currentUser.getId()));
				query.addCriteria(Criteria.where("productId").is(productRateExtracted.getProductId()));
				ProductRating existingRating = null;
				existingRating = mongoTemplate.findOne(query, ProductRating.class);

				// Query productQuery = new Query();
				// productQuery.addCriteria(Criteria.where("id").is(productRateExtracted.getProductId()));
				Product product = null;
				product = productRepository.findOne(productRateExtracted.getProductId());

				if (existingRating == null) {

					if (product != null) {

						List<ProductRating> allProductRatings = new ArrayList<ProductRating>();
						allProductRatings = productRatingRepo.findByProductId(productRateExtracted.getProductId());
						allProductRatings.add(productRateExtracted);

						// userProfile.getRatedBy().add(currentUser.getId());
						float totRating = 0;

						for (ProductRating rating : allProductRatings) {
							totRating += rating.getRating();
						}

						totRating = totRating / allProductRatings.size();

						product.setRating(Math.round(totRating));
						mongoTemplate.save(product);
					}

					productRating = productRatingRepo.save(productRateExtracted);
				} else {
					// Update User Rating
					existingRating.setRating(productRateExtracted.getRating());
					existingRating.setLastModifiedAt(productRateExtracted.getLastModifiedAt());
					productRating = productRatingRepo.save(existingRating);

					if (product != null) {
						// Recount average rating for DB product
						List<ProductRating> allProductRatings = new ArrayList<ProductRating>();
						allProductRatings = productRatingRepo.findByProductId(productRateExtracted.getProductId());

						float totRating = 0;

						for (ProductRating rating : allProductRatings) {
							totRating += rating.getRating();
						}

						totRating = totRating / allProductRatings.size();

						product.setRating(Math.round(totRating));
						mongoTemplate.save(product);
					}
					// throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}

				logger.info("new product rating entity created with ID: " + productRating.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW product rating added.", currentUser.getId(), currentUser.getEmail(),
				productRating.getId(), null, null, null, "new product ratingi entity is added", "PRODUCT_RATING");
		return BYGenericResponseHandler.getResponse(productRating);
	}

	/**
	 * Get ratings for product
	 */
	@RequestMapping(method = { RequestMethod.GET }, value = { "/ratings" }, produces = { "application/json" })
	@ResponseBody
	public Object getProductRatings(@RequestParam(value = "productId", required = true) String productId,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();

		List<ProductRating> productRatings = null;
		try {
			productRatings = productRatingRepo.findByProductId(productId);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productRatings);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/likeReview" }, consumes = { "application/json" })
	@ResponseBody
	public Object LikeUnlikeReview(@RequestParam(value = "reviewId", required = true) String reviewId,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		ProductReview reviewUpdated = null;
		if (null != currentUser) {

			// Query query = new Query();
			// query.addCriteria(Criteria.where("id").is(reviewId));
			ProductReview productRevExtracted = null;
			productRevExtracted = productRevRepo.findOne(reviewId);
			List<String> users = new ArrayList<String>();
			if (productRevExtracted != null) {

				// if (isLike) {
				if (productRevExtracted.getLikeCount() != null
						&& productRevExtracted.getLikeCount().contains(currentUser.getId())) {
					users = productRevExtracted.getLikeCount();
					users.remove(currentUser.getId());
					productRevExtracted.setLikeCount(users);
					// throw new BYException(BYErrorCodes.USER_ALREADY_EXIST);
				} else {
					if (productRevExtracted.getLikeCount() != null) {
						users = productRevExtracted.getLikeCount();
					}
					users.add(currentUser.getId());
					productRevExtracted.setLikeCount(users);
				}
				// }
				// else {
				// if (serviceRevExtracted.getUnLikeCount() != null
				// && serviceRevExtracted.getUnLikeCount().contains(currentUser.getId())) {
				// throw new BYException(BYErrorCodes.USER_ALREADY_EXIST);
				// } else {
				// if (serviceRevExtracted.getUnLikeCount() != null) {
				// users = serviceRevExtracted.getUnLikeCount();
				// }
				// users.add(currentUser.getId());
				// serviceRevExtracted.setUnLikeCount(users);
				// }
				// }

				reviewUpdated = productRevRepo.save(productRevExtracted);
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}

		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}

		Util.logStats(mongoTemplate, request, "Product Review Like/Unliked.", currentUser.getId(),
				currentUser.getEmail(), reviewUpdated.getId(), null, null, null, "new Product Review Like/Unliked.",
				"PRODUCT_REVIEW");
		return BYGenericResponseHandler.getResponse(reviewUpdated);
	}

}
