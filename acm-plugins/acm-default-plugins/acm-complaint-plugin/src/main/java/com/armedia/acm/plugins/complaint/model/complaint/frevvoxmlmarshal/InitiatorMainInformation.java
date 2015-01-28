/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint.frevvoxmlmarshal;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;

/**
 * @author riste.tutureski
 *
 */
public class InitiatorMainInformation extends MainInformation {

	@XmlElement(name="initiatorTitle")
	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@XmlElement(name="initiatorAnonymous")
	@Override
	public String getAnonymous() {
		return super.getAnonymous();
	}

	@Override
	public void setAnonymous(String anonymous) {
		super.setAnonymous(anonymous);
	}

	@XmlElement(name="initiatorFirstName")
	@Override
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	@XmlElement(name="initiatorLastName")
	@Override
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	@XmlElement(name="initiatorType")
	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@XmlElement(name="initiatorDescription")	
	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
	}
	
}
