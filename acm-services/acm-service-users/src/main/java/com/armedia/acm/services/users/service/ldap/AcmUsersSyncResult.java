package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Synchronizes LDAP users with current AcmUsers
 */
public class AcmUsersSyncResult
{
    private List<AcmUser> modifiedUsers;
    private List<AcmUser> newUsers;
    private List<AcmUser> deletedUsers;
    private final boolean fullSync;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public AcmUsersSyncResult(boolean fullSync)
    {
        this.fullSync = fullSync;
        this.deletedUsers = new ArrayList<>();
    }

    public Map<String, AcmUser> sync(List<LdapUser> ldapUsers, List<AcmUser> acmUsers)
    {
        Map<String, AcmUser> currentUsersMap = getUsersByIdMap(acmUsers);
        newUsers = findNewUsers(ldapUsers, currentUsersMap);
        log.debug("[{}] new users to be synced", newUsers.size());
        modifiedUsers = findModifiedUsers(ldapUsers, currentUsersMap);
        log.debug("[{}] modified users to be synced", modifiedUsers.size());
        newUsers.forEach(acmUser -> currentUsersMap.put(acmUser.getUserId(), acmUser));
        if (fullSync)
        {
            deletedUsers = findDeletedUsers(ldapUsers, acmUsers);
            log.debug("[{}] deleted users to be synced", deletedUsers.size());
        }
        return currentUsersMap;
    }

    public Map<String, AcmUser> getUsersByIdMap(List<AcmUser> users)
    {
        return users.stream()
                .collect(Collectors.toMap(AcmUser::getUserId, Function.identity()));
    }

    private List<AcmUser> findNewUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> acmUsers)
    {
        Predicate<LdapUser> doesNotExist = it -> !acmUsers.containsKey(it.getUserId());
        return ldapUsers.stream()
                .filter(doesNotExist)
                .peek(it -> log.trace("New user [{}] with dn [{}] to be synced", it.getUserId(), it.getDistinguishedName()))
                .map(LdapUser::toAcmUser)
                .collect(Collectors.toList());
    }

    private List<AcmUser> findModifiedUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> acmUsers)
    {
        Predicate<LdapUser> userExist = user -> acmUsers.containsKey(user.getUserId());
        Predicate<Pair<LdapUser, AcmUser>> userIsModified = it -> it.getLeft().isChanged(it.getRight());
        return ldapUsers.stream()
                .filter(userExist)
                .map(it -> new ImmutablePair<>(it, acmUsers.get(it.getUserId())))
                .filter(userIsModified)
                .map(it -> {
                    AcmUser acmUser = it.right;
                    LdapUser ldapUser = it.left;
                    log.trace("Modified user [{}] with dn [{}] to be updated",
                            ldapUser.getUserId(), ldapUser.getDistinguishedName());
                    return ldapUser.setAcmUserEditableFields(acmUser);
                })
                .collect(Collectors.toList());
    }

    private List<AcmUser> findDeletedUsers(List<LdapUser> ldapUsers, List<AcmUser> acmUsers)
    {
        Set<String> ldapUserIds = ldapUsers.stream()
                .map(LdapUser::getUserId)
                .collect(Collectors.toSet());

        Predicate<AcmUser> notInLdap = it -> !ldapUserIds.contains(it.getUserId());
        Predicate<AcmUser> valid = it -> it.getUserState() == AcmUserState.VALID;

        List<AcmUser> deletedUsers = acmUsers.stream()
                .filter(notInLdap.and(valid))
                .collect(Collectors.toList());

        deletedUsers.forEach(it -> {
            log.trace("Deleted user [{}] with dn [{}] to be updated", it.getUserId(), it.getDistinguishedName());
            it.setUserState(AcmUserState.INVALID);
        });
        return deletedUsers;
    }

    public List<AcmUser> getModifiedUsers()
    {
        return modifiedUsers;
    }

    public List<AcmUser> getNewUsers()
    {
        return newUsers;
    }

    public List<AcmUser> getDeletedUsers()
    {
        return deletedUsers;
    }
}
