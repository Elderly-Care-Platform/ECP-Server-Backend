package com.beautifulyears.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "service_categories_mapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceCategoriesMapping {

	@Id
	private String id;
	private String name;
	private List<ServiceSubCategoryMapping> subCategories = new ArrayList<ServiceSubCategoryMapping>();
	private long totalServices = 0;

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

	public List<ServiceSubCategoryMapping> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<ServiceSubCategoryMapping> subCategories) {
		this.subCategories = subCategories;
	}

	public ServiceCategoriesMapping(String name) {
		this.name = name;
	}

	public ServiceCategoriesMapping() {
	}

	public long getTotalServices() {
		return totalServices;
	}

	public void setTotalServices(long totalServices) {
		this.totalServices = totalServices;
	}

}
