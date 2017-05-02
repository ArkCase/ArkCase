package com.armedia.acm.plugins.documentrepository.pipeline;

import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * Store all the Document Repository saving-related references in this context.
 */
public class DocumentRepositoryPipelineContext extends AbstractPipelineContext
{
    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * Flag showing whether new document repository is created.
     */
    private boolean newDocumentRepository;

    /**
     * IP Address.
     */
    private String ipAddress;

    /**
     * Existing version of documentRepository, to be checked for updates and audit the changes
     */
    private DocumentRepository documentRepository;

    private List<String> auditEventTypes;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public boolean isNewDocumentRepository()
    {
        return newDocumentRepository;
    }

    public void setNewDocumentRepository(boolean newDocumentRepository)
    {
        this.newDocumentRepository = newDocumentRepository;
    }

    public DocumentRepository getDocumentRepository()
    {
        return documentRepository;
    }

    public void setDocumentRepository(DocumentRepository documentRepository)
    {
        this.documentRepository = documentRepository;
    }

    public List<String> getAuditEventTypes()
    {
        return auditEventTypes;
    }

    public void setAuditEventTypes(List<String> auditEventTypes)
    {
        this.auditEventTypes = auditEventTypes;
    }
}
