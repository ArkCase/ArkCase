/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;

/**
 * @author riste.tutureski
 *
 */
public class Contact {
	
	private Long id;
	private String contactType;
	private SearchResult searchResult;
	private MainInformation mainInformation;
	private List<ContactMethod> communicationDevice;
	private List<Organization> organization;
	private List<PostalAddress> location;
	private PersonAlias alias;
	private String notes;
	
	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	@XmlTransient
	public SearchResult getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}

	@XmlTransient
	public MainInformation getMainInformation() {
		return mainInformation;
	}

	public void setMainInformation(MainInformation mainInformation) {
		this.mainInformation = mainInformation;
	}

	@XmlTransient
	public List<ContactMethod> getCommunicationDevice() {
		return communicationDevice;
	}

	public void setCommunicationDevice(List<ContactMethod> communicationDevice) {
		this.communicationDevice = communicationDevice;
	}

	@XmlTransient
	public List<Organization> getOrganization() {
		return organization;
	}

	public void setOrganization(List<Organization> organization) {
		this.organization = organization;
	}

	@XmlTransient
	public List<PostalAddress> getLocation() {
		return location;
	}

	public void setLocation(List<PostalAddress> location) {
		this.location = location;
	}

	@XmlTransient
	public PersonAlias getAlias() {
		return alias;
	}

	public void setAlias(PersonAlias alias) {
		this.alias = alias;
	}

	@XmlTransient
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public Contact returnBase() {
		return this;
	}
	
}
