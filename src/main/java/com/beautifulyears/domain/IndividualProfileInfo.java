package com.beautifulyears.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.beautifulyears.domain.menu.Menu;

public class IndividualProfileInfo {

	private String salutation;

	@TextIndexed(weight = 1)
	private String lastName;

	private int gender; // 0: female, 1: male.

	private Date dob;

	private Map<String, Date> otherDates;

	private String occupation;// working, not working, retired, studying

	@TextIndexed(weight = 2)
	private List<String> emotionalIssues;

	// @TextIndexed(weight = 3)
	// @DBRef
	private List<String> medicalIssues;

	@TextIndexed(weight = 4)
	private List<String> otherIssues;

	private String maritalStatus;

	private List<Language> spokenLanguage;

	// @TextIndexed(weight = 5)
	// @DBRef
	private List<String> hobbies;

	@TextIndexed(weight = 6)
	private List<String> otherHobbies;

	@TextIndexed(weight = 7)
	@DBRef
	private List<Menu> interests;

	@TextIndexed(weight = 8)
	private List<String> otherInterests;

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Map<String, Date> getOtherDates() {
		return otherDates;
	}

	public void setOtherDates(Map<String, Date> otherDates) {
		this.otherDates = otherDates;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public List<String> getEmotionalIssues() {
		return emotionalIssues;
	}

	public void setEmotionalIssues(List<String> emotionalIssues) {
		this.emotionalIssues = emotionalIssues;
	}

	public List<String> getMedicalIssues() {
		return medicalIssues;
	}

	public void setMedicalIssues(List<String> medicalIssues) {
		this.medicalIssues = medicalIssues;
	}

	public List<String> getOtherIssues() {
		return otherIssues;
	}

	public void setOtherIssues(List<String> otherIssues) {
		this.otherIssues = otherIssues;
	}

	public String getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public List<Language> getLanguage() {
		return spokenLanguage;
	}

	public void setLanguage(List<Language> spokenLanguage) {
		this.spokenLanguage = spokenLanguage;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public void setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
	}

	public List<String> getOtherHobbies() {
		return otherHobbies;
	}

	public void setOtherHobbies(List<String> otherHobbies) {
		this.otherHobbies = otherHobbies;
	}

	public List<Menu> getInterests() {
		return interests;
	}

	public void setInterests(List<Menu> interests) {
		this.interests = interests;
	}

	public List<String> getOtherInterests() {
		return otherInterests;
	}

	public void setOtherInterests(List<String> otherInterests) {
		this.otherInterests = otherInterests;
	}

	@Override
	public String toString() {
		return "IndividualProfileInfo [salutation=" + salutation + ", lastName=" + lastName + ", gender=" + gender
				+ ", dob=" + dob + ", otherDates=" + otherDates + ", occupation=" + occupation + ", emotionalIssues="
				+ emotionalIssues + ", medicalIssues=" + medicalIssues + ", otherIssues=" + otherIssues
				+ ", maritalStatus=" + maritalStatus + ", language=" + spokenLanguage + ", hobbies=" + hobbies
				+ ", otherHobbies=" + otherHobbies + ", interests=" + interests + ", otherInterests=" + otherInterests
				+ ", getSalutation()=" + getSalutation() + ", getLastName()=" + getLastName() + ", getGender()="
				+ getGender() + ", getDob()=" + getDob() + ", getOtherDates()=" + getOtherDates() + ", getOccupation()="
				+ getOccupation() + ", getEmotionalIssues()=" + getEmotionalIssues() + ", getMedicalIssues()="
				+ getMedicalIssues() + ", getOtherIssues()=" + getOtherIssues() + ", getMaritalStatus()="
				+ getMaritalStatus() + ", getLanguage()=" + getLanguage() + ", getHobbies()=" + getHobbies()
				+ ", getOtherHobbies()=" + getOtherHobbies() + ", getInterests()=" + getInterests()
				+ ", getOtherInterests()=" + getOtherInterests() + ", getClass()=" + getClass() + ", hashCode()="
				+ hashCode() + ", toString()=" + super.toString() + "]";
	}

}
