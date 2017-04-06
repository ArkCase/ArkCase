package com.armedia.acm.plugins.documentrepository.pipeline.postsave;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepositoryEvent;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.plugins.documentrepository.service.DocumentRepositoryEventPublisher;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Audit events for any updates related to DocumentRepository
 */
public class DocumentRepositoryAuditHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{
    private DocumentRepositoryEventPublisher documentRepositoryEventPublisher;

    @Override
    public void execute(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        List<DocumentRepositoryEvent> auditEvents = prepareEvents(pipelineContext.getDocumentRepository(), entity);
        auditEvents.forEach(event ->
        {
            event.setIpAddress(AuthenticationUtils.getUserIpAddress());
            event.setSucceeded(true);
            documentRepositoryEventPublisher.publishEvent(event);
        });
    }

    private List<DocumentRepositoryEvent> prepareEvents(DocumentRepository existingRepo, DocumentRepository updateRepo)
    {
        List<DocumentRepositoryEvent> auditEvents = new ArrayList<>();

        if (existingRepo == null)
        {
            auditEvents.add(new DocumentRepositoryEvent(updateRepo, "created"));
        } else
        {
            if (!existingRepo.getName().equalsIgnoreCase(updateRepo.getName()))
            {
                auditEvents.add(new DocumentRepositoryEvent(updateRepo, "name.updated"));
            }

            auditEvents.addAll(prepareParticipantEvents(existingRepo, updateRepo));

            auditEvents.add(new DocumentRepositoryEvent(updateRepo, "updated"));
        }

        return auditEvents;
    }

    private List<DocumentRepositoryEvent> prepareParticipantEvents(DocumentRepository existingRepo, DocumentRepository updateRepo)
    {
        List<AcmParticipant> existing = existingRepo.getParticipants();
        List<AcmParticipant> updated = updateRepo.getParticipants();

        boolean deleted = existing.stream().anyMatch(it -> !updated.contains(it));
        boolean added = updated.stream().anyMatch(it -> !existing.contains(it));

        List<DocumentRepositoryEvent> auditEvents = new ArrayList<>();
        if (deleted)
        {
            // participant deleted
            DocumentRepositoryEvent participantDeletedEvent = new DocumentRepositoryEvent(updateRepo, "participant.deleted");
            auditEvents.add(participantDeletedEvent);
        }

        if (added)
        {
            // participant added
            DocumentRepositoryEvent participantAddedEvent = new DocumentRepositoryEvent(updateRepo, "participant.added");
            auditEvents.add(participantAddedEvent);
        }
        return auditEvents;
    }

    @Override
    public void rollback(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
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
