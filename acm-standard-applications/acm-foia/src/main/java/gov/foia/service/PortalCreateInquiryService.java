package gov.foia.service;

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
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

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

import gov.foia.model.FOIAPerson;
import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequesterAssociation;
import gov.foia.model.PortalFOIAInquiry;
import gov.foia.model.PortalFOIARequestFile;

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

    public void createFOIAInquiry(PortalFOIAInquiry inquiry)
    {
        FOIARequesterAssociation personAssociation = new FOIARequesterAssociation();
        Authentication auth = new UsernamePasswordAuthenticationToken(inquiry.getUserId(), "");

        try
        {
            AcmTask saved = getTaskDao().createAdHocTask(populateTask(inquiry));
            populatePersonAssociation(personAssociation, inquiry, saved);
            getPersonAssociationDao().save(personAssociation);

            AcmFolder folder = saved.getContainer().getAttachmentFolder();
            if (!inquiry.getDocuments().isEmpty())
            {
                for (PortalFOIARequestFile document : inquiry.getDocuments())
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

    private FOIARequesterAssociation populatePersonAssociation(FOIARequesterAssociation pa, PortalFOIAInquiry in, AcmTask task)
    {
        pa.setParentId(task.getTaskId());
        pa.setParentType("TASK");
        pa.setPersonType("Creator");
        pa.setCreator(in.getUserId());
        List<Person> personList = getPersonDao().findByNameOrContactValue("", in.getEmailAddress());
        FOIAPerson person;
        if (personList.isEmpty())
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
