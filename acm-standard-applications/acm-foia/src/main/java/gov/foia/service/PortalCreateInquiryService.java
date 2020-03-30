package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.amazonaws.util.IOUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;
import com.armedia.acm.plugins.task.service.impl.ActivitiTaskDao;
import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIAInquiry;
import gov.foia.model.PortalFOIARequestFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PortalCreateInquiryService {
    private final Logger log = LogManager.getLogger(getClass());
    private TaskDao taskDao;
    private PersonAssociationDao personAssociationDao;
    private PersonDao personDao;
    private CaseFileDao caseFileDao;
    private EcmFileService ecmFileService;
    private PortalCreateRequestService portalCreateRequestService;
    private TaskEventPublisher taskEventPublisher;

    public void createFOIAInquiry(PortalFOIAInquiry inquiry)
    {
        FOIARequesterAssociation personAssociation = new FOIARequesterAssociation();
        Authentication auth = new UsernamePasswordAuthenticationToken(inquiry.getUserId(), "");

        try {
            AcmTask saved = getTaskDao().createAdHocTask(populateTask(inquiry));
            populatePersonAssociation(personAssociation,  inquiry, saved);
            getPersonAssociationDao().save(personAssociation);

            AcmFolder folder = saved.getContainer().getAttachmentFolder();
            if(!inquiry.getDocuments().isEmpty())
            {
                for (PortalFOIARequestFile document : inquiry.getDocuments())
                {
                    MultipartFile file = getPortalCreateRequestService().portalRequestFileToMultipartFile(document);
                    ecmFileService.upload(file.getOriginalFilename(), "Other", file, auth,
                            folder.getCmisFolderId(),
                            saved.getObjectType(),
                            saved.getTaskId());
                }
            }
            AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(saved, "create", inquiry.getUserId(), true, null);
            getTaskEventPublisher().publishTaskEvent(event);

        } catch (AcmCreateObjectFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AcmUserActionFailedException e) {
            e.printStackTrace();
        }
    }

    private AcmTask populateTask(PortalFOIAInquiry in)
    {
        AcmTask task = new AcmTask();
        FOIARequest request = (FOIARequest) getCaseFileDao().findByCaseNumber(in.getParentId());
        task.setStatus(TaskConstants.STATE_ACTIVE);
        task.setOwner(in.getUserId());
        task.setTitle(in.getSubject());
        task.setDetails(in.getDescription());
        task.setType("web-portal-inquiry");
        task.setAdhocTask(true);

        if(request != null)
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
    private FOIARequesterAssociation populatePersonAssociation(FOIARequesterAssociation pa, PortalFOIAInquiry in, AcmTask task)
    {
        pa.setParentId(task.getTaskId());
        pa.setParentType("TASK");
        pa.setPersonType("Creator");
        pa.setCreator(in.getUserId());
        List<Person> personList = getPersonDao().findByNameOrContactValue("", in.getEmailAddress());
        FOIAPerson person;
        if(personList.isEmpty())
        {
            person = new FOIAPerson();
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
            person = (FOIAPerson) personList.get(0);
        }

        pa.setPerson(person);
        pa.setPersonType("Creator");
        return pa;
    }
    public MultipartFile convertPortalRequestFileToMultipartFile(PortalFOIARequestFile requestFile) throws IOException
    {
        byte[] content = Base64.getDecoder().decode(requestFile.getContent());

        File file = new File(requestFile.getFileName());
        Path path = Paths.get(file.getAbsolutePath());
        Files.write(path, content);

        FileItem fileItem = new DiskFileItem("", requestFile.getContentType(), false, file.getName(), (int) file.length(),
                file.getParentFile());

        try (InputStream input = new FileInputStream(file))
        {
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
        }

        return new CommonsMultipartFile(fileItem);
    }


    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }

    public EcmFileService getEcmFileService() {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }

    public PersonAssociationDao getPersonAssociationDao() {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao) {
        this.personAssociationDao = personAssociationDao;
    }

    public PersonDao getPersonDao() {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    public PortalCreateRequestService getPortalCreateRequestService()
    {
        return portalCreateRequestService;
    }

    public void setPortalCreateRequestService(PortalCreateRequestService portalCreateRequestService)
    {
        this.portalCreateRequestService = portalCreateRequestService;
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
