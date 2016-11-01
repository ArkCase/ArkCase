/**
 *
 */
package com.armedia.acm.form.ebrief.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.form.casefile.service.CaseFileWorkflowListener;
import com.armedia.acm.form.ebrief.model.EbriefForm;
import com.armedia.acm.form.ebrief.model.xml.EbriefDetails;
import com.armedia.acm.form.ebrief.model.xml.EbriefInformation;
import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;
import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.ecm.service.impl.FileWorkflowBusinessRule;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.ApplicationNotificationEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.notification.service.NotificationEventPublisher;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.activiti.engine.RuntimeService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;

/**
 * @author riste.tutureski
 */
public class EbriefService extends FrevvoFormAbstractService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

    private EbriefFactory ebriefFactory;
    private SaveCaseService saveCaseService;
    private CaseFileDao caseFileDao;
    private FileWorkflowBusinessRule fileWorkflowBusinessRule;
    private RuntimeService activitiRuntimeService;
    private CaseFile caseFile;
    private NotificationDao notificationDao;
    private NotificationEventPublisher notificationEventPublisher;

    @Override
    public Object get(String action)
    {
        Object result = null;

        if (action != null)
        {
            if ("init-form-data".equals(action))
            {
                result = initFormData();
            }

            if ("init-location-data".equals(action))
            {
                result = initLocationData();
            }
        }

        return result;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        // Convert XML to Object
        EbriefForm form = (EbriefForm) convertFromXMLToObject(cleanXML(xml), EbriefForm.class);

        if (form == null)
        {
            LOG.warn("Cannot unmarshall eBrief Form.");
            return false;
        }

        // Save eBrief to the database
        form = saveEBrief(form);

        updateXMLAttachment(attachments, FrevvoFormName.EBRIEF, form);

        // Change PDF file name
        attachments = updateFileName(getCaseFile().getTitle(), FrevvoFormConstants.PDF, attachments);

        // Save Attachments
        FrevvoUploadedFiles frevvoFiles = saveAttachments(
                attachments,
                form.getCmisFolderId(),
                FrevvoFormName.CASE_FILE.toUpperCase(),
                form.getId());

        String mode = getRequest().getParameter("mode");
        if (!"edit".equals(mode))
        {
            CaseFileWorkflowListener workflowListener = new CaseFileWorkflowListener();
            workflowListener.handleNewCaseFile(
                    getCaseFile(),
                    frevvoFiles,
                    getActivitiRuntimeService(),
                    getFileWorkflowBusinessRule(),
                    this);
        }

        return true;
    }

    private EbriefForm saveEBrief(EbriefForm form) throws AcmCreateObjectFailedException
    {
        LOG.info("Saving eBrief ...");

        CaseFile caseFile = null;

        // Edit mode
        String mode = getRequest().getParameter("mode");
        if (mode != null && "edit".equals(mode) && form.getId() != null)
        {
            caseFile = getCaseFileDao().find(form.getId());
        }

        caseFile = getEbriefFactory().asAcmCaseFile(form, caseFile);

        // Save Case file
        try
        {
            caseFile = getSaveCaseService().saveCase(caseFile, getAuthentication(), getUserIpAddress());
        } catch (PipelineProcessException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("eBrief", e.getMessage(), e);
        }

        if (mode == null || "".equals(mode) || "create".equals(mode))
        {
            createNotification(caseFile);
        }

        setCaseFile(caseFile);

        form = getEbriefFactory().asFrevvoEbriefForm(caseFile, form, this);

        return form;
    }

    private Object initFormData()
    {
        EbriefInformation information = new EbriefInformation();

        information.setTypes(convertToList((String) getProperties().get(FrevvoFormName.EBRIEF + ".types"), ","));

        JSONObject json = createResponse(information);

        return json;
    }

    private Object initLocationData()
    {
        EbriefDetails details = new EbriefDetails();

        details.setCourtLocations(convertToList((String) getProperties().get(FrevvoFormName.EBRIEF + ".court.locations"), ","));

        JSONObject json = createResponse(details);

        return json;
    }

    @Override
    public Object convertToFrevvoForm(Object obj, Object form)
    {
        return getEbriefFactory().asFrevvoEbriefForm((CaseFile) obj, (EbriefForm) form, this);
    }

    private void createNotification(CaseFile caseFile)
    {
        try
        {
            Notification notification = new Notification();

            notification.setStatus(NotificationConstants.STATUS_NEW);
            notification.setTitle(caseFile.getTitle());
            notification.setNote(caseFile.getTitle() + " was submitted.");
            notification.setData("{\"usr\":\"/plugin/casefile/" + caseFile.getId() + "\"}");
            notification.setUser(caseFile.getCreator());
            notification.setParentId(caseFile.getId());
            notification.setParentType(caseFile.getObjectType());
            notification.setParentName(caseFile.getCaseNumber());
            notification.setParentTitle(caseFile.getTitle());
            notification.setType(NotificationConstants.TYPE_POPUP);

            getNotificationDao().save(notification);

            ApplicationNotificationEvent event = new ApplicationNotificationEvent(notification, "notification", true, getUserIpAddress());
            getNotificationEventPublisher().publishNotificationEvent(event);
        } catch (Exception e)
        {
            LOG.error("Cannot publish notification ... ", e);
        }
    }

    @Override
    public String getFormName()
    {
        return FrevvoFormName.EBRIEF;
    }

    @Override
    public Class<?> getFormClass()
    {
        return EbriefForm.class;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }


    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public EbriefFactory getEbriefFactory()
    {
        return ebriefFactory;
    }

    public void setEbriefFactory(EbriefFactory ebriefFactory)
    {
        this.ebriefFactory = ebriefFactory;
    }

    public FileWorkflowBusinessRule getFileWorkflowBusinessRule()
    {
        return fileWorkflowBusinessRule;
    }

    public void setFileWorkflowBusinessRule(
            FileWorkflowBusinessRule fileWorkflowBusinessRule)
    {
        this.fileWorkflowBusinessRule = fileWorkflowBusinessRule;
    }

    public RuntimeService getActivitiRuntimeService()
    {
        return activitiRuntimeService;
    }

    public void setActivitiRuntimeService(RuntimeService activitiRuntimeService)
    {
        this.activitiRuntimeService = activitiRuntimeService;
    }

    public CaseFile getCaseFile()
    {
        return caseFile;
    }

    public void setCaseFile(CaseFile caseFile)
    {
        this.caseFile = caseFile;
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public NotificationEventPublisher getNotificationEventPublisher()
    {
        return notificationEventPublisher;
    }

    public void setNotificationEventPublisher(
            NotificationEventPublisher notificationEventPublisher)
    {
        this.notificationEventPublisher = notificationEventPublisher;
    }
}
