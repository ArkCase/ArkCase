/**
 * 
 */
package com.armedia.acm.plugins.person.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.plugins.person.model.Person;

/**
 * @author riste.tutureski
 *
 */
public class DefendantPerson extends Person implements FrevvoPerson  {

	private static final long serialVersionUID = 601307053343844821L;
	
	private String type;
	
	public DefendantPerson()
	{
		
	}
	
	public DefendantPerson(Person person)
	{
		setId(person.getId());
		setGivenName(person.getGivenName());
		setFamilyName(person.getFamilyName());
		setDateOfBirth(person.getDateOfBirth());
		setPersonIdentification(person.getPersonIdentification());
	}

	@XmlElement(name="defendantId")
	@Override
	public Long getId() {
        return super.getId();
    }

	@Override
    public void setId(Long id) {
        super.setId(id);
    }
	
	@XmlElement(name="defendantFirstName")
	@Override
	public String getGivenName() {
        return super.getGivenName();
    }

	@Override
    public void setGivenName(String givenName) {
        super.setGivenName(givenName);
    }
	
	@XmlElement(name="defendantLastName")
	@Override
	public String getFamilyName() {
        return super.getFamilyName();
    }

	@Override
    public void setFamilyName(String familyName) {
        super.setFamilyName(familyName);
    }
	
	@XmlElement(name="defendantDOB")
	@Override
	public Date getDateOfBirth() {
        return super.getDateOfBirth();
    }

	@Override
    public void setDateOfBirth(Date dateOfBirth) {
        super.setDateOfBirth(dateOfBirth);
    }

	@XmlElement(name="defendantType")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public List<String> getPersonIdentificationKeys() {
		return null;
	}

	@Override
	public Person returnBase()
	{
		Person person = new Person();
		
		person.setId(getId());
		person.setGivenName(getGivenName());
		person.setFamilyName(getFamilyName());
		person.setDateOfBirth(getDateOfBirth());
		person.setPersonIdentification(getPersonIdentification());
		
		return person;
	}	
	
}
