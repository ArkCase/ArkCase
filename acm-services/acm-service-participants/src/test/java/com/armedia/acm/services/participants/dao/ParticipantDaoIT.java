package com.armedia.acm.services.participants.dao;

/*-
 * #%L
 * ACM Service: Participants
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-config-participant-service-test-beans.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class ParticipantDaoIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    private final Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;
    @Autowired
    private AcmParticipantDao dao;
    private String objectType = "TEST OBJECT TYPE";
    private Long objectId = -500L; // negative object id means we can't collide with any participant that might actually
                                   // exist

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");
    }

    @Test
    @Transactional
    public void findParticipantWithPrivilege()
    {
        AcmParticipant p = makeAcmParticipant("first", "assignee", "grant", "save");

        dao.save(p);

        entityManager.flush();

        boolean hasAccess = dao.hasObjectAccess(p.getParticipantLdapId(), objectId, objectType, "save", "grant");

        assertTrue(hasAccess);

        boolean anotherUserHasAccess = dao.hasObjectAccess("random-user", objectId, objectType, "save", "grant");

        assertFalse(anotherUserHasAccess);
    }

    @Test
    @Transactional
    public void findDefaultUserWithPrivilege()
    {
        AcmParticipant p = makeAcmParticipant("*", "assignee", "grant", "save");

        dao.save(p);

        entityManager.flush();

        boolean hasAccess = dao.hasObjectAccess("any-user", objectId, objectType, "save", "grant");

        assertTrue(hasAccess);
    }

    @Test
    @Transactional
    public void findParticipantInGroupWithPrivilege()
    {
        AcmParticipant p = makeAcmParticipant("ACM_ADMINISTRATOR_DEV", "assignee", "grant", "read");
        dao.save(p);

        entityManager.flush();

        boolean hasAccess = dao.hasObjectAccessViaGroup(
                new HashSet<>(Arrays.asList(p.getParticipantLdapId())), objectId, objectType, "read", "grant");

        assertTrue(hasAccess);
    }

    @Test
    @Transactional
    public void participants()
    {
        AcmParticipant first = makeAcmParticipant("first", "assignee", "type", "action");
        AcmParticipant second = makeAcmParticipant("second", "follower", "type", "action");

        List<AcmParticipant> participantList = Arrays.asList(first, second);

        dao.saveParticipants(participantList);

        entityManager.flush();

        List<AcmParticipant> found = dao.findParticipantsForObject(objectType, objectId);

        assertNotNull(found);
        assertEquals(participantList.size(), found.size());

        List<Long> firstBatchOfParticipantIds = new ArrayList<>();

        for (AcmParticipant ap : found)
        {
            assertNotNull(ap.getId());
            assertEquals(1, ap.getPrivileges().size());
            assertNotNull(ap.getPrivileges().get(0).getId());

            firstBatchOfParticipantIds.add(ap.getId());
        }

        AcmParticipant third = makeAcmParticipant("third", "owner", "type", "action");

        found.add(third);

        for (AcmParticipant p : found)
        {
            if (p.getParticipantLdapId().equals("second"))
            {
                p.setParticipantType("approver");
            }
        }

        dao.saveParticipants(found);

        entityManager.flush();

        List<AcmParticipant> secondRound = dao.findParticipantsForObject(objectType, objectId);

        entityManager.flush();

        assertNotNull(secondRound);
        assertEquals(found.size(), secondRound.size());

        for (AcmParticipant ap : secondRound)
        {
            assertNotNull(ap.getId());
            assertEquals(1, ap.getPrivileges().size());
            assertNotNull(ap.getPrivileges().get(0).getId());

            // ensure IDs from first round did not change
            if (!ap.getParticipantLdapId().equals("third"))
            {
                assertTrue(firstBatchOfParticipantIds.contains(ap.getId()));
            }

            if (ap.getParticipantLdapId().equals("second"))
            {
                assertEquals("approver", ap.getParticipantType());
            }

        }

        Long deletedId = secondRound.get(0).getId();

        assertEquals(3, secondRound.size());

        secondRound.remove(0);
        log.debug("Should be removed: " + deletedId);

        for (AcmParticipant f : secondRound)
        {
            log.debug("should be kept: " + f.getId());
        }

        int removed = dao.removeAllOtherParticipantsForObject(objectType, objectId, secondRound);

        entityManager.flush();

        assertEquals(1, removed);

        assertEquals(2, secondRound.size());

        dao.saveParticipants(secondRound);

        entityManager.flush();

        List<AcmParticipant> thirdRound = dao.findParticipantsForObject(objectType, objectId);

        entityManager.flush();

        assertNotNull(thirdRound);
        assertEquals(2, thirdRound.size());

        AcmParticipant deleted = dao.find(deletedId);

        assertNull(deleted);

    }

    private AcmParticipant makeAcmParticipant(String ldapId, String participantType, String accessType, String objectAction)
    {
        AcmParticipant participant = new AcmParticipant();
        participant.setObjectType(objectType);
        participant.setObjectId(objectId);
        participant.setParticipantLdapId(ldapId);
        participant.setParticipantType(participantType);

        AcmParticipantPrivilege privilege = new AcmParticipantPrivilege();
        participant.getPrivileges().add(privilege);
        privilege.setAccessReason("reason");
        privilege.setAccessType(accessType);
        privilege.setObjectAction(objectAction);

        return participant;
    }
}
