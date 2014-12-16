/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Subject {

	@XmlElements({
		@XmlElement(name="id"),
		@XmlElement(name="personId")
	})
	private Long id;
	
	@XmlElements({
		@XmlElement(name="id"),
		@XmlElement(name="employeeId")
	})
	private String employeeId;
	
	@XmlElements({
		@XmlElement(name="title"),
		@XmlElement(name="employeeTitle")
	})
	private String title;
	private List<String> titles;
	
	@XmlElements({
		@XmlElement(name="suffix"),
		@XmlElement(name="employeeSuffix")
	})
	private String suffix;
	
	@XmlElements({
		@XmlElement(name="firstName"),
		@XmlElement(name="employeeFirstName"),
		@XmlElement(name="referenceFirstName"),
		@XmlElement(name="employerFirstName"),
		@XmlElement(name="employerSupervisorFirstName"),
		@XmlElement(name="employerReferenceFirstName")
	})
	private String firstName;
	
	@XmlElements({
		@XmlElement(name="middleName"),
		@XmlElement(name="employeeMiddleName")
	})
	private String middleName;
	
	@XmlElements({
		@XmlElement(name="lastName"),
		@XmlElement(name="employeeLastName"),
		@XmlElement(name="referenceLastName"),
		@XmlElement(name="employerLastName"),
		@XmlElement(name="employerSupervisorLastName"),
		@XmlElement(name="employerReferenceLastName")
	})
	private String lastName;
	
	@XmlElements({
		@XmlElement(name="dateOfBirth"),
		@XmlElement(name="employeeDateOfBirth")
	})
	private Date dateOfBirth;
	
	@XmlElements({
		@XmlElement(name="socialSecurityNumber"),
		@XmlElement(name="employeeSocialSecurityNumber")
	})
	private String socialSecurityNumber;
	
	@XmlElements({
		@XmlElement(name="phoneNumber"),
		@XmlElement(name="referencePhoneNumber"),
		@XmlElement(name="employerPhoneNumber"),
		@XmlElement(name="employerSupervisorPhoneNumber"),
		@XmlElement(name="employerReferencePhoneNumber")
	})
	private String phoneNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}
	
	public List<String> getTitles() 
	{
		return titles;
	}
	
	public void setTitles(List<String> titles) 
	{
		this.titles = titles;
	}
	
	public String getSuffix() 
	{
		return suffix;
	}
	
	public void setSuffix(String suffix) 
	{
		this.suffix = suffix;
	}
	
	public String getFirstName() 
	{
		return firstName;
	}
	
	public void setFirstName(String firstName) 
	{
		this.firstName = firstName;
	}
	
	public String getMiddleName() 
	{
		return middleName;
	}
	
	public void setMiddleName(String middleName) 
	{
		this.middleName = middleName;
	}
	
	public String getLastName() 
	{
		return lastName;
	}
	
	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
	}
	
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSocialSecurityNumber() {
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) {
		this.socialSecurityNumber = socialSecurityNumber;
	}

	public String getPhoneNumber() 
	{
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) 
	{
		this.phoneNumber = phoneNumber;
	}
	
}
