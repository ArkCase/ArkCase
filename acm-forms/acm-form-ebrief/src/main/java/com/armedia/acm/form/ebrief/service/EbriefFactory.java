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

import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.form.ebrief.model.EbriefConstants;
import com.armedia.acm.form.ebrief.model.EbriefForm;
import com.armedia.acm.form.ebrief.model.xml.EbriefDetails;
import com.armedia.acm.form.ebrief.model.xml.EbriefInformation;
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
import com.armedia.acm.plugins.person.service.PersonService;
import com.armedia.acm.services.participants.model.ParticipantTypes;

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
		caseFile.setCaseType(form.getInformation().getType());
		caseFile.setPersonAssociations(getPersonAssociations(form));
		
		ParticipantItem item = new ParticipantItem();
		item.setId(form.getDetails().getAssignedToId());
		item.setType(ParticipantTypes.ASSIGNEE);
		item.setName(form.getDetails().getAssignedTo());
		item.setValue(form.getDetails().getAssignedToUserId());
		
		caseFile.setParticipants(getParticipants(caseFile.getParticipants(), Arrays.asList(item), null, caseFile.getObjectType()));
		
		return caseFile;
	}
	
	public EbriefForm asFrevvoEbriefForm(CaseFile caseFile, EbriefForm form, FrevvoFormAbstractService formService)
	{
		try
		{
			if (form == null)
			{
				form = new EbriefForm();
			}
			
			if (form.getInformation() == null)
			{
				form.setInformation(new EbriefInformation());
			}
			
			if (caseFile != null)
			{
				form.setId(caseFile.getId());
				form.getInformation().setType(caseFile.getCaseType());
				form.getInformation().setNumber(caseFile.getCaseNumber());
				form.setDefendants(getDefendants(caseFile.getPersonAssociations()));
				String cmisFolderId = formService.findFolderIdForAttachments(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
				form.setCmisFolderId(cmisFolderId);
				
				List<ParticipantItem> items = asFrevvoParticipants(caseFile.getParticipants());
				
				if (items != null)
				{
					for (ParticipantItem item : items)
					{
						if (ParticipantTypes.ASSIGNEE.equals(item.getType()))
						{
							if (form.getDetails() == null)
							{
								form.setDetails(new EbriefDetails());
							}
							
							form.getDetails().setAssignedToId(item.getId());
							form.getDetails().setAssignedToUserId(item.getValue());
							form.getDetails().setAssignedTo(item.getName());
							
							break;
						}
					}
				}
			}
		}
		catch (Exception e) 
		{
			LOG.error("Cannot convert Object to Frevvo form.", e);
		}
		
		return form;
	}
	
	private List<PersonAssociation> getPersonAssociations(EbriefForm form)
	{
		List<PersonAssociation> paArray = new ArrayList<>();
		
		if (form.getDefendants() != null)
		{
			paArray = populatePersonAssociations(form.getId(), form.getDefendants(), paArray);
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
	
	private List<Person> getDefendants(List<PersonAssociation> pas)
	{
		List<Person> defendants = findPersonsByType(pas, EbriefConstants.DEFENDANT);
		
		if (defendants != null)
		{
			defendants = defendants.stream()
							       .map(element -> {
							    	   DefendantPerson defendant = new DefendantPerson(element);
							    	   defendant.setType(EbriefConstants.DEFENDANT);
							    	 
							    	   List<PersonIdentification> personIdentifications = element.getPersonIdentification();
							    	   defendant = (DefendantPerson) getPersonService().setPersonIdentifications(personIdentifications, defendant);
										
							    	   return defendant;
							       })
							       .collect(Collectors.toList());
		}
		
		return defendants;
	}
	
	// I WILL LEAVE THIS METHOD HERE FOR NOW. IF INVESTIGATION OFFICERS SHOULD BE A PERSON INSTEAD OF USER, THIS METHOD SHOULD BE USED
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
