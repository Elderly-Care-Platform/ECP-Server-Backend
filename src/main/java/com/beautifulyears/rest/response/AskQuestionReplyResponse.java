package com.beautifulyears.rest.response;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.AskQuestionReply;
import com.beautifulyears.domain.User;

public class AskQuestionReplyResponse implements IResponse {

	private List<AskQuestionReplyEntity> askQuestionReplyArray = new ArrayList<AskQuestionReplyEntity>();

	@Override
	public List<AskQuestionReplyEntity> getResponse() {
		return this.askQuestionReplyArray;
	}

	public static class AskQuestionReplyPage {
		private List<AskQuestionReplyEntity> content = new ArrayList<AskQuestionReplyEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public AskQuestionReplyPage() {
			super();
		}

		public AskQuestionReplyPage(PageImpl<AskQuestionReply> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (AskQuestionReply askQuestionReply : page.getContent()) {
				this.content.add(new AskQuestionReplyEntity(askQuestionReply, user));
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

		public List<AskQuestionReplyEntity> getContent() {
			return content;
		}

		public void setContent(List<AskQuestionReplyEntity> content) {
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

	public static class AskQuestionReplyEntity {
		private String 	id;
		private String 	askQuestionId;
		private String 	reply;
		private User 	user;
		private Date createdAt;
		private Date lastModifiedAt;

		public AskQuestionReplyEntity(AskQuestionReply askQuestionReply, User user) {
			this.setId(askQuestionReply.getId());
			this.setAskQuestionId(askQuestionReply.getAskQuestionId());
			this.setReply(askQuestionReply.getReply());
			this.setCreatedAt(askQuestionReply.getCreatedAt());
			this.setLastModifiedAt(askQuestionReply.getLastModifiedAt());
			this.setUser(askQuestionReply.getUser());
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAskQuestionId() {
			return askQuestionId;
		}

		public void setAskQuestionId(String askQuestionId) {
			this.askQuestionId = askQuestionId;
		}

		public String getReply() {
			return reply;
		}

		public void setReply(String reply) {
			this.reply = reply;
		}

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
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
		

		
	}

	public void add(List<AskQuestionReply> askQuestionReplyArray) {
		for (AskQuestionReply askQuestionReply : askQuestionReplyArray) {
			this.askQuestionReplyArray.add(new AskQuestionReplyEntity(askQuestionReply, null));
		}
	}

	public void add(AskQuestionReply askQuestionReply) {
		this.askQuestionReplyArray.add(new AskQuestionReplyEntity(askQuestionReply, null));
	}

	public void add(List<AskQuestionReply> askQuestionReplyArray, User user) {
		for (AskQuestionReply askQuestionReply : askQuestionReplyArray) {
			this.askQuestionReplyArray.add(new AskQuestionReplyEntity(askQuestionReply, user));
		}
	}

	public void add(AskQuestionReply askQuestionReply, User user) {
		this.askQuestionReplyArray.add(new AskQuestionReplyEntity(askQuestionReply, user));
	}

	public static AskQuestionReplyPage getPage(PageImpl<AskQuestionReply> page, User user) {
		AskQuestionReplyPage res = new AskQuestionReplyPage(page, user);
		return res;
	}

	public AskQuestionReplyEntity getAskQuestionReplyEntity(AskQuestionReply askQuestionReply, User user) {
		return new AskQuestionReplyEntity(askQuestionReply, user);
	}

}
