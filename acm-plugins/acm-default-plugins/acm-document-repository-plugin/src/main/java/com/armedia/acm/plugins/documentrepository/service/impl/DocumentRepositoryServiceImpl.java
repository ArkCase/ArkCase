package com.armedia.acm.plugins.documentrepository.service.impl;


import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public class DocumentRepositoryServiceImpl implements DocumentRepositoryService
{
    final Logger log = LoggerFactory.getLogger(getClass());

    private DocumentRepositoryDao documentRepositoryDao;

    private PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> pipelineManager;

    @Override
    public DocumentRepository findById(Long id)
    {
        return documentRepositoryDao.find(id);
    }

    @Override
    @Transactional
    public DocumentRepository save(DocumentRepository documentRepository, Authentication authentication)
            throws PipelineProcessException
    {
        DocumentRepositoryPipelineContext pipelineContext = new DocumentRepositoryPipelineContext();
        // populate the context
        pipelineContext.setNewComplaint(documentRepository.getId() == null);
        pipelineContext.setAuthentication(authentication);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);

        return pipelineManager.executeOperation(documentRepository, pipelineContext, () ->
        {
            log.debug("Saving document repository: {}", documentRepository.getName());
            return documentRepositoryDao.save(documentRepository);
        });
    }

    public DocumentRepositoryDao getDocumentRepositoryDao()
    {
        return documentRepositoryDao;
    }


    public void setDocumentRepositoryDao(DocumentRepositoryDao documentRepositoryDao)
    {
        this.documentRepositoryDao = documentRepositoryDao;
    }

    public PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }
}
