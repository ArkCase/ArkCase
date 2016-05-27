package com.armedia.acm.services.notification.service;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ncuculova
 */
public class ObjectNameTitleFormatter implements CustomTitleFormatter
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private TaskDao taskDao;
    private CaseFileDao caseFileDao;
    private ComplaintDao complaintDao;

    @Override
    public String format(Notification notification)
    {
        String parentObjectType = notification.getRelatedObjectType() != null ?
                notification.getRelatedObjectType() : notification.getParentType();
        Long parentObjectId = notification.getRelatedObjectId() != null ?
                notification.getRelatedObjectId() : notification.getParentId();
        String title = notification.getTitle();
        String titleFormatted = null;

        if (parentObjectType.equals(TaskConstants.OBJECT_TYPE))
        {
            try
            {
                AcmTask task = getTaskDao().findById(parentObjectId);
                titleFormatted = replacePlaceholder(task.getTitle(), title, NotificationConstants.NAME_LABEL);
            } catch (AcmTaskException e)
            {
                logger.warn("Task not found", e);
            }
        } else if (parentObjectType.equals(CaseFileConstants.OBJECT_TYPE))
        {
            CaseFile caseFile = getCaseFileDao().find(parentObjectId);
            if (caseFile != null)
            {
                titleFormatted = replacePlaceholder(caseFile.getCaseNumber(), title, NotificationConstants.NAME_LABEL);
            }
        } else if (parentObjectType.equals(ComplaintConstants.OBJECT_TYPE))
        {
            Complaint complaint = getComplaintDao().find(parentObjectId);
            if (complaint != null)
            {
                titleFormatted = replacePlaceholder(complaint.getComplaintNumber(), title, NotificationConstants.NAME_LABEL);
            }
        }
        return titleFormatted;
    }


    public String replacePlaceholder (String objectName, String titlePlaceholder, String placeholder){
        return titlePlaceholder.replace(placeholder, objectName);
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public ComplaintDao getComplaintDao()
    {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao)
    {
        this.complaintDao = complaintDao;
    }
}
