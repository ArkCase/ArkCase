/**
 * 
 */
package com.armedia.acm.form.casefile.model.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.casefile.model.Subject;

/**
 * @author riste.tutureski
 *
 */
public class EmployerSupervisor extends Subject{

	@XmlElement(name="employerSupervisorFirstName")
	@Override
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	@XmlElement(name="employerSupervisorLastName")
	@Override
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	@XmlElement(name="employerSupervisorPhoneNumber")
	@Override
	public String getPhoneNumber() {
		return super.getPhoneNumber();
	}

	@Override
	public void setPhoneNumber(String phoneNumber) {
		super.setPhoneNumber(phoneNumber);
	}
	
}
