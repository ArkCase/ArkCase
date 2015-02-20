/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.PeopleContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.PeoplePostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;

/**
 * @author riste.tutureski
 *
 */
public class PeoplePerson extends Person {

	private static final long serialVersionUID = 601307053343844821L;
	
	private List<String> titles;
	private String type;
	private List<String> types;
	
	public PeoplePerson()
	{
		
	}
	
	public PeoplePerson(Person person)
	{
		setId(person.getId());
		setTitle(person.getTitle());
		setGivenName(person.getGivenName());
		setFamilyName(person.getFamilyName());
		
		if (person.getAddresses() != null)
		{
			List<PostalAddress> addresses = new ArrayList<PostalAddress>();
			for (PostalAddress postalAddress : person.getAddresses())
    		{
				PeoplePostalAddress a = new PeoplePostalAddress(postalAddress);
    			addresses.add(a);
    		}
			setAddresses(addresses);
		}
		
		if (person.getContactMethods() != null)
        {
        	List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        	for (ContactMethod contactMethod : person.getContactMethods())
    		{
        		PeopleContactMethod c = new PeopleContactMethod(contactMethod);
        		contactMethods.add(c);
    		}
        	setContactMethods(contactMethods);
        }
		
		if (person.getOrganizations() != null)
        {
        	List<Organization> organizations = new ArrayList<Organization>();
        	for (Organization organization : person.getOrganizations())
    		{
    			PeopleOrganization o = new PeopleOrganization(organization);
    			organizations.add(o);
    		}
            setOrganizations(organizations);
        }
		
	}

	@XmlElement(name="peopleId")
	@Override
	public Long getId() {
        return super.getId();
    }

	@Override
    public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="peopleTitle")
	@Override
	public String getTitle() {
        return super.getTitle();
    }

	@Override
    public void setTitle(String title) {
        super.setTitle(title);
    }

	@XmlTransient
	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
	@XmlElement(name="peopleFirstName")
	@Override
	public String getGivenName() {
        return super.getGivenName();
    }

	@Override
    public void setGivenName(String givenName) {
        super.setGivenName(givenName);
    }
	
	@XmlElement(name="peopleLastName")
	@Override
	public String getFamilyName() {
        return super.getFamilyName();
    }

	@Override
    public void setFamilyName(String familyName) {
        super.setFamilyName(familyName);
    }

	@XmlElement(name="peopleType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlTransient
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	@XmlElement(name="peopleLocation", type=PeoplePostalAddress.class)
	@Override
	public List<PostalAddress> getAddresses() {
        return super.getAddresses();
    }
	
	@Override
    public void setAddresses(List<PostalAddress> addresses) {
        super.setAddresses(addresses);
    }
	
	@XmlElement(name="peopleContactMethod", type=PeopleContactMethod.class)
	@Override
	public List<ContactMethod> getContactMethods() {
        return super.getContactMethods();
    }

	@Override
    public void setContactMethods(List<ContactMethod> contactMethods) {
        super.setContactMethods(contactMethods);
    }
	
	@XmlElement(name="peopleOrganization", type=PeopleOrganization.class)
	@Override
	public List<Organization> getOrganizations() {
        return super.getOrganizations();
    }

	@Override
    public void setOrganizations(List<Organization> organizations) {
        super.setOrganizations(organizations);
    }
	
	@Override
	public Person returnBase()
	{
		Person person = new Person();
		
		person.setId(getId());
		person.setTitle(getTitle());
		person.setGivenName(getGivenName());
		person.setFamilyName(getFamilyName());
		
		if (getAddresses() != null)
		{
			List<PostalAddress> addresses = new ArrayList<PostalAddress>();
			for (PostalAddress postalAddress : getAddresses())
    		{
    			PostalAddress base = postalAddress.returnBase();
    			addresses.add(base);
    		}
			person.setAddresses(addresses);
		}
		
		if (getContactMethods() != null)
        {
        	List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        	for (ContactMethod contactMethod : getContactMethods())
    		{
        		ContactMethod base = contactMethod.returnBase();
        		contactMethods.add(base);
    		}
        	person.setContactMethods(contactMethods);
        }
		
		if (getOrganizations() != null)
        {
        	List<Organization> organizations = new ArrayList<Organization>();
        	for (Organization organization : getOrganizations())
    		{
    			Organization base = organization.returnBase();
    			organizations.add(base);
    		}
            person.setOrganizations(organizations);
        }
		
		
		return person;
	}
	
	
}
