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

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.json.JSONArray;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;

/**
 * Create folder structure for a Case File.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileFolderStructureHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    /**
     * Logger instance.
     */
    private final Logger log = LogManager.getLogger(getClass());
    /**
     * Case File folder structure.
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
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        Authentication auth = pipelineContext.getAuthentication();
        log.info("CaseFile entity entry Core CaseFileFolderStructureHandler : [{}]", entity);

        if (pipelineContext.isNewCase())
        {
            createFolderStructure(entity);
        }
        log.info("CaseFile entity exit Core CaseFileFolderStructureHandler : [{}]", entity);

    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO: delete folder structure and maybe raise another event (deleted)?
    }

    private void createFolderStructure(CaseFile caseFile)
    {
        if (folderStructureAsString != null && !folderStructureAsString.isEmpty())
        {
            try
            {
                log.debug("Folder Structure [{}]", folderStructureAsString);
                JSONArray folderStructure = new JSONArray(folderStructureAsString);
                AcmContainer container = ecmFileService.getOrCreateContainer(caseFile.getObjectType(), caseFile.getId());
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
