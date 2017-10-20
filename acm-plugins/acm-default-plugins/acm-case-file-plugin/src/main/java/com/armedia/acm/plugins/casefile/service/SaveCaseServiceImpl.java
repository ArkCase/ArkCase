package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseServiceImpl implements SaveCaseService
{
    private CaseFileDao caseFileDao;
    private EcmFileParticipantService fileParticipantService;

    private PipelineManager<CaseFile, CaseFilePipelineContext> pipelineManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile in, Authentication auth, String ipAddress) throws PipelineProcessException
    {
        CaseFilePipelineContext pipelineContext = new CaseFilePipelineContext();
        // populate the context
        pipelineContext.setNewCase(in.getId() == null);
        pipelineContext.setAuthentication(auth);
        pipelineContext.setIpAddress(ipAddress);

        return pipelineManager.executeOperation(in, pipelineContext, () -> {
            CaseFile originalCaseFile = null;
            if (in.getId() != null)
            {
                originalCaseFile = caseFileDao.find(in.getId());
            }
            CaseFile saved = caseFileDao.save(in);
            try
            {
                getFileParticipantService().inheritParticipantsFromAssignedObject(in.getParticipants(),
                        originalCaseFile == null ? new ArrayList<>() : originalCaseFile.getParticipants(), in.getContainer().getFolder());
                getFileParticipantService().inheritParticipantsFromAssignedObject(in.getParticipants(),
                        originalCaseFile == null ? new ArrayList<>() : originalCaseFile.getParticipants(),
                        in.getContainer().getAttachmentFolder());
            }
            catch (AcmAccessControlException e)
            {
                throw new PipelineProcessException(e);
            }
            log.info("Case saved '{}'", saved);
            return saved;

        });
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    public PipelineManager getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager pipelineManager)
    {
        this.pipelineManager = pipelineManager;
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
