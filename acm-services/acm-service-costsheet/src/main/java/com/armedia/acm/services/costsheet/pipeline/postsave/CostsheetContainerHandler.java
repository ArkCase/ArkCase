package com.armedia.acm.services.costsheet.pipeline.postsave;

/*-
 * #%L
 * ACM Service: Costsheet
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

import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CostsheetContainerHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{

    private final Logger log = LogManager.getLogger(getClass());
    private EcmFileParticipantService fileParticipantService;

    @Override
    public void execute(AcmCostsheet entity, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        log.trace("Costsheet with id [{}] and title [{}] entering CostsheetContainerHandler ", entity.getId(), entity.getTitle());
        if (entity.getContainer() == null)
        {
            AcmContainer container = new AcmContainer();
            entity.setContainer(container);
        }

        if (entity.getContainer().getContainerObjectType() == null)
        {
            entity.getContainer().setContainerObjectType(entity.getObjectType());
        }

        if (entity.getContainer().getContainerObjectTitle() == null)
        {
            entity.getContainer().setContainerObjectTitle(entity.getCostsheetNumber());
        }

        if (entity.getContainer().getFolder() == null)
        {
            AcmFolder folder = new AcmFolder();
            folder.setName("ROOT");
            folder.setParticipants(getFileParticipantService().getFolderParticipantsFromAssignedObject(entity.getParticipants()));

            entity.getContainer().setFolder(folder);
            entity.getContainer().setAttachmentFolder(folder);
        }
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
