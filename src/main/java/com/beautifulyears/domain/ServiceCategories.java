package com.beautifulyears.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "service_categories_mapping")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceCategories {

	@Id
	private String 	id;
	private String 	name;
    private List<ServiceSubCategory> subCategories = new ArrayList<ServiceSubCategory>();

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
    
    public List<ServiceSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<ServiceSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

	public ServiceCategories(String name) {
		this.name = name;
	}
	public ServiceCategories() {
	}

}
