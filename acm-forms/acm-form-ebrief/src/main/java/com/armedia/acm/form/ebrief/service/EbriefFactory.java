/**
 * 
 */
package com.armedia.acm.form.ebrief.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.armedia.acm.form.ebrief.model.EbriefConstants;
import com.armedia.acm.form.ebrief.model.EbriefForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.xml.DefendantPerson;
import com.armedia.acm.plugins.person.model.xml.OfficerPerson;
import com.armedia.acm.plugins.person.model.xml.PoliceWitnessPerson;
import com.armedia.acm.plugins.person.model.xml.VictimPerson;
import com.armedia.acm.plugins.person.model.xml.WitnessVictimPerson;

/**
 * @author riste.tutureski
 *
 */
public class EbriefFactory extends FrevvoFormFactory{

	public CaseFile asAcmCaseFile(EbriefForm form, CaseFile caseFile)
	{
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setTitle(EbriefConstants.EBRIEF);
		caseFile.setCaseType(form.getType());
		caseFile.setDetails(form.getNotes());
		caseFile.setParticipants(asAcmParticipants(form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));
		caseFile.setPersonAssociations(getPersonAssociations(form));
		
		return caseFile;
	}
	
	public EbriefForm asFrevvoEbriefForm(CaseFile caseFile, EbriefForm form, FrevvoFormAbstractService formService)
	{
		if (caseFile != null && form != null)
		{
			form.setId(caseFile.getId());
			String cmisFolderId = formService.findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
			form.setCmisFolderId(cmisFolderId);
			
			// TODO: This will need for editing ....
		}
		
		return form;
	}
	
	private List<PersonAssociation> getPersonAssociations(EbriefForm form)
	{
		List<PersonAssociation> paArray = new ArrayList<>();
		if (form.getWitnessVictims() != null)
		{
			paArray = populatePersonAssociations(form.getWitnessVictims(), paArray);
		}
		
		if (form.getDefendant() != null)
		{
			paArray = populatePersonAssociations(Arrays.asList(form.getDefendant()), paArray);
		}
		
		if (form.getVictim() != null)
		{
			paArray = populatePersonAssociations(Arrays.asList(form.getVictim()), paArray);
		}
		
		if (form.getOfficer() != null)
		{
			paArray = populatePersonAssociations(Arrays.asList(form.getOfficer()), paArray);
		}
		
		if (form.getPoliceWinesses() != null)
		{
			paArray = populatePersonAssociations(form.getPoliceWinesses(), paArray);
		}
		
		return paArray;
	}
	
	private List<PersonAssociation> populatePersonAssociations(List<Person> persons, List<PersonAssociation> paArray)
	{
		if (paArray == null)
		{
			paArray = new ArrayList<>();
		}
		
		if (persons != null)
		{
			List<PersonAssociation> paArrayLocal = persons.stream()
														  .map(person -> {
															  PersonAssociation pa = new PersonAssociation();
															  pa.setPerson(person.returnBase());
															  pa.setPersonType(getPersonType(person));
															  return pa;
														  })
														  .collect(Collectors.toList());
			
			paArray.addAll(paArrayLocal);
		}
		
		return paArray;
	}
	
	private String getPersonType(Person person)
	{
		if (person != null)
		{
			if (person instanceof WitnessVictimPerson)
			{
				return ((WitnessVictimPerson) person).getType();
			}
			
			if (person instanceof DefendantPerson)
			{
				return ((DefendantPerson) person).getType();
			}
			
			if (person instanceof VictimPerson)
			{
				return ((VictimPerson) person).getType();
			}
			
			if (person instanceof OfficerPerson)
			{
				return ((OfficerPerson) person).getType();
			}
			
			if (person instanceof PoliceWitnessPerson)
			{
				return ((PoliceWitnessPerson) person).getType();
			}
		}
		
		return null;
	}
	
}
