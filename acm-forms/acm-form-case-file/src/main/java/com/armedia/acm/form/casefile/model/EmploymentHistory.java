/**
 * 
 */
package com.armedia.acm.form.casefile.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

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
		@XmlElement(name="employer"),
		@XmlElement(name="employmentEmployerSection")
	})
	private Subject employer;
	
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

	public Subject getEmployer() 
	{
		return employer;
	}
	
	public void setEmployer(Subject employer) 
	{
		this.employer = employer;
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
