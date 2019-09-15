package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.Product;
import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.domain.User;

public class ProductResponse implements IResponse {

	private List<ProductEntity> productArray = new ArrayList<ProductEntity>();

	@Override
	public List<ProductEntity> getResponse() {
		return this.productArray;
	}

	public static class ProductPage {
		private List<ProductEntity> content = new ArrayList<ProductEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public ProductPage() {
			super();
		}

		public ProductPage(PageImpl<Product> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (Product product : page.getContent()) {
				this.content.add(new ProductEntity(product, user));
			}
			this.size = page.getSize();
			this.total = page.getTotal();
		}

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public List<ProductEntity> getContent() {
			return content;
		}

		public void setContent(List<ProductEntity> content) {
			this.content = content;
		}

		public boolean isLastPage() {
			return lastPage;
		}

		public void setLastPage(boolean lastPage) {
			this.lastPage = lastPage;
		}

		public long getNumber() {
			return number;
		}

		public void setNumber(long number) {
			this.number = number;
		}

	}

	public static class ProductEntity {
		private String 	id;
		private String 	name;
		private ProductCategory productCategory;
		private String 	shortDescription;
		private String 	description;
		private Boolean isFeatured;
		private Integer rating;
		private Integer reviews;
		private Float 	price;
		private int 	status;
		private String 	buyLink;
		private String	buyFrom;
		private List<String>  images = new ArrayList<String>();;
		private Date createdAt = new Date();
		private Date lastModifiedAt = new Date();	
		private boolean isEditableByUser = false;

		public ProductEntity(Product product, User user) {
			this.setName(product.getName());
			this.setProductCategory(product.getProductCategory());
			this.setShortDescription(product.getShortDescription());
			this.setDescription(product.getDescription());
			this.setIsFeatured(product.getIsFeatured());
			this.setRating(product.getRating());
			this.setReviews(product.getReviews());
			this.setPrice(product.getPrice());
			this.setStatus(product.getStatus());
			this.setBuyLink(product.getBuyLink());
			this.setBuyFrom(product.getBuyFrom());
			this.setImages(product.getImages());
			this.setCreatedAt(product.getCreatedAt());
			this.setLastModifiedAt(product.getLastModifiedAt());
			
			if (null != user
					&& (BYConstants.USER_ROLE_EDITOR.equals(user.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(user.getUserRoleId())
				)) {
				this.setEditableByUser(true);
			}
		}

		public boolean isEditableByUser() {
			return isEditableByUser;
		}

		public void setEditableByUser(boolean isEditable) {
			this.isEditableByUser = isEditable;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProductCategory getProductCategory() {
			return productCategory;
		}

		public void setProductCategory(ProductCategory productCategory) {
			this.productCategory = productCategory;
		}

		public String getShortDescription() {
			return shortDescription;
		}

		public void setShortDescription(String shortDescription) {
			this.shortDescription = shortDescription;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Boolean getIsFeatured() {
			return isFeatured;
		}

		public void setIsFeatured(Boolean isFeatured) {
			this.isFeatured = isFeatured;
		}

		public Integer getRating() {
			return rating;
		}

		public void setRating(Integer rating) {
			this.rating = rating;
		}

		public Integer getReviews() {
			return reviews;
		}

		public void setReviews(Integer reviews) {
			this.reviews = reviews;
		}

		public Float getPrice() {
			return price;
		}

		public void setPrice(Float price) {
			this.price = price;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getBuyLink() {
			return buyLink;
		}

		public void setBuyLink(String buyLink) {
			this.buyLink = buyLink;
		}

		public String getBuyFrom() {
			return buyFrom;
		}

		public void setBuyFrom(String buyFrom) {
			this.buyFrom = buyFrom;
		}

		public List<String> getImages() {
			return images;
		}

		public void setImages(List<String> images) {
			this.images = images;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}

		public Date getLastModifiedAt() {
			return lastModifiedAt;
		}

		public void setLastModifiedAt(Date lastModifiedAt) {
			this.lastModifiedAt = lastModifiedAt;
		}

		
		
	}

	public void add(List<Product> productArray) {
		for (Product product : productArray) {
			this.productArray.add(new ProductEntity(product, null));
		}
	}

	public void add(Product product) {
		this.productArray.add(new ProductEntity(product, null));
	}

	public void add(List<Product> productArray, User user) {
		for (Product product : productArray) {
			this.productArray.add(new ProductEntity(product, user));
		}
	}

	public void add(Product product, User user) {
		this.productArray.add(new ProductEntity(product, user));
	}

	public static ProductPage getPage(PageImpl<Product> page, User user) {
		ProductPage res = new ProductPage(page, user);
		return res;
	}

	public ProductEntity getProductEntity(Product product, User user) {
		return new ProductEntity(product, user);
	}

}
