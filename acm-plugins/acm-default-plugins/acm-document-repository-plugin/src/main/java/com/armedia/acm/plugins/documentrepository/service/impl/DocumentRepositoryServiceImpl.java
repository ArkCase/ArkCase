package com.armedia.acm.plugins.documentrepository.service.impl;


import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryEvent;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryEventPublisher;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class DocumentRepositoryServiceImpl implements DocumentRepositoryService
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private DocumentRepositoryDao documentRepositoryDao;

    private PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> pipelineManager;

    private DocumentRepositoryEventPublisher documentRepositoryEventPublisher;

    @Override
    public DocumentRepository findById(Long id)
    {
        return documentRepositoryDao.find(id);
    }

    @Override
    public DocumentRepository findByName(String name)
    {
        return documentRepositoryDao.findByName(name);
    }

    @Override
    @Transactional
    public DocumentRepository save(DocumentRepository documentRepository, Authentication authentication)
            throws PipelineProcessException
    {
        boolean isNew = documentRepository.getId() == null;
        DocumentRepositoryPipelineContext pipelineContext = new DocumentRepositoryPipelineContext();
        // populate the context
        pipelineContext.setNewDocumentRepository(isNew);
        pipelineContext.setAuthentication(authentication);
        String ipAddress = AuthenticationUtils.getUserIpAddress();
        pipelineContext.setIpAddress(ipAddress);

        if (!isNew)
        {
            DocumentRepository existingDocumentRepository = documentRepositoryDao.find(documentRepository.getId());
            pipelineContext.setDocumentRepository(existingDocumentRepository);
        }

        return pipelineManager.executeOperation(documentRepository, pipelineContext, () ->
        {
            log.debug("Saving document repository: {}", documentRepository.getName());
            DocumentRepository savedDocumentRepository = documentRepositoryDao.save(documentRepository);
            publishAuditEvents(pipelineContext.getAuditEventTypes(), savedDocumentRepository);
            return savedDocumentRepository;
        });
    }

    private void publishAuditEvents(List<String> auditEventTypes, DocumentRepository documentRepository)
    {
        auditEventTypes.forEach(eventType ->
        {
            DocumentRepositoryEvent event = new DocumentRepositoryEvent(documentRepository, eventType);
            event.setIpAddress(AuthenticationUtils.getUserIpAddress());
            event.setSucceeded(true);
            documentRepositoryEventPublisher.publishEvent(event);
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

    public DocumentRepositoryEventPublisher getDocumentRepositoryEventPublisher()
    {
        return documentRepositoryEventPublisher;
    }

    public void setDocumentRepositoryEventPublisher(DocumentRepositoryEventPublisher documentRepositoryEventPublisher)
    {
        this.documentRepositoryEventPublisher = documentRepositoryEventPublisher;
    }
}
