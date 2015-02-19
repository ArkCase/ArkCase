/**
 * 
 */
package com.armedia.acm.form.casefile.model.ps.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.form.casefile.model.ps.Subject;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class Employee extends Subject{

	@XmlElement(name="personId")
	@Override
	public Long getId()
	{
		return super.getId();
	}
	
	@Override
	public void setId(Long id)
	{
		super.setId(id);
	}
	
	@XmlElement(name="employeeId")
	@Override
	public String getEmployeeId() {
		return super.getEmployeeId();
	}

	@Override
	public void setEmployeeId(String employeeId) {
		super.setEmployeeId(employeeId);
	}
	
	@XmlElement(name="employeeTitle")
	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}
	
	@XmlElement(name="employeeSuffix")
	@Override
	public String getSuffix() {
		return super.getSuffix();
	}

	@Override
	public void setSuffix(String suffix) {
		super.setSuffix(suffix);
	}
	
	@XmlElement(name="employeeFirstName")
	@Override
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}
	
	@XmlElement(name="employeeMiddleName")
	@Override
	public String getMiddleName() {
		return super.getMiddleName();
	}

	@Override
	public void setMiddleName(String middleName) {
		super.setMiddleName(middleName);
	}
	
	@XmlElement(name="employeeLastName")
	@Override
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}
	
	@XmlElement(name="employeeDateOfBirth")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	@Override
	public Date getDateOfBirth() {
		return super.getDateOfBirth();
	}

	@Override
	public void setDateOfBirth(Date dateOfBirth) {
		super.setDateOfBirth(dateOfBirth);
	}
	
	@XmlElement(name="employeeSocialSecurityNumber")
	@Override
	public String getSocialSecurityNumber() {
		return super.getSocialSecurityNumber();
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		super.setSocialSecurityNumber(socialSecurityNumber);
	}
	
}
