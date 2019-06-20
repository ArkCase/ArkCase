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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import com.armedia.acm.services.timesheet.model.AcmTimesheet;
import com.armedia.acm.services.timesheet.pipeline.TimesheetPipelineContext;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Create Alfresco folder on saving a Timesheet.
 */
public class TimesheetEcmFolderHandler implements PipelineHandler<AcmTimesheet, TimesheetPipelineContext> {
    private final Logger log = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    @Override
    public void execute(AcmTimesheet entity, TimesheetPipelineContext ctx) throws PipelineProcessException {
        log.trace("Timesheet with id [{}] and title [{}] entering TimesheetEcmFolderHandler", entity.getId(), entity.getTitle());
        if (entity.getEcmFolderPath() != null) {
            try {
                String folderId = ecmFileService.createFolder(entity.getEcmFolderPath());
                entity.getContainer().getFolder().setCmisFolderId(folderId);
            } catch (AcmCreateObjectFailedException e) {
                throw new PipelineProcessException(e);
            }

        } else {
            log.trace("There is no need to create folder for timesheet [{}]", entity.getId());
        }
        log.trace("Timesheet with id [{}] and title [{}] exiting TimesheetEcmFolderHandler", entity.getId(), entity.getTitle());
    }

    @Override
    public void rollback(AcmTimesheet entity, TimesheetPipelineContext ctx) throws PipelineProcessException {
        // TODO: implement CMIS folder deletion
    }

    public void setEcmFileService(EcmFileService ecmFileService) {
        this.ecmFileService = ecmFileService;
    }
}
