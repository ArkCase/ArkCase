package com.armedia.acm.services.dataaccess.service.impl;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.dataaccess.model.DataAccessControlConstants;
import com.armedia.acm.services.dataaccess.model.test.DataAccessAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;
import com.armedia.acm.services.participants.model.AcmParticipantPrivilege;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by armdev on 2/16/15.
 */
public class AcmPrivilegeServiceTest
{
    private AcmPrivilegeService unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new AcmPrivilegeService();
    }

    /**
     * Setting access controls should not add any participants.
     * 
     * @throws Exception
     */
    @Test
    public void setPrivileges_noSuchParticipantType() throws Exception
    {
        DataAccessAssignedObject test = new DataAccessAssignedObject();
        test.getParticipants().clear();

        String spec = "grant read to *";

        unit.setPrivileges(test, spec);

        assertTrue(test.getParticipants().isEmpty());
    }

    /**
     * grant a new privilege to an existing participant
     * 
     * @throws Exception
     */
    @Test
    public void setPrivileges_newPrivilege() throws Exception
    {
        DataAccessAssignedObject test = new DataAccessAssignedObject();
        test.getParticipants().clear();

        AcmParticipant part = new AcmParticipant();
        part.setParticipantType("type one");
        test.getParticipants().add(part);

        String spec = "grant read to type one";

        unit.setPrivileges(test, spec);

        assertFalse(test.getParticipants().isEmpty());

        assertFalse(part.getPrivileges().isEmpty());

        AcmParticipantPrivilege priv = part.getPrivileges().get(0);

        assertEquals("read", priv.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, priv.getAccessReason());
        assertEquals(DataAccessControlConstants.ACCESS_GRANT, priv.getAccessType());
    }

    /**
     * grant multiple privilege to multiple existing participants
     * 
     * @throws Exception
     */
    @Test
    public void setPrivileges_multipleParticipantTypes() throws Exception
    {
        DataAccessAssignedObject test = new DataAccessAssignedObject();
        test.getParticipants().clear();

        AcmParticipant part = new AcmParticipant();
        part.setParticipantType("type one");
        test.getParticipants().add(part);

        AcmParticipant partTwo = new AcmParticipant();
        partTwo.setParticipantType("type two");
        test.getParticipants().add(partTwo);

        String spec = "grant read to type one, type two";

        unit.setPrivileges(test, spec);

        assertFalse(test.getParticipants().isEmpty());

        assertFalse(part.getPrivileges().isEmpty());
        assertFalse(partTwo.getPrivileges().isEmpty());

        AcmParticipantPrivilege privOne = part.getPrivileges().get(0);

        assertEquals("read", privOne.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, privOne.getAccessReason());
        assertEquals(DataAccessControlConstants.ACCESS_GRANT, privOne.getAccessType());

        AcmParticipantPrivilege privTwo = partTwo.getPrivileges().get(0);

        assertEquals("read", privTwo.getObjectAction());
        assertEquals(DataAccessControlConstants.ACCESS_REASON_POLICY, privTwo.getAccessReason());
        assertEquals(DataAccessControlConstants.ACCESS_GRANT, privTwo.getAccessType());
    }
}
