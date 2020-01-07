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

import com.armedia.acm.form.config.FormsTypeCheckService;
import com.armedia.acm.frevvo.model.UploadedFiles;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.costsheet.service.CostsheetEventPublisher;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CostsheetEventHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{
    private final Logger log = LogManager.getLogger(getClass());
    private CostsheetEventPublisher costsheetEventPublisher;
    private CostsheetService costsheetService;
    private EcmFileDao ecmFileDao;
    private FormsTypeCheckService formsTypeCheckService;

    @Override
    public void execute(AcmCostsheet costsheet, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        if (!formsTypeCheckService.getTypeOfForm().equals("frevvo"))
        {
            log.info("Costsheet with id [{}] and title [{}] entering CostsheetEventHandler", costsheet.getId(), costsheet.getTitle());

            String submissionName = ctx.getSubmissonName(); // "Save" or "Submit"
            UploadedFiles uploadedFiles = new UploadedFiles();

            EcmFile existing = getEcmFileDao().findForContainerAttachmentFolderAndFileType(costsheet.getContainer().getId(),
                    costsheet.getContainer().getAttachmentFolder().getId(), CostsheetConstants.COSTSHEET_DOCUMENT);
            uploadedFiles.setPdfRendition(existing);

            boolean startWorkflow = getCostsheetService()
                    .checkWorkflowStartup(CostsheetConstants.EVENT_TYPE + "." + submissionName.toLowerCase());

            getCostsheetEventPublisher().publishEvent(costsheet, ctx.getAuthentication().getName(), ctx.getIpAddress(), true,
                    submissionName.toLowerCase(), uploadedFiles,
                    startWorkflow);

            log.info("Costsheet with id [{}] and title [{}] exiting CostsheetEventHandler", costsheet.getId(), costsheet.getTitle());
        }
    }

    @Override
    public void rollback(AcmCostsheet entity, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        // nothing to execute on rollback
    }

    public CostsheetEventPublisher getCostsheetEventPublisher()
    {
        return costsheetEventPublisher;
    }

    public void setCostsheetEventPublisher(CostsheetEventPublisher costsheetEventPublisher)
    {
        this.costsheetEventPublisher = costsheetEventPublisher;
    }

    public CostsheetService getCostsheetService()
    {
        return costsheetService;
    }

    public void setCostsheetService(CostsheetService costsheetService)
    {
        this.costsheetService = costsheetService;
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public FormsTypeCheckService getFormsTypeCheckService()
    {
        return formsTypeCheckService;
    }

    public void setFormsTypeCheckService(FormsTypeCheckService formsTypeCheckService)
    {
        this.formsTypeCheckService = formsTypeCheckService;
    }
}
