package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUserRole;
import com.armedia.acm.services.users.model.AcmUserRoleState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Synchronizes AcmUser roles
 */
public class AcmUserRolesSyncResult
{
    private final List<AcmUserRole> acmUserRoles;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static Function<String, AcmUserRole> acmUserRole(String user, AcmUserRoleState roleState)
    {
        return role -> {
            AcmUserRole acmUserRole = new AcmUserRole();
            acmUserRole.setRoleName(role);
            acmUserRole.setUserId(user);
            acmUserRole.setUserRoleState(roleState);
            return acmUserRole;
        };
    }

    public AcmUserRolesSyncResult(Map<String, List<String>> groupToRole, Map<String, Set<AcmGroup>> userGroupsMap,
                                  List<AcmUserRole> acmUserRoles)
    {
        Function<AcmGroup, Stream<String>> groupStream =
                group -> Stream.concat(Stream.of(group.getName()), group.getAscendants());

        Stream<AcmUserRole> userRoleStream = userGroupsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .flatMap(groupStream)
                        .filter(groupToRole::containsKey)
                        .flatMap(it -> groupToRole.get(it).stream())
                        .map(acmUserRole(entry.getKey(), AcmUserRoleState.VALID)));

        Stream<AcmUserRole> userRolesForGroups = userGroupsMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .flatMap(groupStream)
                        .map(acmUserRole(entry.getKey(), AcmUserRoleState.VALID)));

        Set<AcmUserRole> validUserRoles = Stream.concat(userRoleStream, userRolesForGroups)
                .collect(Collectors.toSet());

        acmUserRoles.addAll(validUserRoles);

        acmUserRoles.forEach(userRole -> {
            if (validUserRoles.contains(userRole))
            {
                userRole.setUserRoleState(AcmUserRoleState.VALID);
            } else
            {
                userRole.setUserRoleState(AcmUserRoleState.INVALID);
            }
        });

        this.acmUserRoles = acmUserRoles.stream().distinct().collect(Collectors.toList());
    }

    public List<AcmUserRole> getAcmUserRoles()
    {
        return acmUserRoles;
    }

}
