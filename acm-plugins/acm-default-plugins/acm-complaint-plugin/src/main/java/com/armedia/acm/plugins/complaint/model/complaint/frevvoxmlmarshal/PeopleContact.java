/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint.frevvoxmlmarshal;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.frevvoxmlmarshal.PeopleContactMethod;
import com.armedia.acm.plugins.addressable.model.frevvoxmlmarshal.PeoplePostalAddress;
import com.armedia.acm.plugins.complaint.model.complaint.Contact;
import com.armedia.acm.plugins.complaint.model.complaint.MainInformation;
import com.armedia.acm.plugins.complaint.model.complaint.SearchResult;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.PersonAlias;
import com.armedia.acm.plugins.person.model.frevvoxmlmarshal.PeopleOrganization;
import com.armedia.acm.plugins.person.model.frevvoxmlmarshal.PeoplePersonAlias;

/**
 * @author riste.tutureski
 *
 */
public class PeopleContact extends Contact {

	@XmlElement(name="peopleId")
	@Override
	public Long getId() {
		return super.getId();
	}

	public void setId(Long id) {
		super.setId(id);
	}
	
	@XmlElement(name="peopleContactType")
	@Override
	public String getContactType() {
		return super.getContactType();
	}

	@Override
	public void setContactType(String contactType) {
		super.setContactType(contactType);
	}

	@XmlElement(name="searchExistingPeople", type=PeopleSearchResult.class)
	@Override
	public SearchResult getSearchResult() {
		return super.getSearchResult();
	}

	@Override
	public void setSearchResult(SearchResult searchResult) {
		super.setSearchResult(searchResult);
	}

	@XmlElement(name="peopleMainInformation", type=PeopleMainInformation.class)
	@Override
	public MainInformation getMainInformation() {
		return super.getMainInformation();
	}

	@Override
	public void setMainInformation(MainInformation mainInformation) {
		super.setMainInformation(mainInformation);
	}

	@XmlElement(name="peopleCommunicationDevice", type=PeopleContactMethod.class)
	@Override
	public List<ContactMethod> getCommunicationDevice() {
		return super.getCommunicationDevice();
	}

	@Override
	public void setCommunicationDevice(List<ContactMethod> communicationDevice) {
		super.setCommunicationDevice(communicationDevice);
	}

	@XmlElement(name="peopleOrganizationInformation", type=PeopleOrganization.class)
	@Override
	public List<Organization> getOrganization() {
		return super.getOrganization();
	}

	@Override
	public void setOrganization(List<Organization> organization) {
		super.setOrganization(organization);
	}

	@XmlElement(name="peopleLocationInformation", type=PeoplePostalAddress.class)
	@Override
	public List<PostalAddress> getLocation() {
		return super.getLocation();
	}

	@Override
	public void setLocation(List<PostalAddress> location) {
		super.setLocation(location);
	}
	
	@XmlElement(name="peopleAliasInformation", type=PeoplePersonAlias.class)
	@Override
	public PersonAlias getAlias() {
		return super.getAlias();
	}

	@Override
	public void setAlias(PersonAlias alias) {
		super.setAlias(alias);
	}
	
	@XmlElement(name="peopleNotes")
	@Override
	public String getNotes() {
		return super.getNotes();
	}

	@Override
	public void setNotes(String notes) {
		super.setNotes(notes);
	}
	
	@Override
	public Contact returnBase() {
		Contact base = new Contact();
		
		base.setId(getId());
		base.setContactType(getContactType());
		base.setSearchResult(getSearchResult());
		base.setMainInformation(getMainInformation());
		base.setCommunicationDevice(getCommunicationDevice());
		base.setOrganization(getOrganization());
		base.setLocation(getLocation());
		base.setAlias(getAlias());
		base.setNotes(getNotes());
		
		return base;
	}
	
}
