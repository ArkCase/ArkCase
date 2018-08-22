package com.armedia.acm.services.costsheet.pipeline.postsave;

import com.armedia.acm.frevvo.model.FrevvoUploadedFiles;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.services.costsheet.model.AcmCostsheet;
import com.armedia.acm.services.costsheet.model.CostsheetConstants;
import com.armedia.acm.services.costsheet.pipeline.CostsheetPipelineContext;
import com.armedia.acm.services.costsheet.service.CostsheetEventPublisher;
import com.armedia.acm.services.costsheet.service.CostsheetService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CostsheetEventHandler implements PipelineHandler<AcmCostsheet, CostsheetPipelineContext>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private CostsheetEventPublisher costsheetEventPublisher;
    private CostsheetService costsheetService;
    private EcmFileDao ecmFileDao;

    @Override
    public void execute(AcmCostsheet costsheet, CostsheetPipelineContext ctx) throws PipelineProcessException
    {
        log.info("Costsheet with id [{}] and title [{}] entering CostsheetEventHandler", costsheet.getId(), costsheet.getTitle());

        String submissionName = ctx.getSubmissonName(); // "Save" or "Submit"
        FrevvoUploadedFiles uploadedFiles = new FrevvoUploadedFiles();

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
}