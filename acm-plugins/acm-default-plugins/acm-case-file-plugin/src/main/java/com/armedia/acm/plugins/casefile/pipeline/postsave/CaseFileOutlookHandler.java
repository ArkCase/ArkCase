package com.armedia.acm.plugins.casefile.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
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

/**
 * Create Outlook folder and update participants for a Case File. Created by Petar Ilin <petar.ilin@armedia.com> on
 * 11.08.2015.
 */
public class CaseFileOutlookHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Auto create folder for case file flag.
     */
    private boolean autoCreateFolderForCaseFile;
    /**
     * Outlook calendar service
     */
    private OutlookContainerCalendarService outlookContainerCalendarService;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("CaseFile entrering CaseFileOutlookHandler : [{}]", entity);
        Optional<AcmOutlookUser> user = getConfiguredCalendarUser(pipelineContext);
        // if integration is not enabled the user will be null.
        if (!user.isPresent())
        {
            return;
        }

        // create calendar folder
        if (autoCreateFolderForCaseFile && pipelineContext.isNewCase())
        {
            createOutlookFolder(user.get(), entity);
        }
        log.info("CaseFile entity post - autoCreateFolderForCaseFile  CaseFileOutlookHandler : [{}]", entity);

        if (!pipelineContext.isNewCase() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            // update folder participants
            updateOutlookFolderParticipants(user.get(), entity);
        }
        log.trace("CaseFile exiting CaseFileOutlookHandler : [{}]", entity);

    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Delete created calendar folder for '{}'", entity.getCaseNumber());
        Optional<AcmOutlookUser> user = getConfiguredCalendarUser(pipelineContext);
        // if integration is not enabled the user will be null.
        if (!user.isPresent())
        {
            return;
        }
        getOutlookContainerCalendarService().deleteFolder(user.get(), entity.getContainer(), DeleteMode.MoveToDeletedItems);
    }

    /**
     * @param pipelineContext
     * @return
     * @throws PipelineProcessException
     */
    private Optional<AcmOutlookUser> getConfiguredCalendarUser(CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        return calendarAdminService.getHandlerOutlookUser(pipelineContext.getAuthentication().getName(), CaseFileConstants.OBJECT_TYPE);
    }

    private void createOutlookFolder(AcmOutlookUser outlookUser, CaseFile caseFile)
    {
        try
        {
            outlookContainerCalendarService.createFolder(outlookUser, caseFile.getId(), CaseFileConstants.OBJECT_TYPE,
                    caseFile.getTitle() + "(" + caseFile.getCaseNumber() + ")", caseFile.getContainer(), caseFile.getParticipants());
        }
        catch (AcmOutlookItemNotFoundException | AcmOutlookCreateItemFailedException e)
        {
            log.error("Error creating calendar folder for '{}'", caseFile.getCaseNumber(), e);
        }
    }

    private void updateOutlookFolderParticipants(AcmOutlookUser outlookUser, CaseFile caseFile)
    {
        try
        {
            AcmContainer container = caseFile.getContainer();
            outlookContainerCalendarService.updateFolderParticipants(outlookUser, container.getCalendarFolderId(),
                    caseFile.getParticipants());
        }
        catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error updating participants for '{}'", caseFile.getCaseNumber(), e);
        }
    }

    public boolean isAutoCreateFolderForCaseFile()
    {
        return autoCreateFolderForCaseFile;
    }

    public void setAutoCreateFolderForCaseFile(boolean autoCreateFolderForCaseFile)
    {
        this.autoCreateFolderForCaseFile = autoCreateFolderForCaseFile;
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
