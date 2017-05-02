package com.armedia.acm.plugins.documentrepository.service;


import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import org.springframework.security.core.Authentication;

public interface DocumentRepositoryService
{
    DocumentRepository findById(Long id);

    DocumentRepository findByName(String name);

    DocumentRepository save(DocumentRepository documentRepository, Authentication authentication)
            throws PipelineProcessException;

    void delete(Long id, Authentication authentication) throws AcmObjectNotFoundException, AcmUserActionFailedException;
}
