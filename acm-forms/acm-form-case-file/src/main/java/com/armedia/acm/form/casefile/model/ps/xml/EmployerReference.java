/**
 * 
 */
package com.armedia.acm.form.casefile.model.ps.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.casefile.model.ps.Subject;

/**
 * @author riste.tutureski
 *
 */
public class EmployerReference extends Subject{

	@XmlElement(name="employerReferenceFirstName")
	@Override
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	@XmlElement(name="employerReferenceLastName")
	@Override
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	@XmlElement(name="employerReferencePhoneNumber")
	@Override
	public String getPhoneNumber() {
		return super.getPhoneNumber();
	}

	@Override
	public void setPhoneNumber(String phoneNumber) {
		super.setPhoneNumber(phoneNumber);
	}
	
}
