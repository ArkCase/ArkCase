package gov.foia.service;

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
        oldestSelfAssignedRequest.setQueueEnterDate(LocalDate.now().minus(1, ChronoUnit.DAYS));

        oldestNotAssignedRequest.setParticipants(Arrays.asList(participant, owningGroupParticipant));
        oldestNotAssignedRequest.setQueueEnterDate(LocalDate.now());

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
        oldestSelfAssignedRequest.setQueueEnterDate(LocalDate.now());

        oldestNotAssignedRequest.setParticipants(Arrays.asList(participant, owningGroupParticipant));
        oldestNotAssignedRequest.setQueueEnterDate(LocalDate.now().minus(1, ChronoUnit.DAYS));

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
