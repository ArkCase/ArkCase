/**
 * 
 */
package com.armedia.acm.form.casefile.service;

/*-
 * #%L
 * ACM Forms: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.form.casefile.model.CaseFileForm;
import com.armedia.acm.form.casefile.model.CaseFileFormConstants;
import com.armedia.acm.form.config.xml.PersonItem;
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class CaseFileFactory extends FrevvoFormFactory
{
    public static final String PERSON_TYPE = "Subject";
    public static final String PERSON_IDENTIFICATION_EMPLOYEE_ID = "EMPLOYEE_ID";
    public static final String PERSON_IDENTIFICATION_SSN = "SSN";
    public static final String OBJECT_TYPE_POSTAL_ADDRESS = "POSTAL_ADDRESS";
    public static final String OBJECT_TYPE_ORGANIZATION = "ORGANIZATION";
    private final Logger LOG = LogManager.getLogger(getClass());
    private ObjectAssociationDao objectAssociationDao;
    private EcmFileDao ecmFileDao;
    private AcmHistoryDao acmHistoryDao;
    private PersonDao personDao;
    private PersonAssociationDao personAssociationDao;
    private EcmFileService ecmFileService;

    public CaseFile makeCaseFile()
    {
        return new CaseFile();
    }

    public CaseFile asAcmCaseFile(CaseFileForm form, CaseFile caseFile)
    {
        if (caseFile == null)
        {
            caseFile = makeCaseFile();
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
                personAssociation.setPersonType(form.getInitiatorType());

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

            if (initiatorPersonAssociation != null)
                paArray.add(initiatorPersonAssociation);

            for (PersonItem personItem : form.getPeople())
            {
                Person person = getPersonDao().find(personItem.getId());
                PersonAssociation personAssociation = (personItem.getPersonAssociationId() == null) ? new PersonAssociation()
                        : getPersonAssociationDao().find(
                                personItem.getPersonAssociationId());

                if (person == null)
                    continue;

                personAssociation.setPerson(person);
                personAssociation.setPersonType(personItem.getPersonType());
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
                    form.setInitiatorType(caseFile.getOriginator().getPersonType());
                }

                if (caseFile.getPersonAssociations() != null)
                {
                    List<PersonItem> people = new ArrayList<>();
                    for (PersonAssociation pa : caseFile.getPersonAssociations())
                    {
                        if (pa.getPerson() != null && !pa.getPersonType().equals("Initiator"))
                        {
                            PersonItem personItem = new PersonItem();

                            personItem.setId(pa.getPerson().getId());
                            personItem.setValue(pa.getPerson().getFullName());
                            personItem.setPersonType(pa.getPersonType());
                            personItem.setPersonAssociationId(pa.getId());

                            people.add(personItem);
                        }
                    }
                    form.setPeople(people);
                }
            }
        }
        catch (Exception e)
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
