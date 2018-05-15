package com.armedia.acm.plugins.documentrepository.pipeline.presave;

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
        }
        else
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
