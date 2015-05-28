/**
 * 
 */
package com.armedia.acm.plugins.person.service;

import java.util.List;

import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonIdentification;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;

/**
 * @author riste.tutureski
 *
 */
public interface PersonService {

	public Person get(Long id);
	
	/**
	 *  Add person identification to the Person object for given key (field name) and value
	 * 
	 * @param key
	 * @param value
	 * @param person
	 * @return
	 */
	public Person addPersonIdentification(String key, String value, Person person);
	
	/**
	 * Add person identifications for given list of keys (field names). The values are taken from Frevvo Person object
	 * using reflection
	 * 
	 * @param keys
	 * @param person
	 * @return
	 */
	public Person addPersonIdentifications(List<String> keys, Person person);
	
	/**
	 * This method will set all person identifications values in the appropriate field in Person object using reflection
	 * 
	 * @param personIdentifications
	 * @param person
	 */
	public Person setPersonIdentifications(List<PersonIdentification> personIdentifications, Person person);
	
	/**
	 * The method will return the type of the person created via Frevvo form.
	 * 
	 * @param person
	 * @return
	 */
	public String getPersonType(FrevvoPerson person);
	
	/**
	 * This method will return the list of person identification keys
	 * 
	 * @param person
	 * @return
	 */
	public List<String> getPersonIdentificationKeys(FrevvoPerson person);
	
}
