package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.users.model.AcmRoleToGroupMapping;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author millerd
 */
public class AcmGrantedAuthoritiesMapperTest extends EasyMockSupport
{
    private AcmGrantedAuthoritiesMapper unit;
    private AcmRoleToGroupMapping roleToGroupMappingMock;

    @Before
    public void setUp()
    {
        unit = new AcmGrantedAuthoritiesMapper();
        roleToGroupMappingMock = createMock(AcmRoleToGroupMapping.class);
        unit.setRoleToGroupMapping(roleToGroupMappingMock);
    }

    /**
     * mapAuthorities should include each role associated to each group
     * the user has.
     */
    @Test
    public void mapAuthorities()
    {
        Map<String, List<String>> groupsToRoles = new HashMap<>();

        List<String> roles1 = new ArrayList<>();
        roles1.add("ROLE_INVESTIGATOR");
        roles1.add("ROLE_INVESTIGATOR_SUPERVISOR");

        groupsToRoles.put("GROUP1", roles1);

        List<String> roles2 = new ArrayList<>();
        roles2.add("ROLE_ANALYST");

        groupsToRoles.put("GROUP2", roles2);

        GrantedAuthority auth1 = new AcmGrantedAuthority("GROUP1");

        GrantedAuthority auth2 = new AcmGrantedAuthority("GROUP2");

        GrantedAuthority authNotMapped = new AcmGrantedAuthority("NONE");

        List<GrantedAuthority> auths = Arrays.asList(auth1, auth2, authNotMapped);

        expect(roleToGroupMappingMock.getGroupToRolesMap()).andReturn(groupsToRoles).anyTimes();

        replayAll();

        Collection<? extends GrantedAuthority> found = unit.mapAuthorities(auths);

        verifyAll();
        assertEquals(3, found.size());
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_INVESTIGATOR")));
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_INVESTIGATOR_SUPERVISOR")));
        assertTrue(found.contains(new AcmGrantedAuthority("ROLE_ANALYST")));
    }

}
