/**
 * 
 */
package com.armedia.acm.form.ebrief.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import org.eclipse.persistence.indirection.IndirectList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.armedia.acm.form.ebrief.model.EbriefConstants;
import com.armedia.acm.form.ebrief.model.EbriefForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonIdentification;
import com.armedia.acm.plugins.person.model.xml.DefendantPerson;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;
import com.armedia.acm.plugins.person.model.xml.OfficerPerson;
import com.armedia.acm.plugins.person.model.xml.PoliceWitnessPerson;
import com.armedia.acm.plugins.person.model.xml.VictimPerson;
import com.armedia.acm.plugins.person.model.xml.WitnessVictimPerson;
import com.armedia.acm.plugins.person.service.PersonService;

/**
 * @author riste.tutureski
 *
 */
public class EbriefFactory extends FrevvoFormFactory{
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	private PersonAssociationDao personAssociationDao;
	private PersonService personService;
	
	public CaseFile asAcmCaseFile(EbriefForm form, CaseFile caseFile)
	{
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setId(form.getId());
		caseFile.setTitle(EbriefConstants.EBRIEF);
		caseFile.setCaseType(form.getType());
		caseFile.setDetails(form.getNotes());
		caseFile.setParticipants(getParticipants(caseFile.getParticipants(), form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));
		caseFile.setPersonAssociations(getPersonAssociations(form));
		
		return caseFile;
	}
	
	public EbriefForm asFrevvoEbriefForm(CaseFile caseFile, EbriefForm form, FrevvoFormAbstractService formService)
	{
		if (form == null)
		{
			form = new EbriefForm();
		}
		
		if (caseFile != null)
		{
			form.setId(caseFile.getId());
			form.setType(caseFile.getCaseType());
			form.setNotes(caseFile.getDetails());
			form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));
			form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
			form.setWitnessVictims(getWitnessVictims(caseFile.getPersonAssociations()));
			form.setDefendant(getDefendant(caseFile.getPersonAssociations()));
			form.setVictim(getVictim(caseFile.getPersonAssociations()));
			form.setOfficer(getOfficer(caseFile.getPersonAssociations()));
			form.setPoliceWinesses(getPoliceWitnesses(caseFile.getPersonAssociations()));
			String cmisFolderId = formService.findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
			form.setCmisFolderId(cmisFolderId);
		}
		
		return form;
	}
	
	private List<PersonAssociation> getPersonAssociations(EbriefForm form)
	{
		List<PersonAssociation> paArray = new ArrayList<>();
		if (form.getWitnessVictims() != null)
		{
			paArray = populatePersonAssociations(form.getId(), form.getWitnessVictims(), paArray);
		}
		
		if (form.getDefendant() != null)
		{
			paArray = populatePersonAssociations(form.getId(), Arrays.asList(form.getDefendant()), paArray);
		}
		
		if (form.getVictim() != null)
		{
			paArray = populatePersonAssociations(form.getId(), Arrays.asList(form.getVictim()), paArray);
		}
		
		if (form.getOfficer() != null)
		{
			paArray = populatePersonAssociations(form.getId(), Arrays.asList(form.getOfficer()), paArray);
		}
		
		if (form.getPoliceWinesses() != null)
		{
			paArray = populatePersonAssociations(form.getId(), form.getPoliceWinesses(), paArray);
		}
		
		return paArray;
	}
	
	private List<PersonAssociation> populatePersonAssociations(Long id, List<Person> persons, List<PersonAssociation> paArray)
	{
		if (paArray == null)
		{
			paArray = new ArrayList<>();
		}
		
		if (persons != null)
		{
			List<PersonAssociation> paArrayLocal = persons.stream()
														  .map(person -> {
															  String personType = getPersonService().getPersonType((FrevvoPerson) person);
															  
															  PersonAssociation pa = getPersonAssociationDao().findByPersonIdPersonTypeParentIdParentTypeSilent(person.getId(), personType, id, FrevvoFormName.CASE_FILE.toUpperCase());
															  
															  if (pa == null)
															  {
																  pa = new PersonAssociation();
															  }
															  
															  List<String> keys = getPersonService().getPersonIdentificationKeys((FrevvoPerson) person);
															  person = getPersonService().addPersonIdentifications(keys, person);
															  
															  pa.setPerson(person.returnBase());
															  pa.setPersonType(personType);
															  pa.setParentId(id);
															  pa.setParentType(FrevvoFormName.CASE_FILE.toUpperCase());
															  return pa;
														  })
														  .collect(Collectors.toList());
			
			paArray.addAll(paArrayLocal);
		}
		
		return paArray;
	}
	
	private List<Person> getWitnessVictims(List<PersonAssociation> pas)
	{
		List<Person> witnessVictims = new ArrayList<>();
		
		List<Person> witnesses = findPersonsByType(pas, EbriefConstants.WITNESS);
		List<Person> victims = findPersonsByType(pas, EbriefConstants.VICTIM);
		
		if (witnesses != null)
		{
			witnesses = witnesses.stream()
			   				     .map(element -> {
			   				    	WitnessVictimPerson wvp = new WitnessVictimPerson(element);
			   				    	wvp.setType(EbriefConstants.WITNESS);
			   				    	
			   				    	List<PersonIdentification> personIdentifications = element.getPersonIdentification();
			   				    	wvp = (WitnessVictimPerson) getPersonService().setPersonIdentifications(personIdentifications, wvp);
			   				    	
			   				    	return wvp;
			   				     })
			   				     .collect(Collectors.toList());
			
			witnessVictims.addAll(witnesses);
		}
		
		if (victims != null)
		{
			victims = victims.stream()
			   				     .map(element -> {
			   				    	WitnessVictimPerson wvp = new WitnessVictimPerson(element);
			   				    	wvp.setType(EbriefConstants.VICTIM);
			   				    	
			   				    	List<PersonIdentification> personIdentifications = element.getPersonIdentification();
			   				    	wvp = (WitnessVictimPerson) getPersonService().setPersonIdentifications(personIdentifications, wvp);
			   				    	
			   				    	return wvp;
			   				      })
			   				     .collect(Collectors.toList());
			
			witnessVictims.addAll(victims);
		}
		
		return witnessVictims;
	}
	
	private Person getDefendant(List<PersonAssociation> pas)
	{
		DefendantPerson defendant = null;
		
		if (pas != null)
		{
			Person p = findPersonByType(pas, EbriefConstants.DEFENDANT);
			
			if (p != null)
			{
				defendant = new DefendantPerson(p);
				defendant.setType(EbriefConstants.DEFENDANT);
				
				List<PersonIdentification> personIdentifications = p.getPersonIdentification();
				defendant = (DefendantPerson) getPersonService().setPersonIdentifications(personIdentifications, defendant);
			}
		}
		
		return defendant;
	}
	
	private Person getVictim(List<PersonAssociation> pas)
	{
		VictimPerson victim = null;
		
		if (pas != null)
		{
			Person p = findPersonByType(pas, EbriefConstants.PRIMARY_VICTIM);
			
			if (p != null)
			{
				victim = new VictimPerson(p);
				victim.setType(EbriefConstants.PRIMARY_VICTIM);
				
				List<PersonIdentification> personIdentifications = p.getPersonIdentification();
				victim = (VictimPerson) getPersonService().setPersonIdentifications(personIdentifications, victim);
			}
		}
		
		return victim;
	}
	
	private Person getOfficer(List<PersonAssociation> pas)
	{
		OfficerPerson officer = null;
		
		if (pas != null)
		{
			Person p = findPersonByType(pas, EbriefConstants.INVESTIGATING_OFFICER);
			
			if (p != null)
			{
				officer = new OfficerPerson(p);
				officer.setType(EbriefConstants.INVESTIGATING_OFFICER);
				
				List<PersonIdentification> personIdentifications = p.getPersonIdentification();
				officer = (OfficerPerson) getPersonService().setPersonIdentifications(personIdentifications, officer);
			}
		}
		
		return officer;
	}
	
	private List<Person> getPoliceWitnesses(List<PersonAssociation> pas)
	{
		List<Person> policeWitnesses = findPersonsByType(pas, EbriefConstants.POLICE_WITNESS);
		
		if (policeWitnesses != null)
		{
			policeWitnesses = policeWitnesses.stream()
										     .map(element -> {
										    	 PoliceWitnessPerson pwp = new PoliceWitnessPerson(element);
										    	 pwp.setType(EbriefConstants.POLICE_WITNESS);
										    	 
										    	 List<PersonIdentification> personIdentifications = element.getPersonIdentification();
										    	 pwp = (PoliceWitnessPerson) getPersonService().setPersonIdentifications(personIdentifications, pwp);
													
										    	 return pwp;
										     })
										     .collect(Collectors.toList());;
		}
		
		return policeWitnesses;
	}
	
	private Person findPersonByType(List<PersonAssociation> pas, String type)
	{
		if (pas != null && type != null)
		{
			// There is a bug in JPA with IndirectList and Java 8 for "stream" - https://bugs.eclipse.org/bugs/show_bug.cgi?id=433075
			// I guess in the next JPA version this is fixed (we are using JPA version 2.5.2 which is the same version that the bug is raised)
			// Line below is quick fix for that bug
			pas = pas instanceof IndirectList ? new ArrayList<>(pas) : pas;
			
			Person p = null;
			
			// Stream "get()" method throws exception if the "filter(...)" and "findFirst()" will not return any values
			// We need to continue with execution if that kind of person is not found
			try
			{
				p = pas.stream()
						  .filter(element -> type.equals(element.getPersonType()))
						  .findFirst()
						  .map(element -> element.getPerson())
						  .get();
			}
			catch(Exception e)
			{
				LOG.debug("The person is not found. Continue with execution.");
			}
			
			return p;
		}
		
		return null;
	}
	
	private List<Person> findPersonsByType(List<PersonAssociation> pas, String type)
	{		
		if (pas != null && type != null)
		{
			// There is a bug in JPA with IndirectList and Java 8 for "stream" - https://bugs.eclipse.org/bugs/show_bug.cgi?id=433075
			// I guess in the next JPA version this is fixed (we are using JPA version 2.5.2 which is the same version that the bug is raised)
			// Line below is quick fix for that bug
			pas = pas instanceof IndirectList ? new ArrayList<>(pas) : pas;
			
			List<Person> ps = pas.stream()
					  .filter(element -> type.equals(element.getPersonType()))
					  .map(element -> element.getPerson())
					  .collect(Collectors.toList());
					  
			return ps;
		}
		
		return null;
	}

	public PersonAssociationDao getPersonAssociationDao() {
		return personAssociationDao;
	}

	public void setPersonAssociationDao(PersonAssociationDao personAssociationDao) {
		this.personAssociationDao = personAssociationDao;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
}
