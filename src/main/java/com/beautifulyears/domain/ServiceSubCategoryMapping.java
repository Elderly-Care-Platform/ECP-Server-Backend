package com.beautifulyears.domain;

import java.util.List;

public class ServiceSubCategoryMapping {

	private List<Source> source;
	private String category_name;
	private long totalServices=0;
	 
	public String getName() {
		return category_name;
	}

	public void setName(String name) {
		this.category_name = name;
	}

	public List<Source> getSource() {
		return source;
	}

	public void setSource(List<Source> source) {
		this.source = source;
	}

	public ServiceSubCategoryMapping() {
	}

	public ServiceSubCategoryMapping(List<Source> source, String category_name) {
		this.source = source;
		this.category_name = category_name;
	}

	public class Source {
		private String name;
		private String catid;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCatid() {
			return catid;
		}

		public void setCatid(String catid) {
			this.catid = catid;
		}

	}

	public long getTotalServices() {
		return totalServices;
	}

	public void setTotalServices(long totalServices) {
		this.totalServices = totalServices;
	}

}
