package com.armedia.acm.plugins.consultation.pipeline.postsave;

/*-
 * #%L
 * ACM Default Plugin: Consultation
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

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.model.ConsultationConstants;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import com.armedia.acm.service.outlook.service.OutlookCalendarAdminServiceExtension;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import microsoft.exchange.webservices.data.core.enumeration.service.DeleteMode;


/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 * Create Outlook folder and update participants for a Consultation.
 */
public class ConsultationOutlookHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Auto create folder for consultation flag.
     */
    private boolean autoCreateFolderForConsultation;
    /**
     * Outlook calendar service
     */
    private OutlookContainerCalendarService outlookContainerCalendarService;

    private OutlookCalendarAdminServiceExtension calendarAdminService;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("Consultation entering ConsultationOutlookHandler : [{}]", entity);
        Optional<AcmOutlookUser> user = getConfiguredCalendarUser(pipelineContext);
        // if integration is not enabled the user will be null.
        if (!user.isPresent())
        {
            return;
        }

        // create calendar folder
        if (autoCreateFolderForConsultation && pipelineContext.isNewConsultation())
        {
            createOutlookFolder(user.get(), entity);
        }
        log.info("Consultation entity post - autoCreateFolderForConsultation  ConsultationOutlookHandler : [{}]", entity);

        if (!pipelineContext.isNewConsultation() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            // update folder participants
            updateOutlookFolderParticipants(user.get(), entity);
        }
        log.trace("Consultation exiting ConsultationOutlookHandler : [{}]", entity);

    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.info("Delete created calendar folder for '{}'", entity.getConsultationNumber());
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
    private Optional<AcmOutlookUser> getConfiguredCalendarUser(ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        return calendarAdminService.getHandlerOutlookUser(pipelineContext.getAuthentication().getName(), ConsultationConstants.OBJECT_TYPE);
    }

    private void createOutlookFolder(AcmOutlookUser outlookUser, Consultation consultation)
    {
        try
        {
            outlookContainerCalendarService.createFolder(outlookUser, consultation.getId(), ConsultationConstants.OBJECT_TYPE,
                    consultation.getTitle() + "(" + consultation.getConsultationNumber() + ")", consultation.getContainer(), consultation.getParticipants());
        }
        catch (AcmOutlookItemNotFoundException | AcmOutlookCreateItemFailedException e)
        {
            log.error("Error creating calendar folder for '{}'", consultation.getConsultationNumber(), e);
        }
    }

    private void updateOutlookFolderParticipants(AcmOutlookUser outlookUser, Consultation consultation)
    {
        try
        {
            AcmContainer container = consultation.getContainer();
            outlookContainerCalendarService.updateFolderParticipants(outlookUser, container.getCalendarFolderId(),
                    consultation.getParticipants());
        }
        catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error updating participants for '{}'", consultation.getConsultationNumber(), e);
        }
    }

    public boolean isAutoCreateFolderForConsultation() {
        return autoCreateFolderForConsultation;
    }

    public void setAutoCreateFolderForConsultation(boolean autoCreateFolderForConsultation) {
        this.autoCreateFolderForConsultation = autoCreateFolderForConsultation;
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
