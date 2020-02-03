package com.armedia.acm.services.functionalaccess.web.api;

/*-
 * #%L
 * ACM Service: Functional Access Control
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessServiceImpl;
import com.armedia.acm.services.users.model.ApplicationRolesToPrivilegesConfig;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionalAccessServiceTest extends EasyMockSupport
{

    private final String roleAdd = "role_add";
    private final String roleAdmin = "role_admin";
    private final String wildCardRole = "ADMIN@*";
    private final String privilegeAdd = "add";

    private FunctionalAccessServiceImpl unit;
    private ApplicationRolesToPrivilegesConfig mockRolesToPrivilegesConfig;

    @Before
    public void setUp()
    {
        unit = new FunctionalAccessServiceImpl();
        mockRolesToPrivilegesConfig = new ApplicationRolesToPrivilegesConfig();

        unit.setRolesToPrivilegesConfig(mockRolesToPrivilegesConfig);
    }

    @Test
    public void getRolesForPrivilege()
    {
        Map<String, List<Object>> rolesToPrivileges = new HashMap<>();
        rolesToPrivileges.put(roleAdd, Arrays.asList(privilegeAdd));
        rolesToPrivileges.put(roleAdmin, Arrays.asList(privilegeAdd));
        rolesToPrivileges.put(wildCardRole, Arrays.asList(privilegeAdd));

        mockRolesToPrivilegesConfig.setRolesToPrivileges(rolesToPrivileges);

        List<String> roles = unit.getRolesByPrivilege(privilegeAdd);

        assertEquals(3, roles.size());

    }

}
