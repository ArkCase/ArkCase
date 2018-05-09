package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(getClass());
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
