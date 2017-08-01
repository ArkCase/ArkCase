package com.armedia.acm.plugins.complaint.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.complaint.model.ComplaintConstants;
import com.armedia.acm.plugins.complaint.pipeline.ComplaintPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class ComplaintOutlookHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * Auto create folder for complaint flag.
     */
    private boolean autoCreateFolderForComplaint;
    /**
     * Outlook calendar service
     */
    private OutlookContainerCalendarService outlookContainerCalendarService;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    @Override
    public void execute(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        logger.trace("Complaint entering ComplaintOutlookHandler : [{}]", entity);
        AcmOutlookUser user = getConfiguredCalendarUser(pipelineContext);

        // create calendar folder
        if (autoCreateFolderForComplaint && pipelineContext.isNewComplaint())
        {
            createOutlookFolder(user, entity);
        }
        logger.info("Complaint entity post - autoCreateFolderForComplaint  ComplaintOutlookHandler : [{}]", entity);

        if (!pipelineContext.isNewComplaint() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            // update folder participants
            updateOutlookFolderParticipants(user, entity);
        }
        logger.trace("Complaint exiting ComplaintOutlookHandler : [{}]", entity);
    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        logger.info("Delete created calendar folder for '{}'", entity.getComplaintNumber());
        AcmOutlookUser user = getConfiguredCalendarUser(pipelineContext);
        getOutlookContainerCalendarService().deleteFolder(user, entity.getContainer().getContainerObjectId(),
                entity.getContainer().getCalendarFolderId(), DeleteMode.HardDelete);
    }

    /**
     * @param pipelineContext
     * @return
     * @throws PipelineProcessException
     */
    private AcmOutlookUser getConfiguredCalendarUser(ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        return calendarAdminService.getHandlerOutlookUser(pipelineContext.getAuthentication().getName(), ComplaintConstants.OBJECT_TYPE);
    }

    private void createOutlookFolder(AcmOutlookUser outlookUser, Complaint complaint)
    {
        try
        {
            String folderName = String.format("%s(%s)", complaint.getComplaintTitle(), complaint.getComplaintNumber());
            outlookContainerCalendarService.createFolder(outlookUser, folderName, complaint.getContainer(), complaint.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            logger.error("Error creating calendar folder for '{}'", complaint.getComplaintNumber(), e);
        } catch (AcmOutlookCreateItemFailedException e)
        {
            logger.error("Error creating calendar folder for '{}'", complaint.getComplaintNumber(), e);
        }
    }

    private void updateOutlookFolderParticipants(AcmOutlookUser outlookUser, Complaint complaint)
    {
        try
        {
            AcmContainer container = complaint.getContainer();
            outlookContainerCalendarService.updateFolderParticipants(outlookUser, container.getCalendarFolderId(),
                    complaint.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            logger.error("Error updating participants for '{}'", complaint.getComplaintNumber(), e);
        }
    }

    public boolean isAutoCreateFolderForComplaint()
    {
        return autoCreateFolderForComplaint;
    }

    public void setAutoCreateFolderForComplaint(boolean autoCreateFolderForComplaint)
    {
        this.autoCreateFolderForComplaint = autoCreateFolderForComplaint;
    }

    public OutlookContainerCalendarService getOutlookContainerCalendarService()
    {
        return outlookContainerCalendarService;
    }

    public void setOutlookContainerCalendarService(OutlookContainerCalendarService outlookContainerCalendarService)
    {
        this.outlookContainerCalendarService = outlookContainerCalendarService;
    }

    /**
     * @param calendarAdminService
     *            the calendarAdminService to set
     */
    public void setCalendarAdminService(OutlookCalendarAdminServiceExtension calendarAdminService)
    {
        this.calendarAdminService = calendarAdminService;
    }
}
