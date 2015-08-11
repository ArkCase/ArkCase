package com.armedia.acm.plugins.casefile.pipeline.postsave;

import com.armedia.acm.core.exceptions.AcmOutlookCreateItemFailedException;
import com.armedia.acm.core.exceptions.AcmOutlookItemNotFoundException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.outlook.service.OutlookContainerCalendarService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create Outlook folder and update participants for a Case File.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFileOutlookHandler implements PipelineHandler<CaseFile, CaseFilePipelineContext>
{
    /**
     * Auto create folder for case file flag.
     */
    private boolean autoCreateFolderForCaseFile;

    /**
     * Outlook calendar service
     */
    private OutlookContainerCalendarService outlookContainerCalendarService;

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void execute(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        //create calendar folder
        if (autoCreateFolderForCaseFile && pipelineContext.isNewCase())
        {
            createOutlookFolder(entity, pipelineContext);
        }

        if (!pipelineContext.isNewCase() && !StringUtils.isEmpty(entity.getContainer().getCalendarFolderId()))
        {
            //update folder participants
            updateOutlookFolderParticipants(entity, pipelineContext);
        }

    }

    @Override
    public void rollback(CaseFile entity, CaseFilePipelineContext pipelineContext) throws PipelineProcessException
    {
        // TODO: delete Outlook/Calendar folder
    }

    private void createOutlookFolder(CaseFile caseFile, CaseFilePipelineContext pipelineContext)
    {
        try
        {
            outlookContainerCalendarService.createFolder(caseFile.getTitle() + "(" + caseFile.getCaseNumber() + ")",
                    caseFile.getContainer(), caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error creating calendar folder for '{}'", caseFile.getCaseNumber(), e);
        } catch (AcmOutlookCreateItemFailedException e)
        {
            log.error("Error creating calendar folder for '{}'", caseFile.getCaseNumber(), e);
        }
    }

    private void updateOutlookFolderParticipants(CaseFile caseFile, CaseFilePipelineContext pipelineContext)
    {
        try
        {
            AcmContainer container = caseFile.getContainer();
            outlookContainerCalendarService.updateFolderParticipants(container.getCalendarFolderId(),
                    caseFile.getParticipants());
        } catch (AcmOutlookItemNotFoundException e)
        {
            log.error("Error updating participants for '{}'", caseFile.getCaseNumber(), e);
        }
    }

    public boolean isAutoCreateFolderForCaseFile()
    {
        return autoCreateFolderForCaseFile;
    }

    public void setAutoCreateFolderForCaseFile(boolean autoCreateFolderForCaseFile)
    {
        this.autoCreateFolderForCaseFile = autoCreateFolderForCaseFile;
    }

    public OutlookContainerCalendarService getOutlookContainerCalendarService()
    {
        return outlookContainerCalendarService;
    }

    public void setOutlookContainerCalendarService(OutlookContainerCalendarService outlookContainerCalendarService)
    {
        this.outlookContainerCalendarService = outlookContainerCalendarService;
    }
}
