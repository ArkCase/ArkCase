/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.OfficerContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.OfficerPostalAddress;
import com.armedia.acm.plugins.person.model.Person;

/**
 * @author riste.tutureski
 *
 */
public class OfficerPerson extends Person implements FrevvoPerson {

	private static final long serialVersionUID = 601307053343844821L;
	
	private String type;
	private String leaveDated;
	private String rank;
	private String idNumber;
	private List<String> personIdentificationKeys = Arrays.asList("leaveDated", "rank", "idNumber");
	
	public OfficerPerson()
	{
		
	}
	
	public OfficerPerson(Person person)
	{
		setId(person.getId());
		setGivenName(person.getGivenName());
		setFamilyName(person.getFamilyName());
		
		if (person.getAddresses() != null)
		{
			List<PostalAddress> addresses = new ArrayList<PostalAddress>();
			for (PostalAddress postalAddress : person.getAddresses())
    		{
				OfficerPostalAddress a = new OfficerPostalAddress(postalAddress);
    			addresses.add(a);
    		}
			setAddresses(addresses);
		}
		
		if (person.getContactMethods() != null)
        {
        	List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        	for (ContactMethod contactMethod : person.getContactMethods())
    		{
        		OfficerContactMethod c = new OfficerContactMethod(contactMethod);
        		contactMethods.add(c);
    		}
        	setContactMethods(contactMethods);
        }
		
		setPersonIdentification(person.getPersonIdentification());
	}

	@XmlElement(name="officerId")
	@Override
	public Long getId() {
        return super.getId();
    }

	@Override
    public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="officerFirstName")
	@Override
	public String getGivenName() {
        return super.getGivenName();
    }

	@Override
    public void setGivenName(String givenName) {
        super.setGivenName(givenName);
    }
	
	@XmlElement(name="officerLastName")
	@Override
	public String getFamilyName() {
        return super.getFamilyName();
    }

	@Override
    public void setFamilyName(String familyName) {
        super.setFamilyName(familyName);
    }

	@XmlElement(name="officerType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="officerLocation", type=OfficerPostalAddress.class)
	@Override
	public List<PostalAddress> getAddresses() {
        return super.getAddresses();
    }
	
	@Override
    public void setAddresses(List<PostalAddress> addresses) {
        super.setAddresses(addresses);
    }
	
	@XmlElement(name="officerContact", type=OfficerContactMethod.class)
	@Override
	public List<ContactMethod> getContactMethods() {
        return super.getContactMethods();
    }

	@Override
    public void setContactMethods(List<ContactMethod> contactMethods) {
        super.setContactMethods(contactMethods);
    }

	@XmlElement(name="officerLeaveDates")
	public String getLeaveDated() {
		return leaveDated;
	}

	public void setLeaveDated(String leaveDated) {
		this.leaveDated = leaveDated;
	}
	
	@XmlElement(name="officerRank")
	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	@XmlElement(name="officerIDNumber")
	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	@XmlTransient
	@Override
	public List<String> getPersonIdentificationKeys() {
		return personIdentificationKeys;
	}

	public void setPersonIdentificationKeys(List<String> personIdentificationKeys) {
		this.personIdentificationKeys = personIdentificationKeys;
	}

	@Override
	public Person returnBase()
	{
		Person person = new Person();
		
		person.setId(getId());
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
		
		person.setPersonIdentification(getPersonIdentification());
		
		return person;
	}
	
	
}
