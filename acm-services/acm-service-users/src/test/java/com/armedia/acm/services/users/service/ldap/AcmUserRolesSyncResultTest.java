package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRoleState;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AcmUserRolesSyncResultTest
{
    private AcmUserRolesSyncResult unit;

    private Map<String, Set<String>> userAddedGroups;

    private Map<String, Set<String>> userRemovedGroups;

    private Map<String, Set<String>> userGroupsMap;

    private Map<String, List<String>> groupToRoleMap;

    @Before
    public void setup()
    {
        userAddedGroups = new HashMap<>();
        userAddedGroups.put("ann-acm", new HashSet<>(Arrays.asList("A", "B")));
        userRemovedGroups = new HashMap<>();
        userRemovedGroups.put("ann-acm", new HashSet<>(Arrays.asList("X")));
        groupToRoleMap = new HashMap<>();
        groupToRoleMap.put("A", Arrays.asList("ADMIN"));
        groupToRoleMap.put("B", Arrays.asList("ADMIN"));
        groupToRoleMap.put("X", Arrays.asList("READER"));
    }

    // Expected Roles per added/removed groups
    // @formatter:off
        /**
         * A -> ADMIN  -> VALID
         * B -> ADMIN  -> VALID
         * A -> A      -> VALID
         * B -> B      -> VALID
         * X -> READER -> INVALID
         * X -> X      -> INVALID
         */
        // @formatter:on
        @Test
        public void rolesPerAddedAndRemovedGroupsTest()
        {
            userGroupsMap = new HashMap<>();
            userGroupsMap.put("ann-acm", new HashSet<>(Arrays.asList("Q", "W", "X")));

            unit = new AcmUserRolesSyncResult(userAddedGroups, userRemovedGroups, groupToRoleMap, userGroupsMap);

            List<AcmUserRole> userRoles = unit.getAcmUserRoles();

            long numberInvalidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.INVALID)
                    .count();

            long numberValidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.VALID)
                    .count();

            assertThat("Should have 2 removed (INVALID) role(s)", numberInvalidRoles, is(2L));
            assertThat("Should have 3 added (VALID) role(s)", numberValidRoles, is(3L));
        }

    // Expected Roles per added/removed groups
    // @formatter:off
        /**     roleName  roleState
         * A -> ADMIN  -> VALID
         * B -> ADMIN  -> VALID
         * A -> A      -> VALID
         * B -> B      -> VALID
         * Y -> Y      -> VALID
         * X -> READER -> INVALID
         * X -> X      -> INVALID
         */
        // @formatter:on
        @Test
        public void rolesPerAddedGroupNotMappedWithRolesTest()
        {
            userAddedGroups.get("ann-acm").add("Y");
            userGroupsMap = new HashMap<>();
            userGroupsMap.put("ann-acm", new HashSet<>(Arrays.asList("Q", "W", "X")));

            unit = new AcmUserRolesSyncResult(userAddedGroups, userRemovedGroups, groupToRoleMap, userGroupsMap);

            List<AcmUserRole> userRoles = unit.getAcmUserRoles();

            long numberInvalidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.INVALID)
                    .count();

            long numberValidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.VALID)
                    .count();

            assertThat("Should have 2 removed (INVALID) role(s)", numberInvalidRoles, is(2L));
            assertThat("Should have 4 added (VALID) role(s)", numberValidRoles, is(4L));
        }

    // Expected Roles per added/removed groups
    // @formatter:off
        /**     roleName  roleState
         * A -> ADMIN  -> VALID
         * B -> ADMIN  -> VALID
         * A -> A      -> VALID
         * B -> B      -> VALID
         * Y -> Y      -> INVALID
         * X -> READER -> INVALID
         * X -> X      -> INVALID
         */
        // @formatter:on
        @Test
        public void rolesPerRemovedGroupNotMappedWithRolesTest()
        {
            userRemovedGroups.get("ann-acm").add("Y");
            userGroupsMap = new HashMap<>();
            userGroupsMap.put("ann-acm", new HashSet<>(Arrays.asList("Q", "W", "X", "Y")));

            unit = new AcmUserRolesSyncResult(userAddedGroups, userRemovedGroups, groupToRoleMap, userGroupsMap);

            List<AcmUserRole> userRoles = unit.getAcmUserRoles();

            long numberInvalidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.INVALID)
                    .count();

            long numberValidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.VALID)
                    .count();

            assertThat("Should have 3 removed (INVALID) role(s)", numberInvalidRoles, is(3L));
            assertThat("Should have 3 added (VALID) role(s)", numberValidRoles, is(3L));
        }

    // Expected Roles per added/removed groups
    // @formatter:off
        /**     roleName  roleState   userId
         * A -> ADMIN  -> VALID    -> ann-acm
         * B -> ADMIN  -> VALID    -> ann-acm
         * A -> A      -> VALID    -> ann-acm
         * B -> B      -> VALID    -> ann-acm
         * X -> READER -> INVALID  -> ann-acm
         * X -> X      -> INVALID  -> ann-acm
         * Q -> Q      -> INVALID  -> sally-acm
         */
        // @formatter:on
        @Test
        public void rolesPerRemovedGroupsGroupsToRemainNotFoundTest()
        {
            userRemovedGroups.put("sally-acm", new HashSet<>(Arrays.asList("Q")));
            userGroupsMap = new HashMap<>();

            unit = new AcmUserRolesSyncResult(userAddedGroups, userRemovedGroups, groupToRoleMap, userGroupsMap);

            List<AcmUserRole> userRoles = unit.getAcmUserRoles();

            long numberInvalidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.INVALID)
                    .count();

            long numberValidRoles = userRoles.stream()
                    .filter(userRole -> userRole.getUserRoleState() == AcmUserRoleState.VALID)
                    .count();

            assertThat("Should have 3 removed (INVALID) role(s)", numberInvalidRoles, is(3L));
            assertThat("Should have 3 added (VALID) role(s)", numberValidRoles, is(3L));

        }
}
