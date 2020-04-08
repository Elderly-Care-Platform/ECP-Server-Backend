package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.UserTypes;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.User;
import com.beautifulyears.repository.AskQuestionRepository;
import com.beautifulyears.repository.UserProfileRepository;

import org.bson.types.ObjectId;

public class AskCategoryResponse implements IResponse {

	private List<AskCategoryEntity> askCategoryArray = new ArrayList<AskCategoryEntity>();

	private static UserProfileRepository userProfileRepo;
	private static String searchTxt;

	@Override
	public List<AskCategoryEntity> getResponse() {
		return this.askCategoryArray;
	}

	public static class AskCategoryPage {
		private List<AskCategoryEntity> content = new ArrayList<AskCategoryEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public AskCategoryPage() {
			super();
		}

		public AskCategoryPage(PageImpl<AskCategory> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (AskCategory askCategory : page.getContent()) {
				this.content.add(new AskCategoryEntity(askCategory, user));
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

		public List<AskCategoryEntity> getContent() {
			return content;
		}

		public void setContent(List<AskCategoryEntity> content) {
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

	public static class AskCategoryEntity {
		private String 	id;
		private String 	name;
		private long questionCount;
		private boolean isEditableByUser = false;
		private boolean show = false;

		public AskCategoryEntity(AskCategory askCategory, User user) {
			this.setId(askCategory.getId());
			this.setName(askCategory.getName());
			Integer[] userTypes = { UserTypes.ASK_EXPERT};
			List<ObjectId> experties = new ArrayList<ObjectId>();
			experties.add( new ObjectId(askCategory.getId()));
			long countWithoutSearch = AskCategoryResponse.userProfileRepo.getServiceProvidersByFilterCriteriaCount(null, userTypes, null, null, null, experties,null,null);
			long countWithSearch = AskCategoryResponse.userProfileRepo.getServiceProvidersByFilterCriteriaCount(AskCategoryResponse.searchTxt, userTypes, null, null, null, experties,null,null);
			this.setQuestionCount(countWithSearch);
			if(countWithoutSearch > 0){
				this.setShow(true);
			}
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

		public long getQuestionCount() {
			return questionCount;
		}

		public void setQuestionCount(long questionCount) {
			this.questionCount = questionCount;
		}

		public boolean getShow() {
			return show;
		}

		public void setShow(boolean show) {
			this.show = show;
		}
		
	}

	public void add(List<AskCategory> askCategoryArray) {
		for (AskCategory askCategory : askCategoryArray) {
			this.askCategoryArray.add(new AskCategoryEntity(askCategory, null));
		}
	}

	public void add(AskCategory askCategory) {
		this.askCategoryArray.add(new AskCategoryEntity(askCategory, null));
	}

	public void add(List<AskCategory> askCategoryArray, User user) {
		for (AskCategory askCategory : askCategoryArray) {
			this.askCategoryArray.add(new AskCategoryEntity(askCategory, user));
		}
	}

	public void add(AskCategory askCategory, User user) {
		this.askCategoryArray.add(new AskCategoryEntity(askCategory, user));
	}

	public static AskCategoryPage getPage(PageImpl<AskCategory> page, User user, UserProfileRepository userProfileRepo, String searchTxt ) {
		AskCategoryResponse.userProfileRepo = userProfileRepo;
		AskCategoryResponse.searchTxt = searchTxt;
		AskCategoryPage res = new AskCategoryPage(page, user);
		return res;
	}

	public AskCategoryEntity getAskCategoryEntity(AskCategory askCategory, User user) {
		return new AskCategoryEntity(askCategory, user);
	}

}
