package com.armedia.acm.plugins.documentrepository.service.impl;

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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryConstants;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryEvent;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryEventPublisher;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryService;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;
import com.armedia.acm.services.note.dao.NoteDao;
import com.armedia.acm.services.note.model.Note;
import com.armedia.acm.services.note.model.NoteConstants;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentRepositoryServiceImpl implements DocumentRepositoryService
{
    private final Logger log = LogManager.getLogger(getClass());

    private DocumentRepositoryDao documentRepositoryDao;

    private AcmFolderService acmFolderService;

    private NoteDao noteDao;

    private ObjectAssociationService objectAssociationService;

    private AssociatedTagService associatedTagService;

    private PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> pipelineManager;

    private DocumentRepositoryEventPublisher documentRepositoryEventPublisher;

    private ExecuteSolrQuery executeSolrQuery;

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
    public DocumentRepository save(DocumentRepository documentRepository, Authentication authentication) throws PipelineProcessException
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

        return pipelineManager.executeOperation(documentRepository, pipelineContext, () -> {
            log.debug("Saving document repository: {}", documentRepository.getName());
            if (documentRepository.getCreator() == null)
            {
                documentRepository.setCreator(authentication.getName());
            }
            DocumentRepository savedDocumentRepository = documentRepositoryDao.save(documentRepository);

            publishAuditEvents(pipelineContext.getAuditEventTypes(), savedDocumentRepository);
            return savedDocumentRepository;
        });
    }

    private void publishAuditEvents(List<String> auditEventTypes, DocumentRepository documentRepository)
    {
        auditEventTypes.forEach(eventType -> {
            DocumentRepositoryEvent event = new DocumentRepositoryEvent(documentRepository, eventType);
            event.setIpAddress(AuthenticationUtils.getUserIpAddress());
            event.setSucceeded(true);
            documentRepositoryEventPublisher.publishEvent(event);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Authentication authentication) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        DocumentRepository documentRepository = documentRepositoryDao.find(id);
        log.info("Deleting Document Repository: {} with id: {}", documentRepository.getName(), id);
        documentRepositoryDao.deleteDocumentRepository(id);
        String docRepoName = documentRepository.getName();
        AcmContainer docRepoContainer = documentRepository.getContainer();
        List<Note> docRepoNoteList = noteDao.listNotes(NoteConstants.NOTE_GENERAL, id, DocumentRepositoryConstants.OBJECT_TYPE);
        List<AcmAssociatedTag> docRepoTagList = new ArrayList<>();
        List<ObjectAssociation> references = documentRepository.getReferences();

        log.info("Deleting notes within Document Repository: {} with id: {}", docRepoName, id);
        docRepoNoteList.forEach(note -> noteDao.deleteNoteById(note.getId()));

        try
        {
            docRepoTagList = getAssociatedTagService().getAcmAssociatedTagsByObjectIdAndType(id, DocumentRepositoryConstants.OBJECT_TYPE,
                    authentication);

        }
        catch (AcmObjectNotFoundException e)
        {
            log.info("There aren't any tags associated to Document Repository: {} with id: {}", docRepoName, id);
        }

        log.info("Deleting associated tags within Document Repository: {} with id: {}", docRepoName, id);
        for (AcmAssociatedTag acmAssociatedTag : docRepoTagList)
        {
            try
            {
                getAssociatedTagService().removeAssociatedTag(acmAssociatedTag);
            }
            catch (SQLException e)
            {
                log.warn("Can't delete tag: {} associated to DocumentRepository: {} with id: {}", acmAssociatedTag.getTag().getTagName(),
                        docRepoName, id);
            }
        }

        log.info("Deleting references within Document Repository: {} with id: {}", docRepoName, id);
        references.forEach(reference -> getObjectAssociationService().delete(reference.getAssociationId()));

        log.info("Deleting container with id: {} within Document Repository: {} with id: {}", docRepoContainer.getId(),
                documentRepository.getName(), id);
        getAcmFolderService().deleteContainerSafe(docRepoContainer, authentication);
    }

    @Override
    public String getDocumentRepositoryTasks(Long id, String sort, int startRow, int maxRows, Authentication authentication)
            throws SolrException
    {
        String query = "object_type_s:TASK AND -status_lcs: DELETE AND parent_type_s : DOC_REPO + AND parent_object_id_i:" + id;

        String retval = executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort);

        return retval;
    }

    public DocumentRepositoryDao getDocumentRepositoryDao()
    {
        return documentRepositoryDao;
    }

    public void setDocumentRepositoryDao(DocumentRepositoryDao documentRepositoryDao)
    {
        this.documentRepositoryDao = documentRepositoryDao;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }

    public PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> getPipelineManager()
    {
        return pipelineManager;
    }

    public void setPipelineManager(PipelineManager<DocumentRepository, DocumentRepositoryPipelineContext> pipelineManager)
    {
        this.pipelineManager = pipelineManager;
    }

    public NoteDao getNoteDao()
    {
        return noteDao;
    }

    public void setNoteDao(NoteDao noteDao)
    {
        this.noteDao = noteDao;
    }

    public AssociatedTagService getAssociatedTagService()
    {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService)
    {
        this.associatedTagService = associatedTagService;
    }

    public ObjectAssociationService getObjectAssociationService()
    {
        return objectAssociationService;
    }

    public void setObjectAssociationService(ObjectAssociationService objectAssociationService)
    {
        this.objectAssociationService = objectAssociationService;
    }

    public DocumentRepositoryEventPublisher getDocumentRepositoryEventPublisher()
    {
        return documentRepositoryEventPublisher;
    }

    public void setDocumentRepositoryEventPublisher(DocumentRepositoryEventPublisher documentRepositoryEventPublisher)
    {
        this.documentRepositoryEventPublisher = documentRepositoryEventPublisher;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
