package com.armedia.acm.plugins.documentrepository.web;

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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryEventPublisher;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;
import java.util.Objects;

@Controller
@RequestMapping({ "/api/v1/plugin/documentrepository", "/api/latest/plugin/documentrepository" })
public class DocumentRepositoryAPIController
{

    final private Logger log = LogManager.getLogger(getClass());
    private DocumentRepositoryService documentRepositoryService;
    private DocumentRepositoryEventPublisher documentRepositoryEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public DocumentRepository saveDocumentRepository(@RequestBody DocumentRepository in, Authentication auth)
            throws AcmCreateObjectFailedException, AcmAppErrorJsonMsg
    {
        DocumentRepository existingDocRepo = getDocumentRepositoryService().findByName(in.getName());
        if (existingDocRepo != null && (in.getId() == null || !Objects.equals(in.getId(), existingDocRepo.getId())))
        {
            log.warn("Duplicate Document Repository name: {}", in.getName());
            throw new AcmAppErrorJsonMsg(String.format(
                    "Document Repository with name: [%s] already exists!", in.getName()),
                    DocumentRepositoryConstants.OBJECT_TYPE, "name", null);
        }
        try
        {
            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());
            return getDocumentRepositoryService().save(in, auth);
        }
        catch (PipelineProcessException | TransactionException e)
        {
            log.error("Could not save Document Repository: {}", in.getName(), e);
            throw new AcmCreateObjectFailedException(DocumentRepositoryConstants.OBJECT_TYPE, e.getMessage(), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @DecoratedAssignedObjectParticipants
    @PreAuthorize("hasPermission(#id, 'DOC_REPO', 'getDocumentRepository')")
    @ResponseBody
    public DocumentRepository findById(@PathVariable(value = "id") Long id) throws AcmObjectNotFoundException
    {
        DocumentRepository docRepo = getDocumentRepositoryService().findById(id);

        if (docRepo == null)
        {
            log.warn("Document Repository with id: [{}] not found!", id);
            throw new AcmObjectNotFoundException(DocumentRepositoryConstants.OBJECT_TYPE, id,
                    String.format("Document Repository with id: [%d] not found!", id));
        }
        documentRepositoryEventPublisher.publishViewedEvent(docRepo, true);
        return docRepo;
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable(value = "id") Long id, Authentication authentication)
            throws AcmUserActionFailedException, AcmObjectNotFoundException
    {
        log.info("Deleting Document Repository with id: [{}]", id);
        getDocumentRepositoryService().delete(id, authentication);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasPermission(#id, 'DOC_REPO', 'getDocumentRepository')")
    @ResponseBody
    public String findDocumentRepositoryTasks(@PathVariable(value = "id") Long id,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication) throws SolrException
    {
        return getDocumentRepositoryService().getDocumentRepositoryTasks(id, sort, startRow, maxRows, authentication);
    }

    public DocumentRepositoryService getDocumentRepositoryService()
    {
        return documentRepositoryService;
    }

    public void setDocumentRepositoryService(DocumentRepositoryService documentRepositoryService)
    {
        this.documentRepositoryService = documentRepositoryService;
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
