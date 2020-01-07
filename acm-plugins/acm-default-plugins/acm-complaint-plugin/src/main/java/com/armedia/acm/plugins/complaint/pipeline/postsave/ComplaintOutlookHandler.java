package com.armedia.acm.plugins.complaint.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;

public class ComplaintOutlookHandler implements PipelineHandler<Complaint, ComplaintPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger logger = LogManager.getLogger(getClass());
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
        Optional<AcmOutlookUser> user = getConfiguredCalendarUser(pipelineContext);
        // if integration is not enabled the user will be null.
        if (!user.isPresent())
        {
            return;
        }

        // create calendar folder
        if (autoCreateFolderForComplaint && pipelineContext.isNewComplaint())
        {
            createOutlookFolder(user.get(), entity);
        }
        logger.info("Complaint entity post - autoCreateFolderForComplaint  ComplaintOutlookHandler : [{}]", entity);

        if (!pipelineContext.isNewComplaint() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            // update folder participants
            updateOutlookFolderParticipants(user.get(), entity);
        }
        logger.trace("Complaint exiting ComplaintOutlookHandler : [{}]", entity);
    }

    @Override
    public void rollback(Complaint entity, ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        logger.info("Delete created calendar folder for '{}'", entity.getComplaintNumber());
        Optional<AcmOutlookUser> user = getConfiguredCalendarUser(pipelineContext);
        // if integration is not enabled the user will be null.
        if (!user.isPresent())
        {
            return;
        }
        getOutlookContainerCalendarService().deleteFolder(user.get(), entity.getContainer(), DeleteMode.HardDelete);
    }

    /**
     * @param pipelineContext
     * @return
     * @throws PipelineProcessException
     */
    private Optional<AcmOutlookUser> getConfiguredCalendarUser(ComplaintPipelineContext pipelineContext) throws PipelineProcessException
    {
        return calendarAdminService.getHandlerOutlookUser(pipelineContext.getAuthentication().getName(), ComplaintConstants.OBJECT_TYPE);
    }

    private void createOutlookFolder(AcmOutlookUser outlookUser, Complaint complaint)
    {
        try
        {
            String folderName = String.format("%s(%s)", complaint.getComplaintTitle(), complaint.getComplaintNumber());
            outlookContainerCalendarService.createFolder(outlookUser, complaint.getId(), ComplaintConstants.OBJECT_TYPE, folderName,
                    complaint.getContainer(), complaint.getParticipants());
        }
        catch (AcmOutlookItemNotFoundException | AcmOutlookCreateItemFailedException e)
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
        }
        catch (AcmOutlookItemNotFoundException e)
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
