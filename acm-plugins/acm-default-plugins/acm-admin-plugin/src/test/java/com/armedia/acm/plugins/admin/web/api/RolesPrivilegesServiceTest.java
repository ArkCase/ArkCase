package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RolesPrivilegesServiceTest extends EasyMockSupport
{
    private Map<String, String> mockPrivileges;
    private Map<String, String> expectedValue;
    private RolesPrivilegesService rolesPrivilegesService;

    @Before
    public void setUp() throws Exception
    {
        rolesPrivilegesService = new RolesPrivilegesService();
        mockPrivileges = new HashMap<>();

        mockPrivileges.put("acmCategoryManagementPrivilege", "Category Management Privilege");
        mockPrivileges.put("dashboardPrivilege", "Dashboard Privilege");
        mockPrivileges.put("listLocksDetailsPrivilege", "List Lock Details Privilege");
        mockPrivileges.put("acmOrganizationAssociationSavePrivilege", "Organization Association Save Privilege");
        mockPrivileges.put("acmCaseApprovePrivilege", "Case Approve Privilege");
        mockPrivileges.put("acmPersonListPrivilege", "Person List Privilege");
        mockPrivileges.put("acmComplaintCreatePrivilege", "Complaint Create Privilege");
    }

    @Test
    public void validatePrivilegesSortDirectionASC() throws Exception
    {
        // given
        expectedValue = new TreeMap<>();
        mockPrivileges.forEach((key, value) -> expectedValue.put(value, key));

        // when
        Map<String, String> mapToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 7, "");

        // then
        assertTrue(mapToVerify.equals(expectedValue));
    }

    @Test
    public void validatePrivilegesSortDirectionDESC() throws Exception
    { // given
        expectedValue = new TreeMap<>((o1, o2) -> o2.toLowerCase().compareTo(o1.toLowerCase()));
        mockPrivileges.forEach((key, value) -> expectedValue.put(value, key));
        // when
        Map<String, String> mapToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "DESC", 0, 7, "");

        // then
        assertTrue(expectedValue.keySet().toString().equals(mapToVerify.keySet().toString()));
    }

    @Test
    public void validatePrivilegesStartMaxRows() throws Exception
    {
        // given
        expectedValue = new TreeMap<>();
        expectedValue.put("Case Approve Privilege", "acmCaseApprovePrivilege");
        expectedValue.put("Category Management Privilege", "acmCategoryManagementPrivilege");
        expectedValue.put("Complaint Create Privilege", "acmComplaintCreatePrivilege");

        // when
        Map<String, String> mapToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 3, "");

        // then
        assertTrue(expectedValue.keySet().toString().equals(mapToVerify.keySet().toString()));
    }

    @Test
    public void validatePrivilegesFilterQuery() throws Exception
    {
        // given
        expectedValue = new TreeMap<>();
        expectedValue.put("Dashboard Privilege", "dashboardPrivilege");

        // when
        Map<String, String> mapToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 7, "da");

        // then
        assertTrue(expectedValue.keySet().toString().equals(mapToVerify.keySet().toString()));
    }

}
