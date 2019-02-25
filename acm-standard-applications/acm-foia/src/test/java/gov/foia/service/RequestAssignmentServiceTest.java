package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.utils.ParticipantUtils;
import com.armedia.acm.services.users.service.group.GroupService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * Created by teng.wang on 6/12/2017.
 */
public class RequestAssignmentServiceTest
{
    private static final String userId = "ann-acm";
    private static final String ipAddress = "192.168.56.101";
    private static final String owningGroup = "arkcase-users";
    private static final Long queueId = 1L;
    private FOIARequest oldestSelfAssignedRequest;
    private AcmParticipant selfAssignedParticipant;
    private FOIARequest oldestNotAssignedRequest;
    private AcmParticipant participant;
    private AcmParticipant owningGroupParticipant;
    private RequestAssignmentService unit;
    private Authentication mockedAuthentication = createMock(Authentication.class);
    private HttpSession mockedHttpSession = createMock(HttpSession.class);
    private SaveCaseService mockedSaveCaseService = createMock(SaveCaseService.class);
    private GroupService mockedGroupService = createMock(GroupService.class);
    private FOIARequestDao mockedRequestDao = createMock(FOIARequestDao.class);
    private Object[] mocks = { mockedHttpSession, mockedAuthentication, mockedSaveCaseService, mockedGroupService, mockedRequestDao };

    @Before
    public void setUp()
    {
        unit = new RequestAssignmentService();
        unit.setSaveCaseService(mockedSaveCaseService);
        unit.setGroupService(mockedGroupService);
        unit.setRequestDao(mockedRequestDao);

        oldestSelfAssignedRequest = new FOIARequest();
        oldestNotAssignedRequest = new FOIARequest();

        selfAssignedParticipant = new AcmParticipant();
        selfAssignedParticipant.setParticipantType("assignee");
        selfAssignedParticipant.setParticipantLdapId(userId);

        participant = new AcmParticipant();
        participant.setParticipantType("assignee");

        owningGroupParticipant = new AcmParticipant();
        owningGroupParticipant.setParticipantType("owning group");
        owningGroupParticipant.setParticipantLdapId(owningGroup);
    }

    @Test
    public void testStartWorkingReturnsOldestSelfAssignedRequest() throws Exception
    {
        participant.setParticipantLdapId("");
        oldestSelfAssignedRequest.setParticipants(Arrays.asList(selfAssignedParticipant, owningGroupParticipant));
        oldestSelfAssignedRequest.setQueueEnterDate(LocalDateTime.now().minus(1, ChronoUnit.DAYS));

        oldestNotAssignedRequest.setParticipants(Arrays.asList(participant, owningGroupParticipant));
        oldestNotAssignedRequest.setQueueEnterDate(LocalDateTime.now());

        expect(mockedRequestDao.getOldestRequestInQueueAssignedToUser(queueId, userId)).andReturn(oldestSelfAssignedRequest);

        expect(mockedRequestDao.getAllUnassignedRequestsInQueue(queueId)).andReturn(Arrays.asList(oldestNotAssignedRequest));

        expect(mockedGroupService.isUserMemberOfGroup(userId, owningGroup)).andReturn(true);

        expect(mockedAuthentication.getName()).andReturn(userId).anyTimes();

        replay(mocks);

        FOIARequest found = unit.startWorking(queueId, mockedAuthentication, mockedHttpSession);

        verify(mocks);

        assertEquals(userId, ParticipantUtils.getAssigneeIdFromParticipants(found.getParticipants()));
    }

    @Test
    public void testStartWorkingReturnsOldestNotAssignedRequest() throws Exception
    {
        participant.setParticipantLdapId("");
        oldestSelfAssignedRequest.setParticipants(Arrays.asList(selfAssignedParticipant, owningGroupParticipant));
        oldestSelfAssignedRequest.setQueueEnterDate(LocalDateTime.now());

        oldestNotAssignedRequest.setParticipants(Arrays.asList(participant, owningGroupParticipant));
        oldestNotAssignedRequest.setQueueEnterDate(LocalDateTime.now().minus(1, ChronoUnit.DAYS));

        expect(mockedRequestDao.getOldestRequestInQueueAssignedToUser(queueId, userId)).andReturn(oldestSelfAssignedRequest);

        expect(mockedRequestDao.getAllUnassignedRequestsInQueue(queueId)).andReturn(Arrays.asList(oldestNotAssignedRequest));

        expect(mockedGroupService.isUserMemberOfGroup(userId, owningGroup)).andReturn(true);

        expect(mockedAuthentication.getName()).andReturn(userId).anyTimes();

        expect(mockedHttpSession.getAttribute("acm_ip_address")).andReturn(ipAddress);

        expect(mockedSaveCaseService.saveCase(oldestNotAssignedRequest, mockedAuthentication, ipAddress))
                .andReturn(oldestNotAssignedRequest);

        replay(mocks);

        FOIARequest found = unit.startWorking(queueId, mockedAuthentication, mockedHttpSession);

        verify(mocks);

        assertEquals(userId, ParticipantUtils.getAssigneeIdFromParticipants(found.getParticipants()));
    }

    @Test
    public void testStartWorkingReturnsNullWhenAllRequestsAreAlreadyAssigned() throws Exception
    {
        expect(mockedRequestDao.getOldestRequestInQueueAssignedToUser(queueId, userId)).andReturn(null);

        expect(mockedRequestDao.getAllUnassignedRequestsInQueue(queueId)).andReturn(new ArrayList<>());

        expect(mockedAuthentication.getName()).andReturn(userId).anyTimes();

        replay(mocks);

        FOIARequest returnedRequest = unit.startWorking(queueId, mockedAuthentication, mockedHttpSession);

        verify(mocks);

        assertNull(returnedRequest);
    }

    @Test
    public void testStartWorkingReturnsNullWhenUserNotInOwningGroup() throws Exception
    {
        participant.setParticipantLdapId("");
        oldestNotAssignedRequest.setParticipants(Arrays.asList(participant, owningGroupParticipant));

        expect(mockedRequestDao.getOldestRequestInQueueAssignedToUser(queueId, userId)).andReturn(null);

        expect(mockedRequestDao.getAllUnassignedRequestsInQueue(queueId)).andReturn(Arrays.asList(oldestNotAssignedRequest));

        expect(mockedGroupService.isUserMemberOfGroup(userId, owningGroup)).andReturn(false);

        expect(mockedAuthentication.getName()).andReturn(userId).anyTimes();

        replay(mocks);

        FOIARequest returnedRequest = unit.startWorking(queueId, mockedAuthentication, mockedHttpSession);

        verify(mocks);

        assertNull(returnedRequest);
    }
}
