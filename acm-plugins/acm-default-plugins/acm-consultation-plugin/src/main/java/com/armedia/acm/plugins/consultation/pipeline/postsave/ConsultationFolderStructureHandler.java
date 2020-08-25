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
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.security.core.Authentication;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 * Create folder structure for a Consultation.
 */
public class ConsultationFolderStructureHandler implements PipelineHandler<Consultation, ConsultationPipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Consultation folder structure.
     */
    private String folderStructureAsString;
    /**
     * CMIS service.
     */
    private EcmFileService ecmFileService;
    /**
     * ACM folder service.
     */
    private AcmFolderService acmFolderService;

    @Override
    public void execute(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        Authentication auth = pipelineContext.getAuthentication();
        log.info("Consultation entity entry Core ConsultationFolderStructureHandler : [{}]", entity);

        if (pipelineContext.isNewConsultation())
        {
            createFolderStructure(entity);
        }
        log.info("Consultation entity exit Core ConsultationFolderStructureHandler : [{}]", entity);

    }

    @Override
    public void rollback(Consultation entity, ConsultationPipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO: delete folder structure and maybe raise another event (deleted)?
    }

    private void createFolderStructure(Consultation consultation)
    {
        if (folderStructureAsString != null && !folderStructureAsString.isEmpty())
        {
            try
            {
                log.debug("Folder Structure [{}]", folderStructureAsString);
                JSONArray folderStructure = new JSONArray(folderStructureAsString);
                AcmContainer container = ecmFileService.getOrCreateContainer(consultation.getObjectType(), consultation.getId());
                acmFolderService.addFolderStructure(container, container.getFolder(), folderStructure);
            }
            catch (Exception e)
            {
                log.error("Cannot create folder structure.", e);
            }
        }
    }

    public String getFolderStructureAsString()
    {
        return folderStructureAsString;
    }

    public void setFolderStructureAsString(String folderStructureAsString)
    {
        this.folderStructureAsString = folderStructureAsString;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }
}
