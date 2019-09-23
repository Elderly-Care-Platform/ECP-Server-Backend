package com.beautifulyears.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "productcategory")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductCategory {

	@Id
	private String 	id;
	private String 	name;

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

	public ProductCategory(String name) {
		this.name = name;
	}
	public ProductCategory() {
	}
}
