package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.privacy.model.PortalSARFile;
import gov.privacy.model.PortalSARInquiry;
import gov.privacy.model.SARPerson;
import gov.privacy.model.SARPersonAssociation;
import gov.privacy.model.SubjectAccessRequest;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class PortalCreateInquiryService
{
    private final Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;
    private PersonAssociationDao personAssociationDao;
    private PersonDao personDao;
    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private PortalRequestService portalRequestService;
    private TaskEventPublisher taskEventPublisher;

    public void createSARInquiry(PortalSARInquiry inquiry)
    {
        SARPersonAssociation personAssociation = new SARPersonAssociation();
        Authentication auth = new UsernamePasswordAuthenticationToken(inquiry.getUserId(), "");

        try
        {
            AcmTask saved = getTaskDao().createAdHocTask(populateTask(inquiry));
            populatePersonAssociation(personAssociation, inquiry, saved);
            getPersonAssociationDao().save(personAssociation);

            AcmFolder folder = saved.getContainer().getAttachmentFolder();
            if (!inquiry.getDocuments().isEmpty())
            {
                for (PortalSARFile document : inquiry.getDocuments())
                {
                    MultipartFile file = getPortalRequestService().convertPortalRequestFileToMultipartFile(document);
                    ecmFileService.upload(file.getOriginalFilename(), "Other", file, auth,
                            folder.getCmisFolderId(),
                            saved.getObjectType(),
                            saved.getTaskId());
                }
            }
            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(saved, "create", inquiry.getUserId(), true, null);
            getTaskEventPublisher().publishTaskEvent(event);

        }
        catch (AcmCreateObjectFailedException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (AcmUserActionFailedException e)
        {
            e.printStackTrace();
        }
    }

    private AcmTask populateTask(PortalSARInquiry in)
    {
        AcmTask task = new AcmTask();
        SubjectAccessRequest request = (SubjectAccessRequest) getCaseFileDao().findByCaseNumber(in.getParentId());
        task.setStatus(TaskConstants.STATE_ACTIVE);
        task.setOwner(in.getUserId());
        task.setTitle(in.getSubject());
        task.setDetails(in.getDescription());
        task.setType("web-portal-inquiry");
        task.setAdhocTask(true);

        if (request != null)
        {
            task.setAttachedToObjectId(request.getId());
            task.setAttachedToObjectType(request.getObjectType());
            task.setAttachedToObjectName(request.getCaseNumber());
            task.setParentObjectId(request.getId());
            task.setParentObjectType(request.getObjectType());
            task.setParentObjectName(request.getCaseNumber());
        }
        else
        {
            task.setAttachedToObjectId(null);
            task.setAttachedToObjectType(null);
            task.setAttachedToObjectName(null);
        }
        return task;
    }

    private SARPersonAssociation populatePersonAssociation(SARPersonAssociation pa, PortalSARInquiry in, AcmTask task)
    {
        pa.setParentId(task.getTaskId());
        pa.setParentType("TASK");
        pa.setPersonType("Inquirer");
        pa.setCreator(in.getUserId());
        List<Person> personList = getPersonDao().findByNameOrContactValue("", in.getEmailAddress());
        SARPerson person;
        if (personList.isEmpty())
        {
            person = new SARPerson();
            person.setGivenName(in.getFirstName());
            person.setFamilyName(in.getLastName());
            List<ContactMethod> contactMethods = new ArrayList<>();
            ContactMethod contactMethod = new ContactMethod();
            contactMethod.setType("email");
            contactMethod.setValue(in.getEmailAddress());
            contactMethods.add(contactMethod);
            person.setContactMethods(contactMethods);
        }
        else
        {
            person = (SARPerson) personList.get(0);
        }

        pa.setPerson(person);
        pa.setPersonType("Creator");
        return pa;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public PortalRequestService getPortalRequestService()
    {
        return portalRequestService;
    }

    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }
}
