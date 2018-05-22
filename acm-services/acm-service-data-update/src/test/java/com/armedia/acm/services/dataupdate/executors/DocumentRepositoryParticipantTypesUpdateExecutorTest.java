package com.armedia.acm.services.dataupdate.executors;

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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.documentrepository.dao.DocumentRepositoryDao;
import com.armedia.acm.plugins.documentrepository.model.DocumentRepository;
import com.armedia.acm.services.dataupdate.service.DocumentRepositoryParticipantTypesUpdateExecutor;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.easymock.EasyMockSupport;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DocumentRepositoryParticipantTypesUpdateExecutorTest extends EasyMockSupport
{

    DocumentRepositoryDao documentDao;
    DocumentRepositoryParticipantTypesUpdateExecutor executor;
    AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    @Before
    public void setUp() throws Exception
    {
        executor = new DocumentRepositoryParticipantTypesUpdateExecutor();
        documentDao = createMock(DocumentRepositoryDao.class);
        auditPropertyEntityAdapter = new AuditPropertyEntityAdapter();
        executor.setDocumentDao(documentDao);
        executor.setAuditPropertyEntityAdapter(auditPropertyEntityAdapter);

    }

    @Test
    public void participantMapingTest()
    {
        List<DocumentRepository> list = new ArrayList<>();
        DocumentRepository docRepo = new DocumentRepository();
        docRepo.setParticipants(getOldParticipantList(4));
        list.add(docRepo);
        docRepo.setCreator("theCreator");

        expect(documentDao.findAll()).andReturn(list);
        expect(documentDao.save(list.get(0))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                for (AcmParticipant participant : list.get(0).getParticipants())
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
                        assertTrue(participant.getParticipantType().equals("collaborator"));
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
                        if (participant.getParticipantLdapId().equals("theCreator"))
                        {
                            assertTrue(participant.getParticipantType().equals("owner"));
                        }
                        else
                        {
                            assertTrue(participant.getParticipantType().equals("reader"));
                        }
                        break;
                    }
                    }
                }
                return null;
            }
        });
        replayAll();
        executor.execute();

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
                assertTrue(list.get(0).getParticipants().size() == getOldParticipantList(1).size() - 1);
                return null;
            }
        });
        expect(documentDao.save(list.get(1))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                assertTrue(list.get(1).getParticipants().size() == 0);
                // additional check if match participants
                return null;
            }
        });
        expect(documentDao.save(list.get(2))).andAnswer(new IAnswer<DocumentRepository>()
        {
            @Override
            public DocumentRepository answer() throws Throwable
            {
                assertTrue(list.get(2).getParticipants().size() == 3);
                return null;
            }
        });
        replayAll();
        executor.execute();
    }

    private List<DocumentRepository> getDocumentRepositoryList()
    {
        List<DocumentRepository> list = new ArrayList<>();

        DocumentRepository docRepo = new DocumentRepository();
        docRepo.setCreator("theCreator");
        docRepo.setParticipants(getOldParticipantList(1));

        DocumentRepository docRepo2 = new DocumentRepository();
        docRepo2.setParticipants(getOldParticipantList(2));
        docRepo2.setCreator("theCreator");

        DocumentRepository docRepo3 = new DocumentRepository();
        docRepo3.setParticipants(getOldParticipantList(3));
        docRepo3.setCreator("theCreator");

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
            break;
        }
        case 4:
        {
            list.add(getParticipant("theReader", "reader"));
            list.add(getParticipant("theCollaborator", "collaborator"));
            list.add(getParticipant("theOwningGroup", "owning group"));
            list.add(getParticipant("theFollower", "follower"));
            list.add(getParticipant("theCoOwner", "co-owner"));
            list.add(getParticipant("theAssignee", "assignee"));
            list.add(getParticipant("theCreator", "assignee"));
            break;
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
