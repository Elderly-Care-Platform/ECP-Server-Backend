package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.ProductCategory;
import com.beautifulyears.domain.User;

public class ProductCategoryResponse implements IResponse {

	private List<ProductCategoryEntity> productCategoryArray = new ArrayList<ProductCategoryEntity>();

	@Override
	public List<ProductCategoryEntity> getResponse() {
		return this.productCategoryArray;
	}

	public static class ProductCategoryPage {
		private List<ProductCategoryEntity> content = new ArrayList<ProductCategoryEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public ProductCategoryPage() {
			super();
		}

		public ProductCategoryPage(PageImpl<ProductCategory> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (ProductCategory productCategory : page.getContent()) {
				this.content.add(new ProductCategoryEntity(productCategory, user));
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

		public List<ProductCategoryEntity> getContent() {
			return content;
		}

		public void setContent(List<ProductCategoryEntity> content) {
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

	public static class ProductCategoryEntity {
		private String 	id;
		private String 	name;
		private boolean isEditableByUser = false;

		public ProductCategoryEntity(ProductCategory productCategory, User user) {
			this.setId(productCategory.getId());
			this.setName(productCategory.getName());
			
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
	}

	public void add(List<ProductCategory> productCategoryArray) {
		for (ProductCategory productCategory : productCategoryArray) {
			this.productCategoryArray.add(new ProductCategoryEntity(productCategory, null));
		}
	}

	public void add(ProductCategory productCategory) {
		this.productCategoryArray.add(new ProductCategoryEntity(productCategory, null));
	}

	public void add(List<ProductCategory> productCategoryArray, User user) {
		for (ProductCategory productCategory : productCategoryArray) {
			this.productCategoryArray.add(new ProductCategoryEntity(productCategory, user));
		}
	}

	public void add(ProductCategory productCategory, User user) {
		this.productCategoryArray.add(new ProductCategoryEntity(productCategory, user));
	}

	public static ProductCategoryPage getPage(PageImpl<ProductCategory> page, User user) {
		ProductCategoryPage res = new ProductCategoryPage(page, user);
		return res;
	}

	public ProductCategoryEntity getProductCategoryEntity(ProductCategory productCategory, User user) {
		return new ProductCategoryEntity(productCategory, user);
	}

}
