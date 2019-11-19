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

import com.armedia.acm.plugins.admin.model.PrivilegeItem;
import com.armedia.acm.plugins.admin.service.RolesPrivilegesService;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolesPrivilegesServiceTest extends EasyMockSupport
{
    private Map<String, Object> mockPrivileges;
    private List<PrivilegeItem> expectedValue;
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
        expectedValue = new ArrayList<>();
        mockPrivileges.forEach((key, value) -> {
            PrivilegeItem privilegeItem = new PrivilegeItem();
            privilegeItem.setKey(key);
            privilegeItem.setValue((String) value);
            expectedValue.add(privilegeItem);
        });
        Collections.sort(expectedValue);

        // when
        List<PrivilegeItem> listToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 7, "");

        // then
        assertTrue(listToVerify.equals(expectedValue));
    }

    @Test
    public void validatePrivilegesSortDirectionDESC()
    { // given
        expectedValue = new ArrayList<>();
        mockPrivileges.forEach((key, value) -> {
            PrivilegeItem privilegeItem = new PrivilegeItem();
            privilegeItem.setKey(key);
            privilegeItem.setValue((String) value);
            expectedValue.add(privilegeItem);
        });
        Collections.sort(expectedValue, Collections.reverseOrder());

        // when
        List<PrivilegeItem> listToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "DESC", 0, 7, "");

        // then
        assertTrue(expectedValue.equals(listToVerify));
    }

    @Test
    public void validatePrivilegesStartMaxRows()
    {
        // given
        expectedValue = new ArrayList<>();
        expectedValue.add(new PrivilegeItem("acmCaseApprovePrivilege", "Case Approve Privilege"));
        expectedValue.add(new PrivilegeItem("acmCategoryManagementPrivilege", "Category Management Privilege"));
        expectedValue.add(new PrivilegeItem("acmComplaintCreatePrivilege", "Complaint Create Privilege"));

        // when
        List<PrivilegeItem> listToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 3, "");

        // then
        assertTrue(expectedValue.equals(listToVerify));
    }

    @Test
    public void validatePrivilegesFilterQuery()
    {
        // given
        expectedValue = new ArrayList<>();
        expectedValue.add(new PrivilegeItem("dashboardPrivilege", "Dashboard Privilege"));

        // when
        List<PrivilegeItem> listToVerify = rolesPrivilegesService.getPrivilegesPaged(mockPrivileges, "ASC", 0, 7, "da");

        // then
        assertTrue(expectedValue.equals(listToVerify));
    }

}
