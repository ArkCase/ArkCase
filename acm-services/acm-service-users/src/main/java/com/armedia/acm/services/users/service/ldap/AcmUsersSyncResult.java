package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Map<String, AcmUser> sync(List<LdapUser> ldapUsers, List<AcmUser> currentUsers)
    {
        Map<String, AcmUser> currentUsersMap = getUsersByIdMap(currentUsers);
        newUsers = findNewUsers(ldapUsers, currentUsersMap);
        log.debug("[{}] new users to be synced", newUsers.size());
        changedUsers = findModifiedUsers(ldapUsers, currentUsersMap);
        log.debug("[{}] modified users to be synced", changedUsers.size());
        newUsers.forEach(acmUser -> currentUsersMap.put(acmUser.getUserId(), acmUser));
        return currentUsersMap;
    }

    public Map<String, AcmUser> getUsersByIdMap(List<AcmUser> users)
    {
        return users.stream()
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));
    }

    private List<AcmUser> findNewUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> currentUsersMap)
    {
        return ldapUsers.stream()
                .filter(it -> !currentUsersMap.containsKey(it.getUserId()))
                .peek(it -> log.trace("New user [{}] with dn [{}] to be synced", it.getUserId(), it.getDistinguishedName()))
                .map(LdapUser::toAcmUser)
                .collect(Collectors.toList());
    }

    private List<AcmUser> findModifiedUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> currentUsersMap)
    {
        return ldapUsers.stream()
                .filter(it -> currentUsersMap.containsKey(it.getUserId()))
                .filter(it -> it.isChanged(currentUsersMap.get(it.getUserId())))
                .map(it -> {
                    log.trace("Modified user [{}] with dn [{}] to be updated", it.getUserId(), it.getDistinguishedName());
                    return it.setAcmUserEditableFields(currentUsersMap.get(it.getUserId()));
                })
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
