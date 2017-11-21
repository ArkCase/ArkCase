package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRoleState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.Every.everyItem;

public class AcmUserRolesSyncResultTest
{
    private AcmUserRolesSyncResult unit;

    private Map<String, Set<AcmGroup>> userGroupsMap;

    private Map<String, List<String>> groupToRoleMap;

    private List<AcmUserRole> userRolesPerUser;

    @Before
    public void setup()
    {
        groupToRoleMap = new HashMap<>();
        groupToRoleMap.put("A", Arrays.asList("ADMIN"));
        groupToRoleMap.put("B", Arrays.asList("ADMIN"));
        groupToRoleMap.put("X", Arrays.asList("READER"));
        userRolesPerUser = new ArrayList<>();
    }

    // Expected Roles per added/removed groups
    // @formatter:off
        /**
         * user       group  ascendants
         *
         * ann-acm -> Q      "A,B"
         *            W
         *            X
         */
        // @formatter:on
        @Test
        public void collectRolesPerGroupsTest()
        {
            AcmUserRole userRole1 = new AcmUserRole();
            userRole1.setUserId("ann-acm");
            userRole1.setRoleName("SUPERVISOR");
            userRole1.setUserRoleState(AcmUserRoleState.VALID);
            userRolesPerUser.add(userRole1);
            AcmUserRole userRole2 = new AcmUserRole();
            userRole2.setUserId("ann-acm");
            userRole2.setRoleName("READER");
            userRole2.setUserRoleState(AcmUserRoleState.INVALID);
            userRolesPerUser.add(userRole2);

            userGroupsMap = new HashMap<>();
            userGroupsMap.put("ann-acm", new HashSet<>(Arrays.asList(acmGroup("Q", "A,B"),
                    acmGroup("W", null), acmGroup("X", null))));

            unit = new AcmUserRolesSyncResult(groupToRoleMap, userGroupsMap, userRolesPerUser);

            List<AcmUserRole> userRoles = unit.getAcmUserRoles();

            List<String> actualRoles = userRoles.stream()
                    .map(AcmUserRole::getRoleName)
                    .collect(Collectors.toList());

            Set<AcmUserRole> invalidUserRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.INVALID)
                    .collect(Collectors.toSet());

            assertThat(userRoles.size(), is(8));
            assertThat("Invalid user roles should be:", invalidUserRoles,
                    everyItem(isIn(new HashSet<>(Arrays.asList(userRole1)))));
            assertThat(actualRoles, everyItem(isIn(Arrays.asList("Q", "W", "X", "A", "B", "ADMIN", "READER", "SUPERVISOR"))));
        }

    private AcmGroup acmGroup(String name, String ascendants)
    {
        AcmGroup acmGroup = new AcmGroup();
        acmGroup.setName(name);
        acmGroup.setAscendantsList(ascendants);
        return acmGroup;
    }
}
