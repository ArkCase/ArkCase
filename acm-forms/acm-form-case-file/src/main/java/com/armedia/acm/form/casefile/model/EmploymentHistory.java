/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.plugins.person.model.Organization;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class EmploymentHistory {

	@XmlElements({
		@XmlElement(name="startDate"),
		@XmlElement(name="employmentStartDate")
	})
	private Date startDate;
	
	@XmlElements({
		@XmlElement(name="endDate"),
		@XmlElement(name="employmentEndDate")
	})
	private Date endDate;
	
	@XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="employerType")
	})
	private String type;
	
	@XmlElements({
		@XmlElement(name="organization"),
		@XmlElement(name="employmentOrganizationSection")
	})
    private Organization organization;
	
	@XmlElements({
		@XmlElement(name="supervisor"),
		@XmlElement(name="employmentSupervisorSection")
	})
	private Subject supervisor;
	
	@XmlElements({
		@XmlElement(name="reference"),
		@XmlElement(name="employmentReferenceSection")
	})
	private Subject reference;
	
	public Date getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Subject getSupervisor() 
	{
		return supervisor;
	}
	
	public void setSupervisor(Subject supervisor) 
	{
		this.supervisor = supervisor;
	}
	
	public Subject getReference() 
	{
		return reference;
	}
	
	public void setReference(Subject reference) 
	{
		this.reference = reference;
	}
	
}
