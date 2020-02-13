package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.AskCategory;
import com.beautifulyears.domain.AskQuestion;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.repository.AskQuestionReplyRepository;
import com.beautifulyears.repository.UserProfileRepository;

public class AskQuestionResponse implements IResponse {

	private List<AskQuestionEntity> askQuesArray = new ArrayList<AskQuestionEntity>();
	private static AskQuestionReplyRepository askQuesReplyRepo;
	private static UserProfileRepository userProfileRepo;

	@Override
	public List<AskQuestionEntity> getResponse() {
		return this.askQuesArray;
	}

	public static class AskQuestionPage {
		private List<AskQuestionEntity> content = new ArrayList<AskQuestionEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public AskQuestionPage() {
			super();
		}

		public AskQuestionPage(PageImpl<AskQuestion> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (AskQuestion askQues : page.getContent()) {
				this.content.add(new AskQuestionEntity(askQues, user));
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

		public List<AskQuestionEntity> getContent() {
			return content;
		}

		public void setContent(List<AskQuestionEntity> content) {
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

	public static class AskQuestionEntity {
		private String 	id;
		private String 	question;
		private String 	description;
		private AskCategory askCategory;
		private User	askedBy;
		private UserProfile	askedByProfile;
		private UserProfile	answeredBy;
		private Boolean	answered;
		private long	replyCount;
		private Date createdAt = new Date();
		private Date lastModifiedAt = new Date();
		private boolean isEditableByUser = false;

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

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public AskCategory getAskCategory() {
			return askCategory;
		}

		public void setAskCategory(AskCategory askCategory) {
			this.askCategory = askCategory;
		}

		public User getAskedBy() {
			return askedBy;
		}

		public void setAskedBy(User askedBy) {
			this.askedBy = askedBy;
		}
		
		public UserProfile getAskedByProfile() {
			return askedByProfile;
		}

		public void setAskedByProfile(UserProfile askedByProfile) {
			this.askedByProfile = askedByProfile;
		}

		public UserProfile getAnsweredBy() {
			return answeredBy;
		}

		public void setAnsweredBy(UserProfile userProfile) {
			this.answeredBy = userProfile;
		}

		public Boolean getAnswered() {
			return answered;
		}

		public void setAnswered(Boolean answered) {
			this.answered = answered;
		}

		public long getReplyCount() {
			return replyCount;
		}

		public void setReplyCount(long replyCount) {
			this.replyCount = replyCount;
		}

		public AskQuestionEntity(AskQuestion askQues, User user) {
			this.setId(askQues.getId());
			this.setAnswered(askQues.getAnswered());
			this.setAskCategory(askQues.getAskCategory());
			this.setAskedBy(askQues.getAskedBy());
			this.setAnsweredBy(askQues.getAnsweredBy());
			this.setQuestion(askQues.getQuestion());
			this.setDescription(askQues.getDescription());
			this.setCreatedAt(askQues.getCreatedAt());
			this.setLastModifiedAt(askQues.getLastModifiedAt());
			this.setLastModifiedAt(askQues.getLastModifiedAt());
			if(askQues.getAskedBy() != null){
				this.setAskedByProfile(AskQuestionResponse.userProfileRepo.findByUserId(askQues.getAskedBy().getId()));
			}
			
			this.setReplyCount(AskQuestionResponse.askQuesReplyRepo.getCount(null,askQues.getId() ));
			if (null != user
					&& (BYConstants.USER_ROLE_EDITOR.equals(user.getUserRoleId())
						|| BYConstants.USER_ROLE_SUPER_USER.equals(user.getUserRoleId())
				)) {
				this.setEditableByUser(true);
			}
		}
	}

	public void add(List<AskQuestion> askQuesArray) {
		for (AskQuestion askQues : askQuesArray) {
			this.askQuesArray.add(new AskQuestionEntity(askQues, null));
		}
	}

	public void add(AskQuestion askQues) {
		this.askQuesArray.add(new AskQuestionEntity(askQues, null));
	}

	public void add(List<AskQuestion> askQuesArray, User user) {
		for (AskQuestion askQues : askQuesArray) {
			this.askQuesArray.add(new AskQuestionEntity(askQues, user));
		}
	}

	public void add(AskQuestion askQues, User user) {
		this.askQuesArray.add(new AskQuestionEntity(askQues, user));
	}

	public static AskQuestionPage getPage(PageImpl<AskQuestion> page, User user,AskQuestionReplyRepository askQuesReplyRepo, UserProfileRepository userProfileRepo) {
		AskQuestionResponse.askQuesReplyRepo = askQuesReplyRepo;
		AskQuestionResponse.userProfileRepo = userProfileRepo;
		AskQuestionPage res = new AskQuestionPage(page, user);
		return res;
	}

	public AskQuestionEntity getAskQuestionEntity(AskQuestion askQues, User user) {
		return new AskQuestionEntity(askQues, user);
	}
}
