/**
 * 
 */
package com.armedia.acm.form.casefile.model.ps;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class Subject {

	private Long id;
	private String employeeId;
	private String title;
	private List<String> titles;
	private String suffix;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date dateOfBirth;
	private String socialSecurityNumber;
	private String phoneNumber;

	@XmlTransient
	public Long getId() 
	{
		return id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	@XmlTransient
	public String getEmployeeId() 
	{
		return employeeId;
	}

	public void setEmployeeId(String employeeId) 
	{
		this.employeeId = employeeId;
	}

	@XmlTransient
	public String getTitle() 
	{
		return title;
	}
	
	public void setTitle(String title) 
	{
		this.title = title;
	}

	@XmlTransient
	public List<String> getTitles() 
	{
		return titles;
	}
	
	public void setTitles(List<String> titles) 
	{
		this.titles = titles;
	}

	@XmlTransient
	public String getSuffix() 
	{
		return suffix;
	}
	
	public void setSuffix(String suffix) 
	{
		this.suffix = suffix;
	}

	@XmlTransient
	public String getFirstName() 
	{
		return firstName;
	}
	
	public void setFirstName(String firstName) 
	{
		this.firstName = firstName;
	}

	@XmlTransient
	public String getMiddleName() 
	{
		return middleName;
	}
	
	public void setMiddleName(String middleName) 
	{
		this.middleName = middleName;
	}

	@XmlTransient
	public String getLastName() 
	{
		return lastName;
	}
	
	public void setLastName(String lastName) 
	{
		this.lastName = lastName;
	}

	@XmlTransient
	public Date getDateOfBirth() 
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) 
	{
		this.dateOfBirth = dateOfBirth;
	}

	@XmlTransient
	public String getSocialSecurityNumber() 
	{
		return socialSecurityNumber;
	}

	public void setSocialSecurityNumber(String socialSecurityNumber) 
	{
		this.socialSecurityNumber = socialSecurityNumber;
	}

	@XmlTransient
	public String getPhoneNumber() 
	{
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) 
	{
		this.phoneNumber = phoneNumber;
	}
	
}
