package com.armedia.acm.services.timesheet.pipeline.postsave;
/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;
import com.armedia.acm.services.timesheet.service.TimesheetEventPublisher;
import com.armedia.acm.services.timesheet.service.TimesheetService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TimesheetEventHandler implements PipelineHandler<AcmTimesheet, TimesheetPipelineContext>
{
    private final Logger log = LogManager.getLogger(getClass());
    private TimesheetEventPublisher timesheetEventPublisher;
    private TimesheetService timesheetService;
    private FormsTypeCheckService formsTypeCheckService;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(AcmTimesheet timesheet, TimesheetPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo")) {
            log.info("Timesheet with id [{}] and title [{}] entering TimesheetEventHandler", timesheet.getId(), timesheet.getTitle());

            String submissionName = ctx.getSubmissonName(); // "Save" or "Submit"
            UploadedFiles uploadedFiles = new UploadedFiles();

            EcmFile existing = getEcmFileDao().findForContainerAttachmentFolderAndFileType(timesheet.getContainer().getId(),
                    timesheet.getContainer().getAttachmentFolder().getId(), TimesheetConstants.TIMESHEET_DOCUMENT);
            uploadedFiles.setPdfRendition(existing);

            boolean startWorkflow = getTimesheetService()
                    .checkWorkflowStartup(TimesheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

            getTimesheetEventPublisher().publishEvent(timesheet, ctx.getAuthentication().getName(), ctx.getIpAddress(), true,
                    submissionName.toLowerCase(), uploadedFiles,
                    startWorkflow);

            log.info("Timesheet with id [{}] and title [{}] exiting TimesheetEventHandler", timesheet.getId(), timesheet.getTitle());
        }
    }

    @Override
    public void rollback(AcmTimesheet entity, TimesheetPipelineContext ctx) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public TimesheetEventPublisher getTimesheetEventPublisher()
    {
        return timesheetEventPublisher;
    }

    public void setTimesheetEventPublisher(TimesheetEventPublisher timesheetEventPublisher)
    {
        this.timesheetEventPublisher = timesheetEventPublisher;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService) {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
