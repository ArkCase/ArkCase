/**
 * 
 */
package com.armedia.acm.form.casefile.service;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.form.config.xml.ParticipantItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.participants.model.AcmParticipant;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory 
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	public static final String PERSON_TYPE = "Subject";
	public static final String PERSON_IDENTIFICATION_EMPLOYEE_ID = "EMPLOYEE_ID";
	public static final String PERSON_IDENTIFICATION_SSN = "SSN";
	public static final String OBJECT_TYPE_POSTAL_ADDRESS = "POSTAL_ADDRESS";
	public static final String OBJECT_TYPE_ORGANIZATION = "ORGANIZATION";
	
	private ObjectAssociationDao objectAssociationDao;
	private EcmFileDao ecmFileDao;
	private AcmHistoryDao acmHistoryDao;
	private EcmFileService ecmFileService;

	public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
	{
		if (caseFile == null)
		{
			caseFile = new CaseFile();
		}
		
		caseFile.setTitle(form.getCaseTitle());
		caseFile.setCaseType(form.getCaseType());
		caseFile.setDetails(form.getCaseDescription());
		caseFile.setParticipants(asAcmParticipants(form.getParticipants()));

		if (form.getInitiator() != null)
		{
			PersonAssociation pa = new PersonAssociation();
			pa.setPerson(form.getInitiator().returnBase());
			pa.setPersonType(((InitiatorPerson) form.getInitiator()).getType());
			
			caseFile.setOriginator(pa);
		}
		
		if (form.getPeople() != null)
		{
			List<PersonAssociation> paArray = new ArrayList<>();
			for (Person person : form.getPeople())
			{
				PersonAssociation pa = new PersonAssociation();
				pa.setPerson(person.returnBase());
				pa.setPersonType(((PeoplePerson) person).getType());
				
				paArray.add(pa);
			}
			
			caseFile.setPersonAssociations(paArray);
		}

		
		return caseFile;
	}
	
	private List<AcmParticipant> asAcmParticipants(List<ParticipantItem> items)
	{
		if (items != null)
		{
			List<AcmParticipant> participants = new ArrayList<>();
			for (ParticipantItem item : items)
			{
				AcmParticipant participant = new AcmParticipant();
				
				participant.setId(item.getId());
				participant.setObjectType(FrevvoFormName.CASE_FILE.toUpperCase());
				participant.setParticipantLdapId(item.getValue());
				participant.setParticipantType(item.getType());
				
				participants.add(participant);
			}
			
			return participants;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public CaseFileForm asFrevvoCaseFile(CaseFile caseFile, CaseFileForm form)
	{		
		CaseFileForm retval = new CaseFileForm();
		
		if (caseFile != null)
		{
			retval.setId(caseFile.getId());
			retval.setCaseTitle(caseFile.getTitle());
			retval.setCaseType(caseFile.getCaseType());
			retval.setCaseNumber(caseFile.getCaseNumber());
			retval.setCaseDescription(caseFile.getDetails());
			retval.setCmisFolderId(caseFile.getEcmFolderId());
			retval.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
			
			if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
			{
				InitiatorPerson initiator = new InitiatorPerson(caseFile.getOriginator().getPerson());
				initiator.setType(caseFile.getOriginator().getPersonType());
				
				retval.setInitiator(initiator);
			}
			
			if (caseFile.getPersonAssociations() != null)
			{
				List<Person> people = new ArrayList<>();
				for (PersonAssociation pa : caseFile.getPersonAssociations())
				{
					if (pa.getPerson() != null)
					{
						PeoplePerson peoplePerson = new PeoplePerson(pa.getPerson());
						peoplePerson.setType(pa.getPersonType());
						
						people.add(peoplePerson);
					}
				}
				
				retval.setPeople(people);
			}
		}
		
		return retval;
	}
	
	private List<ParticipantItem> asFrevvoParticipants(List<AcmParticipant> participants)
	{
		if (participants != null)
		{
			List<ParticipantItem> items = new ArrayList<>();
			
			for (AcmParticipant participant : participants)
			{
				if (!CaseFileFormConstants.DEFAULT_USER.equals(participant.getParticipantType()))
				{
					ParticipantItem item = new ParticipantItem();
					
					item.setId(participant.getId());
					item.setType(participant.getParticipantType());
					item.setValue(participant.getParticipantLdapId());
					
					items.add(item);
				}
			}
			
			return items;
		}
		
		return null;
	}

	public ObjectAssociationDao getObjectAssociationDao() {
		return objectAssociationDao;
	}

	public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao) {
		this.objectAssociationDao = objectAssociationDao;
	}

	public EcmFileDao getEcmFileDao() {
		return ecmFileDao;
	}

	public void setEcmFileDao(EcmFileDao ecmFileDao) {
		this.ecmFileDao = ecmFileDao;
	}

	public AcmHistoryDao getAcmHistoryDao() {
		return acmHistoryDao;
	}

	public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao) {
		this.acmHistoryDao = acmHistoryDao;
	}

	public EcmFileService getEcmFileService() {
		return ecmFileService;
	}

	public void setEcmFileService(EcmFileService ecmFileService) {
		this.ecmFileService = ecmFileService;
	}
	
}
