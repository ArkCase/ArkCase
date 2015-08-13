/**
 * 
 */
package com.armedia.acm.plugins.person.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.frevvo.config.FrevvoFormUtils;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;

/**
 * @author riste.tutureski
 *
 */
public class PersonServiceImpl implements PersonService {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private PersonDao personDao;
	
	@Override
	public Person get(Long id) 
	{
		return getPersonDao().find(id);
	}
	
	@Override
	public Person addPersonIdentification(String key, String value, Person person) 
	{
		if (person != null)
		{
			if (key != null && value != null && !value.trim().isEmpty() )
			{
				boolean exists = false;
				if ( person.getIdentifications() != null )
				{
					for ( Identification pi : person.getIdentifications() )
					{
						if ( key.equals(pi.getIdentificationType())  )
						{
							pi.setIdentificationNumber(value);
							exists = true;
							break;
						}
					}
				}
	
				if ( ! exists )
				{
					if ( person.getIdentifications() == null )
					{
						person.setIdentifications(new ArrayList<Identification>());
					}
					
					Identification pi = new Identification();
					pi.setIdentificationNumber(value);
					pi.setIdentificationType(key);

					
					person.getIdentifications().add(pi);
				}
			}
		}
		
		return person;
	}
	
	@Override
	public Person addPersonIdentifications(List<String> keys, Person person) 
	{
		if (keys != null && person != null)
		{
			for (String key : keys)
			{
				String value = FrevvoFormUtils.get(person, key);
				
				Person personFromDatabase = new Person();
				if (person.getId() != null)
				{
					personFromDatabase = get(person.getId());
				}
				personFromDatabase = addPersonIdentification(key, value, personFromDatabase);
				
				person.setIdentifications(personFromDatabase.getIdentifications());
			}
		}
		
		return person;
	}
	
	@Override
	public Person setPersonIdentifications(List<Identification> identifications, Person person)
	{
		if (identifications != null && person != null)
		{
			for (Identification identification : identifications)
			{
				try
				{
					String key = identification.getIdentificationType();
					String value = identification.getIdentificationNumber();
					
					person = (Person) FrevvoFormUtils.set(person, key, value);
				}
				catch(Exception e)
				{
					LOG.debug("Silent catch of exeption while setting value of the property in the object Person. The property name maybe not exist, but execution should go forward.");
				}
			}
		}
		
		return person;
	}
	
	@Override
	public String getPersonType(FrevvoPerson person)
	{
		if (person != null)
		{
			return person.getType();
		}
		
		return null;
	}

	@Override
	public List<String> getPersonIdentificationKeys(FrevvoPerson person) 
	{
		if (person != null)
		{
			return person.getPersonIdentificationKeys();
		}
		
		return null;
	}

	public PersonDao getPersonDao() {
		return personDao;
	}

	public void setPersonDao(PersonDao personDao) {
		this.personDao = personDao;
	}
}
