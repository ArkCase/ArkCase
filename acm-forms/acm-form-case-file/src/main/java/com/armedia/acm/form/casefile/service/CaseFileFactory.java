/**
 * 
 */
package com.armedia.acm.form.casefile.service;

import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.form.config.xml.PeopleItem;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormFactory;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.objectassociation.dao.ObjectAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.service.history.dao.AcmHistoryDao;
import com.armedia.acm.services.participants.model.AcmParticipant;
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
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private EcmFileService ecmFileService;

    public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
    {
        if (caseFile == null)
        {
            caseFile = new CaseFile();
        }

        buildGeneralInformation(form, caseFile);
        buildInitiator(form, caseFile);
        buildPeople(form, caseFile);
        buildParticipants(form, caseFile);

        return caseFile;
    }

    private void buildGeneralInformation(CaseFileForm form, CaseFile caseFile)
    {
        caseFile.setTitle(form.getCaseTitle());
        caseFile.setCaseType(form.getCaseType());
        caseFile.setDetails(form.getCaseDescription());
    }

    private void buildInitiator(CaseFileForm form, CaseFile caseFile)
    {
        if (form.getInitiatorId() != null)
        {
            PersonAssociation personAssociation = caseFile.getOriginator();
            if (personAssociation == null)
            {
                personAssociation = new PersonAssociation();
            }
            Person person = getPersonDao().find(form.getInitiatorId());
            if (person != null)
            {
                // Update Person Association
                personAssociation.setPerson(person);
                personAssociation.setPersonType(form.getPersonType());

            }
            caseFile.setOriginator(personAssociation);
        }
    }

    private void buildPeople(CaseFileForm form, CaseFile caseFile)
    {
        if (form.getPeople() != null && form.getPeople().size() > 0)
        {
            List<PersonAssociation> paArray = new ArrayList<>();

            PersonAssociation initiatorPersonAssociation = caseFile.getPersonAssociations()
                    .stream()
                    .filter(personAssociation -> personAssociation.getPersonType().equals(CaseFileFormConstants.PERSON_TYPE_INITIATOR))
                    .findFirst()
                    .orElse(null);

            if(initiatorPersonAssociation != null)
                paArray.add(initiatorPersonAssociation);

            for (PeopleItem peopleItem : form.getPeople())
            {
                Person person = getPersonDao().find(peopleItem.getId());
                PersonAssociation personAssociation = (peopleItem.getPersonAssociationId() == null) ? new PersonAssociation() : getPersonAssociationDao().find(peopleItem.getPersonAssociationId());

                if(person == null)
                    continue;

                personAssociation.setPerson(person);
                personAssociation.setPersonType(peopleItem.getPersonType());
                paArray.add(personAssociation);
            }
            caseFile.setPersonAssociations(paArray);
        }
    }

    private void buildParticipants(CaseFileForm form, CaseFile caseFile)
    {
        List<AcmParticipant> participants = getParticipants(caseFile.getParticipants(), form.getParticipants(), form.getOwningGroup(),
                caseFile.getObjectType());
        caseFile.setParticipants(participants);
    }

    public CaseFileForm asFrevvoCaseFile(CaseFile caseFile, CaseFileForm form, FrevvoFormAbstractService formService)
    {
        try
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
                String cmisFolderId = formService.findFolderIdForAttachments(caseFile.getContainer(), caseFile.getObjectType(),
                        caseFile.getId());
                form.setCmisFolderId(cmisFolderId);
                form.setParticipants(asFrevvoParticipants(caseFile.getParticipants()));
                form.setOwningGroup(asFrevvoGroupParticipant(caseFile.getParticipants()));

                if (caseFile.getOriginator() != null && caseFile.getOriginator().getPerson() != null)
                {
                    form.setInitiatorId(caseFile.getOriginator().getPerson().getId());
                    form.setInitiatorFullName(caseFile.getOriginator().getPerson().getFullName());
                    form.setPersonType(caseFile.getOriginator().getPersonType());
                }

                if (caseFile.getPersonAssociations() != null)
                {
                    List<PeopleItem> people = new ArrayList<>();
                    for (PersonAssociation pa : caseFile.getPersonAssociations())
                    {
                        if (pa.getPerson() != null && !pa.getPersonType().equals("Initiator"))
                        {
                            PeopleItem peopleItem = new PeopleItem();

                            peopleItem.setId(pa.getPerson().getId());
                            peopleItem.setValue(pa.getPerson().getFullName());
                            peopleItem.setPersonType(pa.getPersonType());
                            peopleItem.setPersonAssociationId(pa.getId());

                            people.add(peopleItem);
                        }
                    }
                    form.setPeople(people);
                }
            }
        } catch (Exception e)
        {
            LOG.error("Cannot convert Object to Frevvo form.", e);
        }

        return form;
    }

    public ObjectAssociationDao getObjectAssociationDao()
    {
        return objectAssociationDao;
    }

    public void setObjectAssociationDao(ObjectAssociationDao objectAssociationDao)
    {
        this.objectAssociationDao = objectAssociationDao;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public AcmHistoryDao getAcmHistoryDao()
    {
        return acmHistoryDao;
    }

    public void setAcmHistoryDao(AcmHistoryDao acmHistoryDao)
    {
        this.acmHistoryDao = acmHistoryDao;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

}
