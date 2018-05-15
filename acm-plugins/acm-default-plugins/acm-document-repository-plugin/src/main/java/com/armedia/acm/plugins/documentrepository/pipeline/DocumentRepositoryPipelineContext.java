package com.armedia.acm.plugins.documentrepository.pipeline;

/*-
 * #%L
 * ACM Default Plugin: Document Repository
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
