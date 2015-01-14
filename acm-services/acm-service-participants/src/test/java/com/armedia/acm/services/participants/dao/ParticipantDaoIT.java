package com.armedia.acm.services.participants.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-particpants.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class ParticipantDaoIT
{
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Autowired
    private AcmParticipantDao dao;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private String objectType = "TEST OBJECT TYPE";
    private Long objectId = -500L;  // negative object id means we can't collide with any participant that might actually exist

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void participants()
    {
        AcmParticipant first = makeAcmParticipant("first", "assignee");
        AcmParticipant second = makeAcmParticipant("second", "follower");

        List<AcmParticipant> participantList = Arrays.asList(first, second);

        dao.saveParticipants(participantList);

        entityManager.flush();

        List<AcmParticipant> found = dao.findParticipantsForObjectId(objectId);

        assertNotNull(found);
        assertEquals(participantList.size(), found.size());

        List<Long> firstBatchOfParticipantIds = new ArrayList<>();

        for ( AcmParticipant ap : found )
        {
            assertNotNull(ap.getId());
            assertEquals(1, ap.getPrivileges().size());
            assertNotNull(ap.getPrivileges().get(0).getId());

            firstBatchOfParticipantIds.add(ap.getId());
        }

        AcmParticipant third = makeAcmParticipant("third", "owner");

        found.add(third);

        second.setParticipantType("approver");

        dao.saveParticipants(found);

        entityManager.flush();

        List<AcmParticipant> secondRound = dao.findParticipantsForObjectId(objectId);

        entityManager.flush();

        assertNotNull(secondRound);
        assertEquals(found.size(), secondRound.size());

        for ( AcmParticipant ap : secondRound )
        {
            assertNotNull(ap.getId());
            assertEquals(1, ap.getPrivileges().size());
            assertNotNull(ap.getPrivileges().get(0).getId());

            // ensure IDs from first round did not change
            if ( ! ap.getParticipantLdapId().equals("third") )
            {
                assertTrue(firstBatchOfParticipantIds.contains(ap.getId()) );
            }

            if ( ap.getParticipantLdapId().equals("second") )
            {
                assertEquals("approver", ap.getParticipantType());
            }

        }

        Long deletedId = found.get(0).getId();

        found.remove(0);

        int removed = dao.removeAllOtherParticipantsForObjectId(objectId, found);

        entityManager.flush();

        assertEquals(1, removed);

        assertEquals(2, found.size());

        dao.saveParticipants(found);

        entityManager.flush();

        List<AcmParticipant> thirdRound = dao.findParticipantsForObjectId(objectId);

        entityManager.flush();

        assertNotNull(thirdRound);
        assertEquals(2, thirdRound.size());

        AcmParticipant deleted = dao.find(deletedId);

        assertNull(deleted);

    }

    private AcmParticipant makeAcmParticipant(String ldapId, String participantType)
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(objectType);
        participant.setObjectId(objectId);
        participant.setParticipantLdapId(ldapId);
        participant.setParticipantType(participantType);

        AcmParticipantPrivilege privilege = new AcmParticipantPrivilege();
        participant.getPrivileges().add(privilege);
        privilege.setAccessReason("reason");
        privilege.setAccessType("type");
        privilege.setObjectAction("action");

        return participant;
    }
}
