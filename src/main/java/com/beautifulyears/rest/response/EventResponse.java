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
		private String title;
		private Date datetime;
		private String description;
		private int entryFee;
		private int perPerson;
		private int eventType;
		private int status;
		private String email;
		private String location;
		private String locLat;
		private String locLng;
		private String languages;
		private String phone;
		private String organiser;
		private Integer isPast;
		private boolean isEditableByUser = false;

		public EventEntity(Event event, User user) {
			this.setId(event.getId());
			this.setTitle(event.getTitle());
			this.setDatetime(event.getDatetime());
			this.setDescription(event.getDescription());
			this.setEntryFee(event.getEntryFee());
			this.setPerPerson(event.getPerPerson());
			this.setEventType(event.getEventType());
			this.setStatus(event.getStatus());
			this.setEmail(event.getEmail());
			this.setLocation(event.getLocation());
			this.setLocLat(event.getLocLat());
			this.setLocLng(event.getLocLng());
			this.setLanguages(event.getLanguages());
			this.setPhone(event.getPhone());
			this.setOrganiser(event.getOrganiser());
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

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getLanguages() {
			return languages;
		}

		public void setLanguages(String languages) {
			this.languages = languages;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
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

		public int getPerPerson() {
			return perPerson;
		}

		public void setPerPerson(int perPerson) {
			this.perPerson = perPerson;
		}

		public int getEventType() {
			return eventType;
		}

		public void setEventType(int eventType) {
			this.eventType = eventType;
		}

		public String getLocLat() {
			return locLat;
		}

		public void setLocLat(String locLat) {
			this.locLat = locLat;
		}

		public String getLocLng() {
			return locLng;
		}

		public void setLocLng(String locLng) {
			this.locLng = locLng;
		}

		public Integer getIsPast() {
			return isPast;
		}

		public void setIsPast(Integer isPast) {
			this.isPast = isPast;
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
