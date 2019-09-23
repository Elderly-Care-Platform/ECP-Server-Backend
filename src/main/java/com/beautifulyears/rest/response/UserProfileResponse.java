/**
 * 
 */
package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.BasicProfileInfo;
import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.domain.IndividualProfileInfo;
import com.beautifulyears.domain.ServiceProviderInfo;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.domain.menu.Tag;

/**
 * @author Nitin
 *
 */
public class UserProfileResponse implements IResponse {

	private List<UserProfileEntity> userProfileArray = new ArrayList<UserProfileEntity>();

	@Override
	public List<UserProfileEntity> getResponse() {
		return userProfileArray;
	}

	public static class UserProfileEntity {
		private String id;
		private String userId;
		private List<Integer> userTypes = new ArrayList<Integer>();
		private BasicProfileInfo basicProfileInfo = new BasicProfileInfo();
		private IndividualProfileInfo individualInfo = new IndividualProfileInfo();
		private ServiceProviderInfo serviceProviderInfo = new ServiceProviderInfo();
		private Float ratingPercentage = 0f;
		private int ratingCount;
		private int reviewCount;
		private boolean isReviewedByUser = false;
		private boolean isRatedByUser = false;
		private Date createdAt = new Date();
		private List<Tag> systemTags = new ArrayList<Tag>();
		private List<String> userTags = new ArrayList<String>();
		private Date lastModifiedAt = new Date();
		private boolean isFeatured;
		private boolean verified;
		private int age;
		private String workTitle;
		private List<AskCategory> experties;


		private List<UserProfileResponse.UserProfileEntity> serviceBranches = new ArrayList<UserProfileResponse.UserProfileEntity>();
		private List<HousingFacility> facilities = new ArrayList<HousingFacility>();

		public UserProfileEntity(UserProfile profile, User user) {
			this.setId(profile.getId());
			this.setUserId(profile.getUserId());
			this.setUserTypes(profile.getUserTypes());
			this.setBasicProfileInfo(profile.getBasicProfileInfo());
			this.setIndividualInfo(profile.getIndividualInfo());
			this.setServiceProviderInfo(profile.getServiceProviderInfo());
			this.setCreatedAt(profile.getCreatedAt());
			this.setLastModifiedAt(profile.getLastModifiedAt());
			this.setRatingPercentage(profile.getAggrRatingPercentage());
			this.setSystemTags(profile.getSystemTags());
			this.setAge(profile.getAge());
			this.setWorkTitle(profile.getWorkTitle());
			this.setExperties(profile.getExperties());
			if (null != user && profile.getRatedBy().contains(user.getId())) {
				this.setRatedByUser(true);
			}
			if (null != user && profile.getReviewedBy().contains(user.getId())) {
				this.setReviewedByUser(true);
			}
			ratingCount = profile.getRatedBy().size();
			reviewCount = profile.getReviewedBy().size();
			this.isFeatured = profile.isFeatured();
			this.verified = profile.isVerified();
			this.facilities = profile.getFacilities();
			if (null != profile && null != user && null != profile.getUserId()
					&& profile.getUserId().equals(user.getId())) {
				this.setUserTags(user.getUserTags());
			} else {
				this.setUserTags(profile.getUserTags());
			}

			for (UserProfile profileBranches : profile.getServiceBranches()) {
				this.serviceBranches.add(UserProfileResponse
						.getUserProfileEntity(profileBranches, user));
			}

		}

		public List<String> getUserTags() {
			return userTags;
		}

		public void setUserTags(List<String> userTags) {
			this.userTags = userTags;
		}

		public List<UserProfileResponse.UserProfileEntity> getServiceBranches() {
			return serviceBranches;
		}

		public void setServiceBranches(
				List<UserProfileResponse.UserProfileEntity> serviceBranches) {
			this.serviceBranches = serviceBranches;
		}

		public List<HousingFacility> getFacilities() {
			return facilities;
		}

		public void setFacilities(List<HousingFacility> facilities) {
			this.facilities = facilities;
		}

		public boolean isFeatured() {
			return isFeatured;
		}

		public void setFeatured(boolean isFeatured) {
			this.isFeatured = isFeatured;
		}

		public boolean isVerified() {
			return verified;
		}

		public void setVerified(boolean verified) {
			this.verified = verified;
		}

		public List<Tag> getSystemTags() {
			return systemTags;
		}

		public void setSystemTags(List<Tag> systemTags) {
			this.systemTags = systemTags;
		}

		public int getRatingCount() {
			return ratingCount;
		}

		public void setRatingCount(int ratingCount) {
			this.ratingCount = ratingCount;
		}

		public int getReviewCount() {
			return reviewCount;
		}

		public void setReviewCount(int reviewCount) {
			this.reviewCount = reviewCount;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}

		public List<Integer> getUserTypes() {
			return userTypes;
		}

		public void setUserTypes(List<Integer> userTypes) {
			this.userTypes = userTypes;
		}

		public BasicProfileInfo getBasicProfileInfo() {
			return basicProfileInfo;
		}

		public void setBasicProfileInfo(BasicProfileInfo basicProfileInfo) {
			this.basicProfileInfo = basicProfileInfo;
		}

		public IndividualProfileInfo getIndividualInfo() {
			return individualInfo;
		}

		public void setIndividualInfo(IndividualProfileInfo individualInfo) {
			this.individualInfo = individualInfo;
		}

		public ServiceProviderInfo getServiceProviderInfo() {
			return serviceProviderInfo;
		}

		public void setServiceProviderInfo(
				ServiceProviderInfo serviceProviderInfo) {
			this.serviceProviderInfo = serviceProviderInfo;
		}

		public Float getRatingPercentage() {
			return ratingPercentage;
		}

		public void setRatingPercentage(Float ratingPercentage) {
			this.ratingPercentage = ratingPercentage;
		}

		public boolean isReviewedByUser() {
			return isReviewedByUser;
		}

		public void setReviewedByUser(boolean isReviewedByUser) {
			this.isReviewedByUser = isReviewedByUser;
		}

		public boolean isRatedByUser() {
			return isRatedByUser;
		}

		public void setRatedByUser(boolean isRatedByUser) {
			this.isRatedByUser = isRatedByUser;
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

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}

		public String getWorkTitle() {
			return workTitle;
		}

		public void setWorkTitle(String workTitle) {
			this.workTitle = workTitle;
		}

		public List<AskCategory> getExperties() {
			return experties;
		}

		public void setExperties(List<AskCategory> experties) {
			this.experties = experties;
		}

	}

	public static class UserProfilePage {
		private List<UserProfileEntity> content = new ArrayList<UserProfileEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public UserProfilePage() {
			super();
		}

		public UserProfilePage(PageImpl<UserProfile> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (UserProfile profile : page.getContent()) {
				this.content.add(new UserProfileResponse.UserProfileEntity(
						profile, user));
			}
			this.size = page.getSize();
			this.total = page.getTotal();
		}

		public List<UserProfileEntity> getContent() {
			return content;
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

		public void setContent(List<UserProfileEntity> content) {
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

	public void add(UserProfile userProfile, User user) {
		this.userProfileArray.add(new UserProfileEntity(userProfile, user));
	}

	public void add(List<UserProfile> userProfiles, User user) {
		for (UserProfile userProfile : userProfiles) {
			this.userProfileArray.add(new UserProfileEntity(userProfile, user));
		}
	}

	public static UserProfilePage getPage(PageImpl<UserProfile> page, User user) {
		UserProfilePage res = new UserProfilePage(page, user);
		return res;
	}

	public static UserProfileEntity getUserProfileEntity(
			UserProfile userProfile, User user) {
		UserProfileEntity res = null;
		if (null != userProfile) {
			res = new UserProfileEntity(userProfile, user);
		}
		return res;
	}

}
