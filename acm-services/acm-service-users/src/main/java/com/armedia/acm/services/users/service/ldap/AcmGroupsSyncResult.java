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
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;
import com.armedia.acm.services.users.service.group.AcmGroupUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Synchronizes LDAP groups with current AcmGroup groups
 */
public class AcmGroupsSyncResult
{
    private final Logger log = LogManager.getLogger(getClass());
    private List<AcmGroup> newGroups;
    private List<AcmGroup> modifiedGroups;
    private List<AcmGroup> deletedGroups;

    public AcmGroupsSyncResult()
    {
        this.deletedGroups = new ArrayList<>();
    }

    public void sync(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups, Map<String, AcmUser> syncedUsers)
    {
        Map<String, AcmUser> syncedUsersByDn = getUsersByDnMap(syncedUsers);

        setUserAndGroupsFromGroupMembers(ldapGroups, syncedUsersByDn);
        setAscendants(ldapGroups);

        Map<String, AcmGroup> acmGroupsByName = getGroupsByNameMap(acmGroups);

        newGroups = findAndCreateNewGroups(ldapGroups, acmGroupsByName);
        log.debug("[{}] new groups to be synced", newGroups.size());
        modifiedGroups = findAndUpdateModifiedGroups(ldapGroups, acmGroupsByName);
        log.debug("[{}] modified groups to be synced", modifiedGroups.size());

        Map<String, LdapGroup> ldapGroupsByName = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getName, Function.identity()));

        Map<String, AcmGroup> modifiedGroupsByName = getGroupsByNameMap(modifiedGroups);

        addAndRemoveUserMembershipForExistingGroups(ldapGroups, syncedUsersByDn, acmGroupsByName, modifiedGroupsByName);
        addUserMembershipForNewGroups(ldapGroupsByName, syncedUsersByDn);

        // now acmGroupsByName map can include new groups
        newGroups.forEach(acmGroup -> acmGroupsByName.put(acmGroup.getName(), acmGroup));
        addGroupMembershipForNewGroups(ldapGroupsByName, acmGroupsByName, modifiedGroupsByName);
        addAndRemoveGroupMembershipForExistingGroups(ldapGroupsByName, acmGroupsByName, modifiedGroupsByName);

        modifiedGroups.forEach(acmGroup -> acmGroupsByName.put(acmGroup.getName(), acmGroup));

        deletedGroups = findDeletedGroups(ldapGroups, acmGroups);
        log.debug("[{}] deleted groups to be synced", deletedGroups.size());
        removeDeletedGroupsUserMembership(deletedGroups);
        removeDeletedGroupsGroupMembership(deletedGroups, modifiedGroupsByName, acmGroupsByName, ldapGroupsByName);

        // changedGroupsMap has been updated
        modifiedGroups = new ArrayList<>(modifiedGroupsByName.values());
    }

    public void setAscendants(List<LdapGroup> ldapGroups)
    {
        ldapGroups.forEach(ldapGroup -> {
            Set<LdapGroup> ascendants = new LdapGroupUtils()
                    .findAscendantsForLdapGroupNode(new LdapGroupNode(ldapGroup), new HashSet<>(ldapGroups));
            log.trace("Ascendants string list for group [{}] is [{}]", ldapGroup.getName(), ascendants);
            ldapGroup.setAscendants(ascendants);
        });
    }

    private void setUserAndGroupsFromGroupMembers(List<LdapGroup> ldapGroups, Map<String, AcmUser> syncedUsersByDn)
    {
        log.trace("Distinguish user and group members for each ldap group");

        Map<String, LdapGroup> ldapGroupsByDnMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getDistinguishedName, Function.identity()));

        ldapGroups.forEach(ldapGroup -> ldapGroup.getMembers()
                .forEach(dn -> {
                    if (ldapGroupsByDnMap.containsKey(dn))
                    {
                        LdapGroup memberGroup = ldapGroupsByDnMap.get(dn);
                        ldapGroup.addMemberGroup(memberGroup);
                        log.trace("Found member group [{}] with dn [{}] for ldap group [{}] with dn [{}]",
                                memberGroup.getName(), dn, ldapGroup.getName(), ldapGroup.getDistinguishedName());
                    }
                    else if (syncedUsersByDn.containsKey(dn))
                    {
                        AcmUser acmUser = syncedUsersByDn.get(dn);
                        ldapGroup.addUserMember(acmUser.getDistinguishedName());
                        log.trace("Found user member [{}] with dn [{}] for ldap group [{}] with dn [{}]",
                                acmUser.getUserId(), dn, ldapGroup.getName(), ldapGroup.getDistinguishedName());
                    }
                }));
    }

    public Map<String, AcmGroup> getGroupsByNameMap(List<AcmGroup> groups)
    {
        return groups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }

    public Map<String, AcmUser> getUsersByDnMap(Map<String, AcmUser> users)
    {
        return users.values().stream()
                .collect(Collectors.toMap(AcmUser::getDistinguishedName, Function.identity()));
    }

    private void addGroupMembershipForNewGroups(Map<String, LdapGroup> ldapGroups, Map<String, AcmGroup> acmGroupsByName,
            Map<String, AcmGroup> modifiedGroupsByName)
    {
        Map<String, AcmGroup> newGroupsByName = getGroupsByNameMap(newGroups);

        newGroups.forEach(acmGroup -> {
            LdapGroup ldapGroup = ldapGroups.get(acmGroup.getName());
            ldapGroup.getMemberGroups()
                    .forEach(group -> {
                        AcmGroup acmMemberGroup = acmGroupsByName.get(group.getName());
                        Set<AcmGroup> descendantsForMemberGroup = AcmGroupUtils.findDescendantsForAcmGroup(acmMemberGroup);
                        descendantsForMemberGroup.forEach(it -> {
                            String descGroupName = it.getName();
                            if (ldapGroups.containsKey(it.getName()))
                            {
                                LdapGroup ldapDescendantGroup = ldapGroups.get(descGroupName);
                                if (newGroupsByName.containsKey(descGroupName))
                                {
                                    AcmGroup descendantGroup = newGroupsByName.get(descGroupName);
                                    descendantGroup.setAscendantsList(ldapDescendantGroup.getAscendantsAsString());
                                }
                                else
                                {
                                    AcmGroup descendantGroup = getAcmGroupToUpdate(modifiedGroupsByName, acmGroupsByName, descGroupName);
                                    descendantGroup.setAscendantsList(ldapDescendantGroup.getAscendantsAsString());
                                    modifiedGroupsByName.put(descendantGroup.getName(), descendantGroup);
                                }
                            }
                            else
                            {
                                log.warn("Group [{}] not found in LDAP", it.getName());
                            }
                        });
                        acmGroup.addGroupMember(acmMemberGroup);
                        log.trace("Add member group [{}] to parent group [{}]", acmMemberGroup.getName(), acmGroup.getName());
                    });
        });
    }

    private void addAndRemoveGroupMembershipForExistingGroups(Map<String, LdapGroup> ldapGroups,
            Map<String, AcmGroup> acmGroups,
            Map<String, AcmGroup> updatedGroups)
    {
        Predicate<LdapGroup> groupExists = it -> acmGroups.containsKey(it.getName());

        ldapGroups.values().stream()
                .filter(groupExists)
                .forEach(ldapGroup -> {
                    AcmGroup groupToUpdate = getAcmGroupToUpdate(updatedGroups, acmGroups, ldapGroup.getName());
                    groupToUpdate.setAscendantsList(ldapGroup.getAscendantsAsString());

                    Set<String> groupMemberGroups = groupToUpdate.getGroupMemberNames().collect(Collectors.toSet());

                    Set<String> addedGroups = ldapGroup.groupNewGroups(groupMemberGroups);
                    log.debug("Found [{}] added groups in [{}] group", addedGroups.size(), ldapGroup.getName());
                    addedGroups.forEach(groupName -> {
                        AcmGroup memberGroup = getAcmGroupToUpdate(updatedGroups, acmGroups, groupName);
                        log.trace("Add member group [{}] to group [{}]", memberGroup.getName(), groupToUpdate.getName());
                        groupToUpdate.addGroupMember(memberGroup);
                        updatedGroups.put(groupToUpdate.getName(), groupToUpdate);
                        setGroupAscendants(memberGroup, updatedGroups, acmGroups, ldapGroups);
                    });

                    Set<String> removedGroups = ldapGroup.groupRemovedGroups(groupMemberGroups);
                    log.debug("Found [{}] removed groups from [{}] group", removedGroups.size(), ldapGroup.getName());
                    removedGroups.forEach(memberGroupName -> {
                        AcmGroup memberGroup = getAcmGroupToUpdate(updatedGroups, acmGroups, memberGroupName);
                        log.trace("Remove member group [{}] from group [{}]", memberGroup.getName(), groupToUpdate.getName());
                        groupToUpdate.removeGroupMember(memberGroup);
                        updatedGroups.put(groupToUpdate.getName(), groupToUpdate);
                        setGroupAscendants(memberGroup, updatedGroups, acmGroups, ldapGroups);
                    });
                });
    }

    private void setGroupAscendants(AcmGroup acmGroup, Map<String, AcmGroup> updatedGroups,
            Map<String, AcmGroup> acmGroups, Map<String, LdapGroup> ldapGroupByName)
    {
        Set<AcmGroup> descendantsForMemberGroup = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);
        descendantsForMemberGroup.forEach(descendantGroup -> {
            AcmGroup groupForUpdate = getAcmGroupToUpdate(updatedGroups, acmGroups, descendantGroup.getName());
            if (ldapGroupByName.containsKey(groupForUpdate.getName()))
            {
                LdapGroup fromLdap = ldapGroupByName.get(groupForUpdate.getName());
                groupForUpdate.setAscendantsList(fromLdap.getAscendantsAsString());
                updatedGroups.put(groupForUpdate.getName(), groupForUpdate);
            }
        });
    }

    private void addAndRemoveUserMembershipForExistingGroups(List<LdapGroup> ldapGroups, Map<String, AcmUser> syncedUsersByDn,
            Map<String, AcmGroup> acmGroups, Map<String, AcmGroup> updatedGroups)
    {
        Predicate<LdapGroup> groupExists = it -> acmGroups.containsKey(it.getName());

        ldapGroups.stream()
                .filter(groupExists)
                .forEach(ldapGroup -> {
                    AcmGroup groupForUpdate = getAcmGroupToUpdate(updatedGroups, acmGroups, ldapGroup.getName());

                    Set<String> groupUserMemberDns = groupForUpdate.getUserMemberDns().collect(Collectors.toSet());

                    Set<String> newUserMembersDns = ldapGroup.getNewUserMembers(groupUserMemberDns);
                    log.debug("Found [{}] new users in group [{}]", newUserMembersDns.size(), groupForUpdate.getName());
                    List<AcmUser> acmUsers = newUserMembersDns.stream()
                            .map(syncedUsersByDn::get)
                            .collect(Collectors.toList());

                    acmUsers.forEach(acmUser -> {
                        groupForUpdate.addUserMember(acmUser);
                        log.trace("Add user [{}] to group [{}]", acmUser.getUserId(), groupForUpdate.getName());
                        updatedGroups.put(groupForUpdate.getName(), groupForUpdate);
                    });

                    Set<String> removedUsers = ldapGroup.groupRemovedUsers(groupUserMemberDns);
                    log.debug("Found [{}] removed users from [{}] group", removedUsers.size(), groupForUpdate.getName());
                    List<AcmUser> removedAcmUsers = removedUsers.stream()
                            .map(syncedUsersByDn::get)
                            .collect(Collectors.toList());

                    removedAcmUsers.forEach(acmUser -> {
                        groupForUpdate.removeUserMember(acmUser);
                        log.trace("Remove user [{}] from group [{}]", acmUser.getUserId(), groupForUpdate.getName());
                        updatedGroups.put(groupForUpdate.getName(), groupForUpdate);
                    });
                });
    }

    private List<AcmGroup> findAndUpdateModifiedGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> acmGroups)
    {
        Predicate<LdapGroup> groupExists = ldapGroup -> acmGroups.containsKey(ldapGroup.getName());
        Predicate<ImmutablePair<LdapGroup, AcmGroup>> groupIsModified = it -> it.left.isChanged(it.right);
        return ldapGroups.stream()
                .filter(groupExists)
                .map(ldapGroup -> new ImmutablePair<>(ldapGroup, acmGroups.get(ldapGroup.getName())))
                .filter(groupIsModified)
                .map(pair -> {
                    LdapGroup ldapGroup = pair.left;
                    AcmGroup acmGroup = pair.right;
                    log.trace("Modified group [{}] with dn [{}] to be updated", ldapGroup.getName(), ldapGroup.getDistinguishedName());
                    return ldapGroup.setAcmGroupEditableFields(acmGroup);
                })
                .collect(Collectors.toList());
    }

    private List<AcmGroup> findAndCreateNewGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> acmGroups)
    {
        Predicate<LdapGroup> doesNotExist = it -> !acmGroups.containsKey(it.getName());
        return ldapGroups.stream()
                .filter(doesNotExist)
                .peek(it -> log.trace("New group [{}] with dn [{}] to be synced", it.getName(), it.getDistinguishedName()))
                .map(LdapGroup::toAcmGroup)
                .collect(Collectors.toList());
    }

    private List<AcmGroup> findDeletedGroups(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups)
    {
        Set<String> ldapGroupNames = ldapGroups.stream()
                .map(LdapGroup::getName)
                .collect(Collectors.toSet());

        Predicate<AcmGroup> notInLdap = it -> !ldapGroupNames.contains(it.getName());
        Predicate<AcmGroup> isActive = it -> it.getStatus() == AcmGroupStatus.ACTIVE;

        List<AcmGroup> deletedGroups = acmGroups.stream()
                .filter(notInLdap.and(isActive))
                .collect(Collectors.toList());

        deletedGroups.forEach(it -> {
            log.trace("Deleted group [{}] with dn [{}] to be updated as [{}]",
                    it.getName(), it.getDistinguishedName(), AcmGroupStatus.INACTIVE);
            it.setStatus(AcmGroupStatus.INACTIVE);
        });
        return deletedGroups;
    }

    private void addUserMembershipForNewGroups(Map<String, LdapGroup> ldapGroups, Map<String, AcmUser> syncedUsersByDn)
    {
        newGroups.forEach(acmGroup -> {
            LdapGroup ldapGroup = ldapGroups.get(acmGroup.getName());
            ldapGroup.getMemberUserDns()
                    .forEach(userDn -> {
                        AcmUser acmUser = syncedUsersByDn.get(userDn);
                        log.trace("Add user member [{}] to group [{}]", acmUser.getUserId(), acmGroup.getName());
                        acmGroup.addUserMember(acmUser);
                    });
        });
    }

    private void removeDeletedGroupsUserMembership(List<AcmGroup> deletedGroups)
    {
        deletedGroups.forEach(group -> {
            log.debug("Found [{}] users to remove from deleted group [{}]", group.getUserMembers(false).size(), group.getName());
            group.getUserMembers(false).clear();
        });
    }

    private void removeDeletedGroupsGroupMembership(List<AcmGroup> deletedGroups,
            Map<String, AcmGroup> modifiedGroupsByName,
            Map<String, AcmGroup> acmGroupsByName,
            Map<String, LdapGroup> ldapGroupMap)
    {
        deletedGroups.forEach(group -> {
            setGroupAscendants(group, modifiedGroupsByName, acmGroupsByName, ldapGroupMap);
            group.getMemberGroups().clear();
            group.getMemberOfGroups().clear();
            group.setAscendantsList(null);
        });
    }

    private AcmGroup getAcmGroupToUpdate(Map<String, AcmGroup> updatedGroups, Map<String, AcmGroup> acmGroups, String groupName)
    {
        // group can already be updated, so check in updated groups to make further changes
        if (updatedGroups.containsKey(groupName))
        {
            return updatedGroups.get(groupName);
        }
        return acmGroups.get(groupName);
    }

    public List<AcmGroup> getNewGroups()
    {
        return newGroups;
    }

    public List<AcmGroup> getModifiedGroups()
    {
        return modifiedGroups;
    }

    public List<AcmGroup> getDeletedGroups()
    {
        return deletedGroups;
    }

}
