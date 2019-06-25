package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
    private final boolean fullSync;
    private final Logger log = LogManager.getLogger(getClass());
    private List<AcmUser> modifiedUsers;
    private List<AcmUser> newUsers;
    private List<AcmUser> deletedUsers;

    public AcmUsersSyncResult(boolean fullSync)
    {
        this.fullSync = fullSync;
        this.deletedUsers = new ArrayList<>();
    }

    public Map<String, AcmUser> sync(List<LdapUser> ldapUsers, List<AcmUser> acmUsers, String defaultLang)
    {
        Map<String, AcmUser> currentUsersMap = getUsersByIdMap(acmUsers);
        newUsers = findNewUsers(ldapUsers, currentUsersMap, defaultLang);
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

    private List<AcmUser> findNewUsers(List<LdapUser> ldapUsers, Map<String, AcmUser> acmUsers, String defaultLang)
    {
        Predicate<LdapUser> doesNotExist = it -> !acmUsers.containsKey(it.getUserId());
        return ldapUsers.stream()
                .filter(doesNotExist)
                .peek(it -> log.trace("New user [{}] with dn [{}] to be synced", it.getUserId(), it.getDistinguishedName()))
                .map(it -> it.toAcmUser(defaultLang))
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
