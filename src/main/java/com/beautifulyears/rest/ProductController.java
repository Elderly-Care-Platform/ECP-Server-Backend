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
import com.beautifulyears.domain.ProductReview;
import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.repository.ProductRepository;
import com.beautifulyears.repository.ProductCategoryRepository;
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
	private static final Logger logger = Logger
			.getLogger(ProductController.class);
	private ProductRepository productRepository;
	private ProductCategoryRepository productCatRepo;
	private ProductReviewRepository productRevRepo;
	private MongoTemplate mongoTemplate;
	ActivityLogHandler<Product> logHandler;
	ActivityLogHandler<ProductCategory> logHandlerCat;
	ActivityLogHandler<ProductReview> logHandlerRev;
	ActivityLogHandler<Object> shareLogHandler;

	@Autowired
	public ProductController(ProductRepository productRepository, UserRepository userRepository, 
			ProductCategoryRepository productCatRepo,
			ProductReviewRepository productRevRepo,
			MongoTemplate mongoTemplate) {
		this.productRepository = productRepository;
		this.productCatRepo = productCatRepo;
		this.productRevRepo = productRevRepo;
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
			@RequestParam(value = "productId", required = true) String productId)
			throws Exception {
		LoggerUtil.logEntry();
		Util.logStats(mongoTemplate, req, "get detail of product item", null,
				null, productId, null, null,
				Arrays.asList("productId = " + productId),
				"get detail page for productId " + productId, "PRODUCT");

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
				
				Product productExtracted = new Product(
					product.getName(), 
					product.getProductCategory(),
					product.getShortDescription(),
					product.getDescription(),
					product.getIsFeatured(),
					product.getRating(),
					product.getReviews(),
					product.getPrice(), 
					product.getStatus(),
					product.getBuyLink(),
					product.getBuyFrom(),
					product.getImages()
					);

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
				product.getId(), null, null, null,
				"new product entity is added", "PRODUCT");
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
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

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
					logHandler.addLog(product,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old product entity updated for ID: "
							+ product.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request,
							"EDIT " + product.getName()
									+ " product content.", currentUser.getId(),
							currentUser.getEmail(), product.getId(), null,
							null, null, "old product entity updated",
							"PRODUCT");

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
	public Object getPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productCategory", required = false) String productCategory,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
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
			productPage = ProductResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productPage);
	}	

	@RequestMapping(method = { RequestMethod.GET }, value = { "/count" }, produces = { "application/json" })
	@ResponseBody
	public Object productCount(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productCategory", required = false) String productCategory,
			HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		Map<String, Long> obj = new HashMap<String, Long>();
		List<String> filterCriteria = new ArrayList<String>();
		try {
			Long allCount = null;
			allCount = productRepository.getCount(searchTxt,productCategory);
			obj.put("all", new Long(allCount));
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, request, "count query for product", null,
				null, null, null, null, filterCriteria,
				"querying count for product", "PRODUCT");
		return BYGenericResponseHandler.getResponse(obj);
	}


	@RequestMapping(method = { RequestMethod.GET }, value = { "/category/page" }, produces = { "application/json" })
	@ResponseBody
	public Object getCategoryPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
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
			productCatPage = ProductCategoryResponse.getPage(page, currentUser,productRepository);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productCatPage);
	}

	@RequestMapping(value = { "/category" }, method = { RequestMethod.POST }, consumes = { "application/json" }, produces = { "application/json" })
	@ResponseBody
	public Object submitProductCategory(@RequestBody ProductCategory productCategory, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productCategory != null && (Util.isEmpty(productCategory.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {
					ProductCategory productCatExtracted = new ProductCategory(
					productCategory.getName()
					);

					productCategory = productCatRepo.save(productCatExtracted);
					logHandlerCat.addLog(productCategory, ActivityLogConstants.CRUD_TYPE_CREATE, request);
					logger.info("new product entity created with ID: " + productCategory.getId());

				}
				else{
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}
				
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Product Category added.", currentUser.getId(), currentUser.getEmail(),
			productCategory.getId(), null, null, null,
				"new product category entity is added", "PRODUCT_CATEGORY");
		return BYGenericResponseHandler.getResponse(productCategory);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/category" }, consumes = { "application/json" })
	@ResponseBody
	public Object editProductCategory(@RequestBody ProductCategory productCat, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productCat != null && (!Util.isEmpty(productCat.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

					ProductCategory oldProductCat = mongoTemplate.findById(new ObjectId(productCat.getId()), ProductCategory.class);
					oldProductCat.setName(productCat.getName());
					
					productCat = productCatRepo.save(oldProductCat);
					logHandlerCat.addLog(productCat,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old product category entity updated for ID: "
							+ productCat.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request,
							"EDIT " + productCat.getName()
									+ " product category content.", currentUser.getId(),
							currentUser.getEmail(), productCat.getId(), null,
							null, null, "old product category entity updated",
							"PRODUCT");
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
	public Object getReviewPage(
			@RequestParam(value = "searchTxt", required = false) String searchTxt,
			@RequestParam(value = "productId", required = false) String productId,
			@RequestParam(value = "sort", required = false, defaultValue = "createdAt") String sort,
			@RequestParam(value = "dir", required = false, defaultValue = "0") int dir,
			@RequestParam(value = "p", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "s", required = false, defaultValue = "10") int pageSize,
			HttpServletRequest request) throws Exception {
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
			productRevPage = ProductReviewResponse.getPage(page, currentUser);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(productRevPage);
	}

	@RequestMapping(method = { RequestMethod.POST }, value = { "/review" }, consumes = { "application/json" })
	@ResponseBody
	public Object submitProductReview(@RequestBody ProductReview productReview, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productReview != null && (Util.isEmpty(productReview.getId()))) {
				ProductReview productRevExtracted = new ProductReview(
					productReview.getProductId(),
					productReview.getRating(),
					productReview.getReview(),
					productReview.getLikeCount(),
					productReview.getUnLikeCount(),
					productReview.getStatus(),
					productReview.getUserName(),
					productReview.getParentReviewId()
				);

				productReview = productRevRepo.save(productRevExtracted);
				logHandlerRev.addLog(productReview, ActivityLogConstants.CRUD_TYPE_CREATE, request);
				logger.info("new product review entity created with ID: " + productReview.getId());
			} else {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		Util.logStats(mongoTemplate, request, "NEW Product Review added.", currentUser.getId(), currentUser.getEmail(),
			productReview.getId(), null, null, null,
				"new product review entity is added", "PRODUCT_REVIEW");
		return BYGenericResponseHandler.getResponse(productReview);
	}

	@RequestMapping(method = { RequestMethod.PUT }, value = { "/review" }, consumes = { "application/json" })
	@ResponseBody
	public Object editProductReview(@RequestBody ProductReview productRev, HttpServletRequest request) throws Exception {
		LoggerUtil.logEntry();
		User currentUser = Util.getSessionUser(request);
		if (null != currentUser && SessionController.checkCurrentSessionFor(request, "PRODUCT")) {
			if (productRev != null && (!Util.isEmpty(productRev.getId()))) {
				if (BYConstants.USER_ROLE_EDITOR.equals(currentUser.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(currentUser.getUserRoleId()) ) {

					ProductReview oldProductRev = mongoTemplate.findById(new ObjectId(productRev.getId()), ProductReview.class);
					oldProductRev.setProductId(productRev.getProductId());
					oldProductRev.setRating(productRev.getRating());
					oldProductRev.setReview(productRev.getReview());
					oldProductRev.setLikeCount(productRev.getLikeCount());
					oldProductRev.setUnLikeCount(productRev.getUnLikeCount());
					oldProductRev.setStatus(productRev.getStatus());
					oldProductRev.setUserName(productRev.getUserName());
					oldProductRev.setParentReviewId(productRev.getParentReviewId());

					productRev = productRevRepo.save(oldProductRev);
					logHandlerRev.addLog(productRev,
							ActivityLogConstants.CRUD_TYPE_UPDATE, request);
					logger.info("old product review entity updated for ID: "
							+ productRev.getId() + " by User "
							+ currentUser.getId());

					Util.logStats(mongoTemplate, request,
							"EDIT " + productRev.getId()
									+ " product review content.", currentUser.getId(),
							currentUser.getEmail(), productRev.getId(), null,
							null, null, "old product review entity updated",
							"PRODUCT");
				} else {
					throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
				}
			} else {
				throw new BYException(BYErrorCodes.NO_CONTENT_FOUND);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}
		return BYGenericResponseHandler.getResponse(productRev);
	}
}
