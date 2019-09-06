package com.beautifulyears.rest.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.Event;
import com.beautifulyears.domain.User;

public class EventResponse implements IResponse {

	private List<EventEntity> eventArray = new ArrayList<EventEntity>();

	@Override
	public List<EventEntity> getResponse() {
		// TODO Auto-generated method stub
		return this.eventArray;
	}

	public static class EventPage {
		private List<EventEntity> content = new ArrayList<EventEntity>();
		private boolean lastPage;
		private long number;
		private long size;
		private long total;

		public EventPage() {
			super();
		}

		public EventPage(PageImpl<Event> page, User user) {
			this.lastPage = page.isLastPage();
			this.number = page.getNumber();
			for (Event event : page.getContent()) {
				this.content.add(new EventEntity(event, user));
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

		public List<EventEntity> getContent() {
			return content;
		}

		public void setContent(List<EventEntity> content) {
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

	public static class EventEntity {
		private String id;
		private String 	title;
		private Date 	datetime;
		private String 	description;
		private int 	capacity;
		private int 	entryFee;
		private int 	eventType;
		private int 	status;
		private String 	address;
		private String 	landmark;
		private String 	languages;
		private String 	organiser;
		private String 	orgPhone;
		private String 	orgEmail;
		private Integer isPast;
		private boolean isEditableByUser = false;

		public EventEntity(Event event, User user) {
			this.setId(event.getId());
			this.setTitle(event.getTitle());
			this.setDatetime(event.getDatetime());
			this.setDescription(event.getDescription());
			this.setCapacity(event.getCapacity());
			this.setEntryFee(event.getEntryFee());
			this.setEventType(event.getEventType());
			this.setStatus(event.getStatus());
			this.setAddress(event.getAddress());
			this.setLandmark(event.getLandmark());
			this.setLanguages(event.getLanguages());
			this.setOrganiser(event.getOrganiser());
			this.setOrgEmail(event.getOrgEmail());
			this.setOrgPhone(event.getOrgPhone());
			this.setIsPast( (new Date()).compareTo(event.getDatetime()) > 0 ? 1 : -1  );
			
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

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Date getDatetime() {
			return datetime;
		}

		public void setDatetime(Date datetime) {
			this.datetime = datetime;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getLanguages() {
			return languages;
		}

		public void setLanguages(String languages) {
			this.languages = languages;
		}

		public String getOrganiser() {
			return organiser;
		}

		public void setOrganiser(String organiser) {
			this.organiser = organiser;
		}

		public int getEntryFee() {
			return entryFee;
		}

		public void setEntryFee(int entryFee) {
			this.entryFee = entryFee;
		}

		public int getEventType() {
			return eventType;
		}

		public void setEventType(int eventType) {
			this.eventType = eventType;
		}

		public Integer getIsPast() {
			return isPast;
		}

		public void setIsPast(Integer isPast) {
			this.isPast = isPast;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getLandmark() {
			return landmark;
		}

		public void setLandmark(String landmark) {
			this.landmark = landmark;
		}

		public String getOrgPhone() {
			return orgPhone;
		}

		public void setOrgPhone(String orgPhone) {
			this.orgPhone = orgPhone;
		}

		public String getOrgEmail() {
			return orgEmail;
		}

		public void setOrgEmail(String orgEmail) {
			this.orgEmail = orgEmail;
		}
		
	}

	public void add(List<Event> eventArray) {
		for (Event event : eventArray) {
			this.eventArray.add(new EventEntity(event, null));
		}
	}

	public void add(Event event) {
		this.eventArray.add(new EventEntity(event, null));
	}

	public void add(List<Event> eventArray, User user) {
		for (Event event : eventArray) {
			this.eventArray.add(new EventEntity(event, user));
		}
	}

	public void add(Event event, User user) {
		this.eventArray.add(new EventEntity(event, user));
	}

	public static EventPage getPage(PageImpl<Event> page, User user) {
		EventPage res = new EventPage(page, user);
		return res;
	}

	public EventEntity getEventEntity(Event event, User user) {
		return new EventEntity(event, user);
	}

}
