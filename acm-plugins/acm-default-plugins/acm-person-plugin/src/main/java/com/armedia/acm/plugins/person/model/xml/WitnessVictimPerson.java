/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.addressable.model.xml.WitnessVictimContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.WitnessVictimPostalAddress;
import com.armedia.acm.plugins.person.model.Person;

/**
 * @author riste.tutureski
 *
 */
public class WitnessVictimPerson extends Person {

	private static final long serialVersionUID = 601307053343844821L;
	
	private String type;
	private String availabilityDifficulties;
	
	public WitnessVictimPerson()
	{
		
	}
	
	public WitnessVictimPerson(Person person)
	{
		setId(person.getId());
		setGivenName(person.getGivenName());
		setFamilyName(person.getFamilyName());
		setDateOfBirth(person.getDateOfBirth());
		
		if (person.getAddresses() != null)
		{
			List<PostalAddress> addresses = new ArrayList<PostalAddress>();
			for (PostalAddress postalAddress : person.getAddresses())
    		{
				WitnessVictimPostalAddress a = new WitnessVictimPostalAddress(postalAddress);
    			addresses.add(a);
    		}
			setAddresses(addresses);
		}
		
		if (person.getContactMethods() != null)
        {
        	List<ContactMethod> contactMethods = new ArrayList<ContactMethod>();
        	for (ContactMethod contactMethod : person.getContactMethods())
    		{
        		WitnessVictimContactMethod c = new WitnessVictimContactMethod(contactMethod);
        		contactMethods.add(c);
    		}
        	setContactMethods(contactMethods);
        }
		
	}

	@XmlElement(name="witnessVictimId")
	@Override
	public Long getId() {
        return super.getId();
    }

	@Override
    public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="witnessVictimFirstName")
	@Override
	public String getGivenName() {
        return super.getGivenName();
    }

	@Override
    public void setGivenName(String givenName) {
        super.setGivenName(givenName);
    }
	
	@XmlElement(name="witnessVictimLastName")
	@Override
	public String getFamilyName() {
        return super.getFamilyName();
    }

	@Override
    public void setFamilyName(String familyName) {
        super.setFamilyName(familyName);
    }

	@XmlElement(name="witnessVictimType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	@XmlElement(name="witnessVictimDOB")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDateOfBirth() {
        return super.getDateOfBirth();
    }

	@Override
    public void setDateOfBirth(Date dateOfBirth) {
        super.setDateOfBirth(dateOfBirth);
    }

	@XmlElement(name="witnessVictimLocation", type=WitnessVictimPostalAddress.class)
	@Override
	public List<PostalAddress> getAddresses() {
        return super.getAddresses();
    }
	
	@Override
    public void setAddresses(List<PostalAddress> addresses) {
        super.setAddresses(addresses);
    }
	
	@XmlElement(name="witnessVictimContact", type=WitnessVictimContactMethod.class)
	@Override
	public List<ContactMethod> getContactMethods() {
        return super.getContactMethods();
    }

	@Override
    public void setContactMethods(List<ContactMethod> contactMethods) {
        super.setContactMethods(contactMethods);
    }
	
	@XmlElement(name="witnessVictimAvailDiff")
	public String getAvailabilityDifficulties() {
		return availabilityDifficulties;
	}

	public void setAvailabilityDifficulties(String availabilityDifficulties) {
		this.availabilityDifficulties = availabilityDifficulties;
	}

	@Override
	public Person returnBase()
	{
		Person person = new Person();
		
		person.setId(getId());
		person.setGivenName(getGivenName());
		person.setFamilyName(getFamilyName());
		person.setDateOfBirth(getDateOfBirth());
		
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
		
		
		return person;
	}
	
	
}
