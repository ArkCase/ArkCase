package com.armedia.acm.services.dataupdate.executors;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.dataupdate.service.TriggerDocumentRepositoryParticipantsExecutor;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentRepositoryParticipantsExecutorTest extends EasyMockSupport
{

    DocumentRepositoryDao documentDao;
    TriggerDocumentRepositoryParticipantsExecutor executor;

    @Before
    public void setUp() throws Exception
    {
        executor = new TriggerDocumentRepositoryParticipantsExecutor();
        documentDao = createMock(DocumentRepositoryDao.class);
        executor.setDocumentDao(documentDao);

    }

    @Test
    public void participantMapingTest()
    {
        List<DocumentRepository> list = new ArrayList<>();
        DocumentRepository docRepo = new DocumentRepository();
        docRepo.setParticipants(getOldParticipantList(4));
        list.add(docRepo);

        expect(documentDao.findAll()).andReturn(list);
        expect(documentDao.save(list.get(0))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                List<AcmParticipant> participants = docRepo.getParticipants();
                for (AcmParticipant participant : participants)
                {
                    switch (participant.getParticipantLdapId())
                    {
                    case "theReader":
                    {
                        assertTrue(participant.getParticipantType().equals("reader"));
                        break;
                    }
                    case "theCollaborator":
                    {
                        assertTrue(participant.getParticipantType().equals("reader"));
                        break;
                    }
                    case "theOwningGroup":
                    {
                        assertTrue(participant.getParticipantType().equals("owning group"));
                        break;
                    }
                    case "theFollower":
                    {
                        assertTrue(participant.getParticipantType().equals("reader"));
                        break;
                    }
                    case "theCoOwner":
                    {
                        assertTrue(participant.getParticipantType().equals("owner"));
                        break;
                    }
                    case "theAssignee":
                    {
                        assertTrue(participant.getParticipantType().equals("owner"));
                        break;
                    }
                    }
                }
                return null;
            }
        });
    }

    @Test
    public void duplicateCreatedParticipantsSizeTest()
    {
        List<DocumentRepository> list = getDocumentRepositoryList();
        expect(documentDao.findAll()).andReturn(list);
        expect(documentDao.save(list.get(0))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                assertTrue(list.get(0).getParticipants().size() == getOldParticipantList(1).size());
                return null;
            }
        });
        expect(documentDao.save(list.get(1))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                assertTrue(list.get(1).getParticipants().size() == 3);
                // additional check if match participants
                return null;
            }
        });
        expect(documentDao.save(list.get(2))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                assertTrue(list.get(2).getParticipants().size() == 0);
                return null;
            }
        });
    }

    private List<DocumentRepository> getDocumentRepositoryList()
    {
        List<DocumentRepository> list = new ArrayList<>();
        DocumentRepository docRepo = new DocumentRepository();
        docRepo.setParticipants(getOldParticipantList(1));

        DocumentRepository docRepo2 = new DocumentRepository();
        docRepo2.setParticipants(getOldParticipantList(2));

        DocumentRepository docRepo3 = new DocumentRepository();
        docRepo3.setParticipants(getOldParticipantList(3));

        list.add(docRepo);
        list.add(docRepo2);
        list.add(docRepo3);
        return list;
    }

    private List<AcmParticipant> getOldParticipantList(int type)
    {
        List<AcmParticipant> list = new ArrayList<>();
        switch (type)
        {
        case 1:
        {
            list.add(getParticipant("theReader", "reader"));
            list.add(getParticipant("theReader", "assignee"));
            list.add(getParticipant("theCoOwner", "co-owner"));
            list.add(getParticipant("theCoOwner", "reader"));
            list.add(getParticipant("theOwningGroup", "owning group"));
            break;
        }
        case 3:
        {
            list.add(getParticipant("theReader", "follower"));
            list.add(getParticipant("theReader", "collaborator"));
            list.add(getParticipant("theOwningGroup", "owning group"));
            list.add(getParticipant("theReader", "assignee"));
        }
        case 4:
        {
            list.add(getParticipant("theReader", "reader"));
            list.add(getParticipant("theCollaborator", "collaborator"));
            list.add(getParticipant("theOwningGroup", "owning group"));
            list.add(getParticipant("theFollower", "follower"));
            list.add(getParticipant("theCoOwner", "co-owner"));
            list.add(getParticipant("theAssignee", "assignee"));
        }
        }
        return list;
    }

    private AcmParticipant getParticipant(String participantLdap, String participantType)
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setParticipantType(participantType);
        participant.setParticipantLdapId(participantLdap);
        return participant;
    }
}
