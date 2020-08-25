package com.armedia.acm.services.dataaccess.model.test;

/*-
 * #%L
 * ACM Service: Data Access Control
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.dataaccess.annotations.DecoratedAssignedObjectParticipantAspect;
import com.armedia.acm.services.dataaccess.service.impl.AcmAssignedObjectBusinessRule;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.aspectj.lang.ProceedingJoinPoint;
import org.easymock.EasyMockRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(EasyMockRunner.class)
public class DecoratedParticipantAnnotationTest
{

    private AcmDataService springAcmDataService;
    private AcmAssignedObjectBusinessRule assignmentBusinessRule;
    private DecoratedAssignedObjectParticipantAspect decoratedParticipantAspect;
    private DataAccessDao dataAccessDao;

    private ProceedingJoinPoint pjp;
    private DecoratedAssignedObjectParticipants decoratedAssignedObjectParticipants;

    // Additional Assignable Object Arguments;
    private String objectType = "DATA-ACCESS-TEST";
    private Long objectId = 101L;

    @Before
    public void setUp() throws Throwable
    {
        springAcmDataService = createMock(AcmDataService.class);
        dataAccessDao = createMock(DataAccessDao.class);

        assignmentBusinessRule = new AcmAssignedObjectBusinessRule();
        assignmentBusinessRule.setFileConfigurationService(new FileConfigurationService()
        {
            @Override
            public void moveFileToConfiguration(InputStreamResource file, String fileName) throws IOException
            {
            }

            @Override
            public void getFileFromConfiguration(String fileName, String customFilesLocation) throws IOException
            {
            }

            @Override
            public InputStream getInputStreamFromConfiguration(String filePath) throws IOException
            {
                return new FileInputStream(new ClassPathResource("/" + filePath).getFile().getCanonicalPath());
            }
        });
        decoratedParticipantAspect = new DecoratedAssignedObjectParticipantAspect();
        decoratedAssignedObjectParticipants = createMock(DecoratedAssignedObjectParticipants.class);
        pjp = createMock(ProceedingJoinPoint.class);
        // expect(pjp.proceed()).andReturn(new DataAccessAssignedObject());
        // DataAccessAssignedObject test = (DataAccessAssignedObject) pjp.proceed();
        // Object objecttest = pjp.proceed();
        assignmentBusinessRule.setRuleSpreadsheetFilename("drools-assigned-object-test-rules.xlsx");
        assignmentBusinessRule.afterPropertiesSet();
        decoratedParticipantAspect.setSpringAcmDataService(springAcmDataService);
        decoratedParticipantAspect.setAssignmentBusinessRule(assignmentBusinessRule);

    }

    @Test
    public void assignableObjectDecorationWithoutParticipantsTest() throws Throwable
    {
        DataAccessAssignedObject testObject = new DataAccessAssignedObject();
        expect(pjp.proceed()).andReturn(testObject);
        replay(pjp);
        DataAccessAssignedObject decorated = (DataAccessAssignedObject) decoratedParticipantAspect.aroundDecoratingMethod(pjp,
                decoratedAssignedObjectParticipants);

        Assert.assertEquals(decorated.getParticipants().size(), 0);
    }

    @Test
    public void assignableObjectDecorationWithParticipantsTest() throws Throwable
    {
        DataAccessAssignedObject testObject = new DataAccessAssignedObject();
        testObject.setParticipants(getDefaultParticipants());
        testObject.setStatus("DRAFT");
        expect(pjp.proceed()).andReturn(testObject);
        replay(pjp);
        DataAccessAssignedObject decorated = (DataAccessAssignedObject) decoratedParticipantAspect.aroundDecoratingMethod(pjp,
                decoratedAssignedObjectParticipants);

        validateParticipants(decorated.getParticipants(), decorated.getCreator());

    }

    @Test
    public void participantsListDecorationTest() throws Throwable
    {
        DataAccessAssignedObject testObject = new DataAccessAssignedObject();
        List<AcmParticipant> participants = getDefaultParticipants();
        testObject.setParticipants(participants);
        setupTestObjectReturnMethod(testObject, participants);
        List<AcmParticipant> decorated = (List<AcmParticipant>) decoratedParticipantAspect.aroundDecoratingMethod(pjp,
                decoratedAssignedObjectParticipants);

        validateParticipants(decorated, testObject.getCreator());

    }

    @Test
    public void participantsListDecorationWithoutParticipantsTest() throws Throwable
    {
        DataAccessAssignedObject testObject = new DataAccessAssignedObject();
        List<AcmParticipant> participants = new ArrayList<>();
        testObject.setParticipants(participants);
        setupTestObjectReturnMethod(testObject, participants);
        List<AcmParticipant> decorated = (List<AcmParticipant>) decoratedParticipantAspect.aroundDecoratingMethod(pjp,
                decoratedAssignedObjectParticipants);

        Assert.assertEquals(decorated.size(), 0);
    }

    @Test
    public void participantsListDecorationWithInvalidParentObject() throws Throwable
    {
        List<AcmParticipant> participants = getDefaultParticipants();
        setupTestObjectReturnMethod(null, participants);
        List<AcmParticipant> decorated = (List<AcmParticipant>) decoratedParticipantAspect.aroundDecoratingMethod(pjp,
                decoratedAssignedObjectParticipants);

        Assert.assertEquals(decorated.size(), participants.size());
    }

    private void setupTestObjectReturnMethod(DataAccessAssignedObject object, List<AcmParticipant> participants) throws Throwable
    {
        Object[] args = new Object[2];
        args[0] = this.objectType;
        args[1] = this.objectId;
        expect(pjp.proceed()).andReturn(participants);
        expect(pjp.getArgs()).andReturn(args);
        replay(pjp);
        expect(decoratedAssignedObjectParticipants.objectTypeIndex()).andReturn(0);
        expect(decoratedAssignedObjectParticipants.objectIdIndex()).andReturn(1);
        expect(decoratedAssignedObjectParticipants.objectId()).andReturn(-1);
        expect(decoratedAssignedObjectParticipants.objectType()).andReturn("");
        replay(decoratedAssignedObjectParticipants);
        expect(dataAccessDao.find(this.objectId)).andReturn(object);
        replay(dataAccessDao);
        expect(springAcmDataService.getDaoByObjectType(this.objectType)).andReturn(dataAccessDao);
        replay(springAcmDataService);
    }

    private void validateParticipants(List<AcmParticipant> participants, String creator)
    {
        for (int i = 0; i < participants.size(); i++)
        {
            AcmParticipant participant = participants.get(i);
            if (participant.getParticipantType().equals("*"))
            {
                // Participant with type * should only have editable user
                Assert.assertTrue(participant.isEditableUser());
                Assert.assertFalse(participant.isEditableType());
                Assert.assertFalse(participant.isDeletable());
            }
            else if (participant.getParticipantType().equals("reader") && participant.getParticipantLdapId().equals(creator))
            {
                // Participant reader that is creator cannot be deleted or edited
                Assert.assertFalse(participant.isDeletable());
                Assert.assertFalse(participant.isEditableType());
                Assert.assertFalse(participant.isEditableUser());
            }
            else if (participant.getParticipantType().equals("owning group"))
            {
                // Participan owning group can only change its user
                Assert.assertFalse(participant.isDeletable());
                Assert.assertFalse(participant.isEditableType());
                Assert.assertTrue(participant.isEditableUser());
            }
        }
    }

    private List<AcmParticipant> getDefaultParticipants()
    {
        List<AcmParticipant> participants = new ArrayList<>();

        AcmParticipant participantStar = new AcmParticipant();
        participantStar.setId(1L);
        participantStar.setParticipantLdapId(UUID.randomUUID().toString());
        participantStar.setParticipantType("*");
        participantStar.setObjectType(this.objectType);
        participantStar.setObjectId(this.objectId);
        participants.add(participantStar);

        AcmParticipant participantOwningGroup = new AcmParticipant();
        participantOwningGroup.setId(2L);
        participantOwningGroup.setParticipantLdapId(UUID.randomUUID().toString());
        participantOwningGroup.setParticipantType("owning group");
        participantOwningGroup.setObjectType(this.objectType);
        participantOwningGroup.setObjectId(this.objectId);
        participants.add(participantOwningGroup);

        AcmParticipant participantReader = new AcmParticipant();
        participantReader.setId(3L);
        participantReader.setParticipantLdapId("TEST-CREATOR");
        participantReader.setObjectType(this.objectType);
        participantReader.setParticipantType("reader");
        participantReader.setObjectId(this.objectId);
        participants.add(participantReader);

        return participants;
    }

}
