package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import java.util.Date;

/**
 * Create folder structure for a Case File.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileFolderStructureHandler implements PipelineHandler<CaseFile>
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        CaseFilePipelineContext context = (CaseFilePipelineContext) pipelineContext;
        Authentication auth = context.getAuthentication();

        if (context.isNewCase())
        {
            createFolderStructure(entity, context);
            context.getCaseFileEventUtility().raiseEvent(entity, entity.getStatus(), new Date(), context.getIpAddress(), auth.getName(), auth);
        } else
        {
            context.getCaseFileEventUtility().raiseEvent(entity, "updated", new Date(), context.getIpAddress(), auth.getName(), auth);
        }

    }

    @Override
    public void rollback(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }


    private void createFolderStructure(CaseFile caseFile, CaseFilePipelineContext context)
    {
        if (context.getFolderStructureAsString() != null && !context.getFolderStructureAsString().isEmpty())
            try
            {
                log.debug("Folder Structure [{}]" + context.getFolderStructureAsString());
                JSONArray folderStructure = new JSONArray(context.getFolderStructureAsString());
                AcmContainer container = getContainer(caseFile, context);
                context.getAcmFolderService().addFolderStructure(container, container.getFolder(), folderStructure);
            } catch (Exception e)
            {
                log.error("Cannot create folder structure.", e);
            }
    }

    private AcmContainer getContainer(CaseFile caseFile, CaseFilePipelineContext context) throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        return context.getEcmFileService().getOrCreateContainer(caseFile.getObjectType(), caseFile.getId());
    }
}
