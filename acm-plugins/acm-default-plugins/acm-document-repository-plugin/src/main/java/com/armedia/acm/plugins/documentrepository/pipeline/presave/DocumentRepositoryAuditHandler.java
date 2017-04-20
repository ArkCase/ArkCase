package com.armedia.acm.plugins.documentrepository.pipeline.presave;

import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.plugins.documentrepository.pipeline.DocumentRepositoryPipelineContext;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.pipeline.handler.PipelineHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Prepare all event types for any updates on DocumentRepository
 */
public class DocumentRepositoryAuditHandler implements PipelineHandler<DocumentRepository, DocumentRepositoryPipelineContext>
{
    @Override
    public void execute(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        List<String> auditEventTypes = prepareEvents(pipelineContext.getDocumentRepository(), entity);
        pipelineContext.setAuditEventTypes(auditEventTypes);
    }

    private List<String> prepareEvents(DocumentRepository existingRepo, DocumentRepository updateRepo)
    {
        List<String> auditEvents = new ArrayList<>();

        if (existingRepo == null)
        {
            auditEvents.add("created");
        } else
        {
            if (!existingRepo.getName().equalsIgnoreCase(updateRepo.getName()))
            {
                auditEvents.add("name.updated");
            }

            auditEvents.addAll(prepareParticipantEvents(existingRepo, updateRepo));
        }

        return auditEvents;
    }

    private List<String> prepareParticipantEvents(DocumentRepository existingRepo, DocumentRepository updateRepo)
    {
        List<AcmParticipant> existing = existingRepo.getParticipants();
        List<AcmParticipant> updated = updateRepo.getParticipants();
        List<String> auditEvents = new ArrayList<>();

        boolean deleted = existing.stream().anyMatch(it -> !updated.contains(it));
        boolean added = updated.stream().anyMatch(it -> !existing.contains(it));

        if (!deleted && !added)
        {
            Map<Long, String> existingParticipants = existing.stream()
                    .collect(Collectors.toMap(AcmParticipant::getId, AcmParticipant::getParticipantLdapId));
            Map<Long, String> editedParticipants = updated.stream()
                    .collect(Collectors.toMap(AcmParticipant::getId, AcmParticipant::getParticipantLdapId));

            boolean edited = existingParticipants.entrySet().stream()
                    .anyMatch(entry -> !Objects.equals(entry.getValue(), editedParticipants.get(entry.getKey())));

            if (edited)
            {
                auditEvents.add("participant.updated");
            }
        }

        if (deleted)
        {
            // participant deleted
            auditEvents.add("participant.deleted");
        }

        if (added)
        {
            // participant added
            auditEvents.add("participant.added");
        }
        return auditEvents;
    }

    @Override
    public void rollback(DocumentRepository entity, DocumentRepositoryPipelineContext pipelineContext)
            throws PipelineProcessException
    {
        // nothing to do here, there is no rollback action to be executed
    }
}
