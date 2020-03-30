package com.beautifulyears.domain;

public class ServiceSubCategory {

	private String 	national_catid;
	private String 	category_name;

	public String getId() {
		return national_catid;
	}

	public void setId(String id) {
		this.national_catid = id;
	}

	public String getName() {
		return category_name;
	}

	public void setName(String name) {
		this.category_name = name;
	}

	public ServiceSubCategory(String name,String id) {
        this.national_catid = id;
        this.category_name = name;
	}
	public ServiceSubCategory() {
	}
}
