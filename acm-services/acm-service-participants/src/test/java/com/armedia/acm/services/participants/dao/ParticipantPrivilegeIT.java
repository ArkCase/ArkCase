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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-object-converter.xml"
})
@Rollback(true)
public class ParticipantPrivilegeIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private final Logger log = LogManager.getLogger(getClass());
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;
    private String objectType = "TEST OBJECT TYPE";
    private Long objectId = 500L;
    private String participantLdapId = "TEST ACCESSOR ID";
    private String participantType = "TEST PARTICIPANT TYPE";

    @Before
    public void setUp() throws Exception
    {
        auditAdapter.setUserId("auditUser");

        String deleteParticipant = "DELETE FROM AcmParticipant a " +
                "WHERE  a.objectType = :objectType " +
                "AND a.objectId = :objectId " +
                "AND a.participantType = :participantType " +
                "AND a.participantLdapId = :participantLdapId";
        Query deleteParticipantQuery = entityManager.createQuery(deleteParticipant);
        deleteParticipantQuery.setParameter("participantLdapId", participantLdapId);
        deleteParticipantQuery.setParameter("objectId", objectId);
        deleteParticipantQuery.setParameter("participantType", participantType);
        deleteParticipantQuery.setParameter("objectType", objectType);

        deleteParticipantQuery.executeUpdate();
    }

    @Test
    @Transactional
    public void storeParticipant()
    {
        AcmParticipant acmParticipant = new AcmParticipant();
        acmParticipant.setObjectType(objectType);
        acmParticipant.setObjectId(objectId);
        acmParticipant.setParticipantLdapId(participantLdapId);
        acmParticipant.setParticipantType(participantType);

        AcmParticipantPrivilege privilege = new AcmParticipantPrivilege();
        acmParticipant.getPrivileges().add(privilege);
        privilege.setAccessReason("reason");
        privilege.setAccessType("type");
        privilege.setObjectAction("action");

        entityManager.persist(acmParticipant);

        entityManager.flush();

        assertNotNull(acmParticipant.getId());
        assertNotNull(privilege.getId());

    }
}
