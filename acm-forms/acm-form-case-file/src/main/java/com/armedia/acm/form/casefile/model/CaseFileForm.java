/**
 * 
 */
package com.armedia.acm.form.casefile.model;

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
public class CaseFileForm {

	private Long id;
	private String number;
	
	@XmlElements({
		@XmlElement(name="title"),
		@XmlElement(name="caseTitle")
	})
	private String title;
	
	@XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="caseType")
	})
	private String type;
	
	private List<String> types;
	
	@XmlElements({
		@XmlElement(name="subject"),
		@XmlElement(name="subjectSection")
	})
	private Subject subject;
	
	@XmlElements({
		@XmlElement(name="addressHistory"),
		@XmlElement(name="addressHistorySection")
	})
	private List<AddressHistory> addressHistory;
	
	@XmlElements({
		@XmlElement(name="employmentHistory"),
		@XmlElement(name="employmentHistorySection")
	})
	private List<EmploymentHistory> employmentHistory;
	
	private String cmisFolderId;
	
	public Long getId() 
	{
		return id;
	}

	public void setId(Long id) 
	{
		this.id = id;
	}

	public String getNumber() 
	{
		return number;
	}

	public void setNumber(String number) 
	{
		this.number = number;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	public String getType() 
	{
		return type;
	}

	public void setType(String type) 
	{
		this.type = type;
	}

	public List<String> getTypes() 
	{
		return types;
	}

	public void setTypes(List<String> types) 
	{
		this.types = types;
	}

	public Subject getSubject() 
	{
		return subject;
	}
	
	public void setSubject(Subject subject) 
	{
		this.subject = subject;
	}

	public List<AddressHistory> getAddressHistory() 
	{
		return addressHistory;
	}

	public void setAddressHistory(List<AddressHistory> addressHistory) 
	{
		this.addressHistory = addressHistory;
	}

	public List<EmploymentHistory> getEmploymentHistory() 
	{
		return employmentHistory;
	}

	public void setEmploymentHistory(List<EmploymentHistory> employmentHistory) 
	{
		this.employmentHistory = employmentHistory;
	}

	public String getCmisFolderId() {
		return cmisFolderId;
	}

	public void setCmisFolderId(String cmisFolderId) 
	{
		this.cmisFolderId = cmisFolderId;
	}
	
}
