/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact {
	
	@XmlElements({
		@XmlElement(name="mainInformation"),
		@XmlElement(name="initiatorMainInformation"),
		@XmlElement(name="peopleMainInformation")
		
	})
	private MainInformation mainInformation;

	@XmlElements({
		@XmlElement(name="communicationDevice"),
		@XmlElement(name="initiatorCommunicationDevice"),
		@XmlElement(name="peopleCommunicationDevice")
		
	})
	private List<ContactMethod> communicationDevice;

	@XmlElements({
		@XmlElement(name="organization"),
		@XmlElement(name="initiatorOrganizationInformation"),
		@XmlElement(name="peopleOrganizationInformation")
		
	})
	private List<Organization> organization;

	@XmlElements({
		@XmlElement(name="location"),
		@XmlElement(name="initiatorLocationInformation"),
		@XmlElement(name="peopleLocationInformation")
		
	})
	private List<PostalAddress> location;

	@XmlElements({
		@XmlElement(name="alias"),
		@XmlElement(name="initiatorAliasInformation"),
		@XmlElement(name="peopleAliasInformation")
		
	})
	private PersonAlias alias;

	@XmlElements({
		@XmlElement(name="notes"),
		@XmlElement(name="initiatorNotes"),
		@XmlElement(name="peopleNotes")
		
	})
	private String notes;
	
	/**
	 * @return the mainInformation
	 */
	public MainInformation getMainInformation() {
		return mainInformation;
	}
	
	/**
	 * @param mainInformation the mainInformation to set
	 */
	public void setMainInformation(MainInformation mainInformation) {
		this.mainInformation = mainInformation;
	}
	
	/**
	 * @return the communicationDevice
	 */
	public List<ContactMethod> getCommunicationDevice() {
		return communicationDevice;
	}

	/**
	 * @param communicationDevice the communicationDevice to set
	 */
	public void setCommunicationDevice(List<ContactMethod> communicationDevice) {
		this.communicationDevice = communicationDevice;
	}

	/**
	 * @return the organization
	 */
	public List<Organization> getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(List<Organization> organization) {
		this.organization = organization;
	}

	/**
	 * @return the location
	 */
	public List<PostalAddress> getLocation() {
		return location;
	}
	
	/**
	 * @param location the location to set
	 */
	public void setLocation(List<PostalAddress> location) {
		this.location = location;
	}
	
	/**
	 * @return the alias
	 */
	public PersonAlias getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(PersonAlias alias) {
		this.alias = alias;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
