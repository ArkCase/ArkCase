package com.armedia.acm.services.dataupdate.service;

import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public class DocumentRepositoryParticipantTypesUpdateExecutor implements AcmDataUpdateExecutor
{
    private DocumentRepositoryDao documentDao;

    @Override
    public String getUpdateId()
    {
        return "document-repository-participant-types-update";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void execute()
    {
        List<DocumentRepository> documentRepositoryList = documentDao.findAll();

        for (DocumentRepository repository : documentRepositoryList)
        {
            List<AcmParticipant> newParticipants = new ArrayList<>();
            List<AcmParticipant> participants = repository.getParticipants();
            for (AcmParticipant participant : participants)
            {
                String mapedParticipantType = participant.getParticipantType();
                switch (participant.getParticipantType())
                {
                case "assignee":
                {
                    if (repository.getCreator().equals(participant.getParticipantLdapId()))
                    {

                        mapedParticipantType = "owner";
                    }
                    else
                    {
                        mapedParticipantType = "reader";
                    }
                    break;
                }
                case "co-owner":
                    mapedParticipantType = "owner";
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
                    boolean present = participants.stream().anyMatch(p -> p.getParticipantType().equals(finalMapedParticipantType)
                            && p.getParticipantLdapId().equals(participant.getParticipantLdapId()));
                    if (!present)
                    {
                        participant.setParticipantType(mapedParticipantType);
                        newParticipants.add(participant);
                    }
                }
                else
                {
                    newParticipants.add(participant);
                }

            }

            repository.setParticipants(newParticipants);
            documentDao.save(repository);
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
}
