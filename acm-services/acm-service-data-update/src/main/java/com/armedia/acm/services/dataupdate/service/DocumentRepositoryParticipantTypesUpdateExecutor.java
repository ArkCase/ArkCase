package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.web.api.MDCConstants;

import org.slf4j.MDC;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentRepositoryParticipantTypesUpdateExecutor implements AcmDataUpdateExecutor
{
    private DocumentRepositoryDao documentDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Override
    public String getUpdateId()
    {
        return "document-repository-participant-types-update";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute()
    {

        MDC.put(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, "DATA_UPDATE");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, "localhost");
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        List<DocumentRepository> documentRepositoryList = documentDao.findAll();

        for (DocumentRepository repository : documentRepositoryList)
        {
            Boolean shouldUpdate = false;
            List<AcmParticipant> newParticipants = new ArrayList<>();
            List<AcmParticipant> participants = repository.getParticipants();
            for (AcmParticipant participant : participants)
            {
                String mapedParticipantType = participant.getParticipantType();
                switch (participant.getParticipantType())
                {
                case "assignee":
                {
                    if (repository.getCreator().equals(participant.getParticipantLdapId())
                            && !newParticipants.stream().anyMatch(p -> p.getParticipantType().equals("owner")))
                    {

                        mapedParticipantType = "owner";
                    }
                    else
                    {
                        mapedParticipantType = "collaborator";
                    }
                    break;
                }
                case "co-owner":
                    if (newParticipants.stream().anyMatch(p -> p.getParticipantType().equals("owner")))
                    {
                        mapedParticipantType = "collaborator";
                    }
                    else
                    {
                        mapedParticipantType = "owner";
                    }
                    break;

                case "supervisor":
                    mapedParticipantType = "reader";
                    break;

                case "approver":
                    mapedParticipantType = "collaborator";
                    break;

                case "follower":
                    mapedParticipantType = "reader";
                    break;
                }
                if (!mapedParticipantType.equals(participant.getParticipantType()))
                {
                    String finalMapedParticipantType = mapedParticipantType;
                    boolean present = newParticipants.stream().anyMatch(p -> p.getParticipantType().equals(finalMapedParticipantType)
                            && p.getParticipantLdapId().equals(participant.getParticipantLdapId()));
                    if (!present)
                    {
                        participant.setParticipantType(mapedParticipantType);
                        newParticipants.add(participant);
                    }
                    shouldUpdate = true;
                }
                else
                {
                    newParticipants.add(participant);
                }

            }

            if (shouldUpdate)
            {
                repository.setParticipants(newParticipants);
                documentDao.save(repository);
            }
        }
    }

    public DocumentRepositoryDao getDocumentDao()
    {
        return documentDao;
    }

    public void setDocumentDao(DocumentRepositoryDao documentDao)
    {
        this.documentDao = documentDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }
}
