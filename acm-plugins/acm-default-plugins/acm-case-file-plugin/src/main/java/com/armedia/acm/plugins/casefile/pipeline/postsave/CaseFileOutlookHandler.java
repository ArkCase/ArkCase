package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.services.pipeline.PipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Outlook folder and update participants for a Case File.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileOutlookHandler implements PipelineHandler<CaseFile>
{
    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {
        CaseFilePipelineContext context = (CaseFilePipelineContext) pipelineContext;

        //create calendar folder
        if (context.isAutoCreateFolderForCaseFile() && context.isNewCase())
        {
            createOutlookFolder(entity, context);
        }

        if (!context.isNewCase() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            //update folder participants
            updateOutlookFolderParticipants(entity, context);
        }

    }

    @Override
    public void rollback(CaseFile entity, PipelineContext pipelineContext) throws PipelineProcessException
    {

    }

    private void createOutlookFolder(CaseFile caseFile, CaseFilePipelineContext context)
    {
        try
        {
            context.getOutlookContainerCalendarService().createFolder(caseFile.getTitle() + "(" + caseFile.getCaseNumber() + ")",
                    caseFile.getContainer(), caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error creating calendar folder for '{}'", caseFile.getCaseNumber(), e);
        } catch (AcmOutlookCreateItemFailedException e)
        {
            log.error("Error creating calendar folder for '{}'", caseFile.getCaseNumber(), e);
        }
    }

    private void updateOutlookFolderParticipants(CaseFile caseFile, CaseFilePipelineContext context)
    {
        try
        {
            AcmContainer container = caseFile.getContainer();
            context.getOutlookContainerCalendarService().updateFolderParticipants(container.getCalendarFolderId(),
                    caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error updating participants for '{}'", caseFile.getCaseNumber(), e);
        }
    }

}
