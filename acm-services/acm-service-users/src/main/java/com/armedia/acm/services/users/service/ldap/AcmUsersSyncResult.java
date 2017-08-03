package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapUser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Synchronizes LDAP users with current AcmUsers
 */
public class AcmUsersSyncResult
{
    private List<AcmUser> changedUsers;
    private List<AcmUser> newUsers;

    public Map<String, AcmUser> sync(List<LdapUser> ldapUsers, List<AcmUser> currentUsers)
    {
        final Map<String, AcmUser> currentUsersMap = getUsersMap(currentUsers);
        newUsers = findNewUsers(ldapUsers, currentUsersMap);
        changedUsers = findModifiedUsers(ldapUsers, currentUsersMap);
        newUsers.forEach(acmUser -> currentUsersMap.put(acmUser.getUserId(), acmUser));
        return currentUsersMap;
    }

    private Map<String, AcmUser> getUsersMap(List<AcmUser> users)
    {
        return users.stream()
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));
    }

    private List<AcmUser> findNewUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> currentUsersMap)
    {
        return ldapUsers.stream()
                .filter(it -> !currentUsersMap.containsKey(it.getUserId()))
                .map(LdapUser::toAcmUser)
                .collect(Collectors.toList());
    }

    private List<AcmUser> findModifiedUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> currentUsersMap)
    {
        return ldapUsers.stream()
                .filter(it -> currentUsersMap.containsKey(it.getUserId()))
                .filter(it -> it.isChanged(currentUsersMap.get(it.getUserId())))
                .map(it -> it.setAcmUserEditableFields(currentUsersMap.get(it.getUserId())))
                .collect(Collectors.toList());
    }

    public List<AcmUser> getChangedUsers()
    {
        return changedUsers;
    }

    public List<AcmUser> getNewUsers()
    {
        return newUsers;
    }

}
