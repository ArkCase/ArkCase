/**
 * 
 */
package com.armedia.acm.form.casefile.service;


import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.xml.InitiatorPerson;
import com.armedia.acm.plugins.person.model.xml.PeoplePerson;
import com.armedia.acm.service.history.dao.AcmHistoryDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory extends FrevvoFormFactory
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
		caseFile.setParticipants(getParticipants(caseFile.getParticipants(),form.getParticipants(), form.getOwningGroup(), caseFile.getObjectType()));

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
	
	public CaseFileForm asFrevvoCaseFile(CaseFile caseFile, CaseFileForm form, FrevvoFormAbstractService formService)
	{		
		if (form == null)
		{
			form = new CaseFileForm();
		}
		
		if (caseFile != null)
		{
			form.setId(caseFile.getId());
			form.setCaseTitle(caseFile.getTitle());
			form.setCaseType(caseFile.getCaseType());
			form.setCaseNumber(caseFile.getCaseNumber());
			form.setCaseDescription(caseFile.getDetails());
            String cmisFolderId = formService.findFolderId(caseFile.getContainer(), caseFile.getObjectType(), caseFile.getId());
            form.setCmisFolderId(cmisFolderId);
            form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
            form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));
			
			if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
			{
				InitiatorPerson initiator = new InitiatorPerson(caseFile.getOriginator().getPerson());
				initiator.setType(caseFile.getOriginator().getPersonType());
				
				form.setInitiator(initiator);
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
				
				form.setPeople(people);
			}
		}
		
		return form;
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
