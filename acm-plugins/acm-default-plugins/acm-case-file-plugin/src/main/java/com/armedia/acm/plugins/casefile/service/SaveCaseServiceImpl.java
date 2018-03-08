package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by armdev on 8/29/14.
 */
public class SaveCaseServiceImpl implements SaveCaseService
{
    private CaseFileDao caseFileDao;

    private PipelineManager<CaseFile, CaseFilePipelineContext> pipelineManager;
    private EcmFileService ecmFileService;

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

            CaseFile saved = caseFileDao.save(in);

            log.info("Case saved '{}'", saved);
            return saved;

        });
    }

    @Override
    @Transactional
    public CaseFile saveCase(CaseFile caseFile, List<MultipartFile> files, Authentication authentication, String ipAddress)
            throws AcmUserActionFailedException,
            AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException, PipelineProcessException,
            IOException
    {
        CaseFile saved = saveCase(caseFile, authentication, ipAddress);

        for (MultipartFile file : files)
        {
            if (file != null)
            {

                String folderId = saved.getContainer().getAttachmentFolder() == null ? saved.getContainer().getFolder().getCmisFolderId()
                        : saved.getContainer().getAttachmentFolder().getCmisFolderId();

                log.debug("Uploading document for FOIA Request [{}] as [{}]", saved.getId(), file.getOriginalFilename());

                getEcmFileService().upload(file.getOriginalFilename(), "other", "Document", file.getInputStream(), "",
                        file.getOriginalFilename(), authentication,
                        folderId, saved.getObjectType(), saved.getId());
            }
        }

        return saved;
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
