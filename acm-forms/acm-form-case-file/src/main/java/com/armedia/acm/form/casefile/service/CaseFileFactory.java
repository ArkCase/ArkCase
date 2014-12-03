/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.Arrays;
import java.util.List;

import com.armedia.acm.form.casefile.model.AddressHistory;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.Subject;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory 
{

	public CaseFile asAcmCaseFile(CaseFileForm form)
	{
		CaseFile caseFile = new CaseFile();
		
		caseFile.setTitle(form.getTitle());
		caseFile.setCaseType(form.getType());
		
		if (form.getSubject() != null)
		{
			PersonAssociation personAssociation = new PersonAssociation();
			Person person = new Person();
			
			personAssociation.setPerson(person);
			
			caseFile.setOriginator(personAssociation);
			
			populatePerson(form, personAssociation, person);
		}
		
		return caseFile;
	}
	
	private void populatePerson(CaseFileForm form, PersonAssociation personAssociation, Person person)
	{
		Subject subject = form.getSubject();
		List<AddressHistory> addressHistory = form.getAddressHistory();
		
		personAssociation.setPersonType("Subject");
		
		person.setTitle(subject.getTitle());
		person.setGivenName(subject.getFirstName());
		person.setFamilyName(subject.getLastName());
		
		if (null != addressHistory && addressHistory.size() > 0)
		{
			for (AddressHistory address : addressHistory)
			{
				person.getAddresses().addAll(Arrays.asList(address.getLocation()));
			}
		}
	}
	
}
