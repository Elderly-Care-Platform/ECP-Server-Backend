package com.beautifulyears.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "product")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

	@Id
	private String 	id;
	private String 	name;

	@DBRef
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
	private final Date createdAt = new Date();
	private Date lastModifiedAt = new Date();


	public Product() {
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

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public Product(String name, ProductCategory productCategory, String shortDescription, String description, Boolean isFeatured, Integer rating,
			Integer reviews, Float price, int status, String buyLink, String buyFrom, List<String> images) {
		this.name = name;
		this.productCategory = productCategory;
		this.shortDescription = shortDescription;
		this.description = description;
		this.isFeatured = isFeatured;
		this.rating = rating;
		this.reviews = reviews;
		this.price = price;
		this.status = status;
		this.buyLink = buyLink;
		this.buyFrom = buyFrom;
		this.images = images;
		this.lastModifiedAt = new Date();
	}
}
