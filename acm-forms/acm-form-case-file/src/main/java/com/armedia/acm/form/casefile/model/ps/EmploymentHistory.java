/**
 * 
 */
package com.armedia.acm.form.casefile.model.ps;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.casefile.model.ps.xml.EmployerReference;
import com.armedia.acm.form.casefile.model.ps.xml.EmployerSupervisor;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.xml.GeneralOrganization;

/**
 * @author riste.tutureski
 *
 */
public class EmploymentHistory {

	private Long id;
	private Date startDate;
	private Date endDate;
	private String type;
    private Organization organization;
	private Subject supervisor;
	private Subject reference;
	
	@XmlElement(name="employmentId")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="employmentStartDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}

	@XmlElement(name="employmentEndDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@XmlElement(name="employerType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="employmentOrganizationSection", type=GeneralOrganization.class)
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@XmlElement(name="employmentSupervisorSection", type=EmployerSupervisor.class)
	public Subject getSupervisor() 
	{
		return supervisor;
	}
	
	public void setSupervisor(Subject supervisor) 
	{
		this.supervisor = supervisor;
	}
	
	@XmlElement(name="employmentReferenceSection", type=EmployerReference.class)
	public Subject getReference() 
	{
		return reference;
	}
	
	public void setReference(Subject reference) 
	{
		this.reference = reference;
	}
	
}
