/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.armedia.acm.form.casefile.model.AddressHistory;
import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.EmploymentHistory;
import com.armedia.acm.form.casefile.model.Subject;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory 
{

	public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
	{
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setTitle(form.getTitle());
		caseFile.setCaseType(form.getType());
		
		if (form.getSubject() != null)
		{
			PersonAssociation personAssociation = null;
			Person person = null;
			
			if (caseFile.getOriginator() != null)
			{
				personAssociation = caseFile.getOriginator();
			}
			else
			{
				personAssociation = new PersonAssociation();
			}
			
			if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
			{
				person = caseFile.getOriginator().getPerson();
			}
			else
			{
				person = new Person();
			}
			
			personAssociation.setPerson(person);
			
			caseFile.setOriginator(personAssociation);
			
			populatePerson(form, personAssociation, person);
		}
		
		return caseFile;
	}
	
	private void populatePerson(CaseFileForm form, PersonAssociation personAssociation, Person person)
	{
		Subject subject = form.getSubject();
		List<AddressHistory> addressHistoryArray = form.getAddressHistory();
		List<EmploymentHistory> employmentHistoryArray = form.getEmploymentHistory();
		
		personAssociation.setPersonType("Subject");
		
		person.setTitle(subject.getTitle());
		person.setGivenName(subject.getFirstName());
		person.setFamilyName(subject.getLastName());
		
		person.setAddresses(new ArrayList<PostalAddress>());
		person.setOrganizations(new ArrayList<Organization>());
		
		if (null != addressHistoryArray && addressHistoryArray.size() > 0)
		{
			for (AddressHistory addressHistory : addressHistoryArray)
			{
				person.getAddresses().addAll(Arrays.asList(addressHistory.getLocation()));
			}
		}
		
		if (null != employmentHistoryArray && employmentHistoryArray.size() > 0)
		{
			for (EmploymentHistory employmentHistory : employmentHistoryArray)
			{
				person.getOrganizations().addAll(Arrays.asList(employmentHistory.getOrganization()));
			}
		}
	}
	
}
