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
public class PeopleMainInformation extends MainInformation {

	@XmlElement(name="peopleTitle")
	@Override
	public String getTitle() {
		return super.getTitle();
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
	}

	@XmlElement(name="peopleAnonimuos")
	@Override
	public String getAnonymous() {
		return super.getAnonymous();
	}

	@Override
	public void setAnonymous(String anonymous) {
		super.setAnonymous(anonymous);
	}

	@XmlElement(name="peopleFirstName")
	@Override
	public String getFirstName() {
		return super.getFirstName();
	}

	@Override
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	@XmlElement(name="peopleLastName")
	@Override
	public String getLastName() {
		return super.getLastName();
	}

	@Override
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	@XmlElement(name="peopleType")
	@Override
	public String getType() {
		return super.getType();
	}

	@Override
	public void setType(String type) {
		super.setType(type);
	}

	@XmlElement(name="peopleDescription")	
	@Override
	public String getDescription() {
		return super.getDescription();
	}

	@Override
	public void setDescription(String description) {
		super.setDescription(description);
	}
	
}
