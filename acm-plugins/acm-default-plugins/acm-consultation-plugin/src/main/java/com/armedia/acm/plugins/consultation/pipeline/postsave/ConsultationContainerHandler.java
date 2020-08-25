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

import com.armedia.acm.plugins.consultation.model.Consultation;
import com.armedia.acm.plugins.consultation.pipeline.ConsultationPipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
public class ConsultationContainerHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        log.trace("Consultation entering ConsultationContainerHandler : [{}]", entity);
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
            entity.getContainer().setContainerObjectTitle(entity.getConsultationNumber());
        }

        if (entity.getContainer().getFolder() == null)
        {
            AcmFolder folder = new AcmFolder();
            folder.setName("ROOT");
            folder.setParticipants(getFileParticipantService().getFolderParticipantsFromAssignedObject(entity.getParticipants()));

            entity.getContainer().setFolder(folder);
            entity.getContainer().setAttachmentFolder(folder);
        }

        log.trace("Consultation exiting ConsultationContainerHandler : [{}]", entity);
    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
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
