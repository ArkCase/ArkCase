/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.casefile.model.frevvoxmlmarshal.Employee;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name="form_" + FrevvoFormName.CASE_FILE, namespace=FrevvoFormNamespace.CASE_FILE_NAMESPACE)
public class CaseFileForm {

	private Long id;
	private String number;
	private String title;
	private String type;
	private List<String> types;
	private Subject subject;
	private List<AddressHistory> addressHistory;
	private List<EmploymentHistory> employmentHistory;
	
	private String cmisFolderId;
	
	@XmlElement(name="caseId")
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

	@XmlElement(name="caseTitle")
	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		this.title = title;
	}

	@XmlElement(name="caseType")
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

	@XmlElement(name="subjectSection", type=Employee.class)
	public Subject getSubject() 
	{
		return subject;
	}
	
	public void setSubject(Subject subject) 
	{
		this.subject = subject;
	}

	@XmlElement(name="addressHistorySection")
	public List<AddressHistory> getAddressHistory() 
	{
		return addressHistory;
	}

	public void setAddressHistory(List<AddressHistory> addressHistory) 
	{
		this.addressHistory = addressHistory;
	}

	@XmlElement(name="employmentHistorySection")
	public List<EmploymentHistory> getEmploymentHistory() 
	{
		return employmentHistory;
	}

	public void setEmploymentHistory(List<EmploymentHistory> employmentHistory) 
	{
		this.employmentHistory = employmentHistory;
	}

	@XmlTransient
	public String getCmisFolderId() {
		return cmisFolderId;
	}

	public void setCmisFolderId(String cmisFolderId) 
	{
		this.cmisFolderId = cmisFolderId;
	}
	
}
