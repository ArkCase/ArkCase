package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapGroupNode;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Synchronizes LDAP groups with current AcmGroup groups
 */
public class AcmGroupsSyncResult
{
    private Map<String, Set<String>> userNewGroups;
    private Map<String, Set<String>> userRemovedGroups;
    private List<AcmGroup> newGroups;
    private List<AcmGroup> changedGroups;

    public AcmGroupsSyncResult()
    {
        this.userNewGroups = new HashMap<>();
        this.userRemovedGroups = new HashMap<>();
    }

    public Map<String, Set<String>> sync(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups, Map<String, AcmUser> currentUsers)
    {
        Map<String, AcmGroup> currentGroups = getGroupsByIdMap(acmGroups);
        separateUserAndGroupsFromGroupMembers(ldapGroups, currentGroups, currentUsers);
        mapAscendantsToLdapGroups(ldapGroups);

        newGroups = findAndCreateNewGroups(ldapGroups, currentGroups);
        changedGroups = findAndUpdateModifiedGroups(ldapGroups, currentGroups);

        Map<String, AcmGroup> changedGroupsMap = getGroupsByIdMap(changedGroups);
        addAndRemoveGroupMemberUsers(ldapGroups, currentUsers, currentGroups, changedGroupsMap);
        addAndRemoveGroupMemberGroups(ldapGroups, currentGroups, changedGroupsMap);
        changedGroups = new ArrayList<>(changedGroupsMap.values());

        mapNewGroupsUserMembership(ldapGroups, currentUsers);
        // now currentGroups map can include new groups
        newGroups.forEach(acmGroup -> currentGroups.put(acmGroup.getName(), acmGroup));
        mapNewGroupsGroupMembership(ldapGroups, currentGroups);
        return getGroupNamesByUserIdMap(currentGroups);
    }

    public void mapAscendantsToLdapGroups(List<LdapGroup> ldapGroups)
    {
        ldapGroups.forEach(ldapGroup -> {
            Set<LdapGroup> ascendants = new LdapGroupUtils()
                    .findAscendantsForLdapGroupNode(new LdapGroupNode(ldapGroup), new HashSet<>(ldapGroups));
            ldapGroup.setAscendants(ascendants);
        });
    }

    private void separateUserAndGroupsFromGroupMembers(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups,
                                                       Map<String, AcmUser> currentUsers)
    {
        Map<String, AcmGroup> currentGroupsByDnMap = currentGroups.values().stream()
                .collect(Collectors.toMap(AcmGroup::getDistinguishedName, Function.identity()));

        Map<String, AcmUser> allUsersByDnMap = getUsersByDnMap(currentUsers);

        Map<String, LdapGroup> ldapGroupsByDnMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getDistinguishedName, Function.identity()));

        ldapGroups.forEach(ldapGroup -> ldapGroup.getMembers()
                .forEach(dn -> {
                    //check in ldapGroups, if memberGroup is newly added group entry
                    if (ldapGroupsByDnMap.containsKey(dn))
                    {
                        LdapGroup memberGroup = ldapGroupsByDnMap.get(dn);
                        ldapGroup.addMemberGroup(memberGroup);
                    }
                    //check in existing acmGroups, if memberGroup is an already existing group
                    else if (currentGroupsByDnMap.containsKey(dn))
                    {
                        AcmGroup acmGroup = currentGroupsByDnMap.get(dn);
                        LdapGroup memberGroup = acmGroupToLdapGroup(acmGroup);
                        Set<LdapGroup> memberGroups = acmGroup.getMemberGroups().stream()
                                .map(this::acmGroupToLdapGroup)
                                .collect(Collectors.toSet());
                        memberGroup.setMemberGroups(memberGroups);
                        ldapGroup.addMemberGroup(memberGroup);
                    }
                    //if not, member must be user entry
                    else if (allUsersByDnMap.containsKey(dn))
                    {
                        AcmUser acmUser = allUsersByDnMap.get(dn);
                        ldapGroup.addUserMember(acmUser.getDistinguishedName());
                    }
                })
        );
    }

    private LdapGroup acmGroupToLdapGroup(AcmGroup acmGroup)
    {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setName(acmGroup.getName());
        ldapGroup.setDisplayName(acmGroup.getDisplayName());
        ldapGroup.setDistinguishedName(acmGroup.getDistinguishedName());
        ldapGroup.setDirectoryName(acmGroup.getDirectoryName());
        ldapGroup.setDescription(acmGroup.getDescription());
        ldapGroup.setMemberUsers(acmGroup.getUserMemberDns().collect(Collectors.toSet()));
        return ldapGroup;
    }

    public Map<String, AcmGroup> getGroupsByIdMap(List<AcmGroup> groups)
    {
        return groups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));
    }

    public Map<String, AcmUser> getUsersByDnMap(Map<String, AcmUser> users)
    {
        return users.values().stream()
                .collect(Collectors.toMap(AcmUser::getDistinguishedName, Function.identity()));
    }

    private void mapNewGroupsGroupMembership(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        Map<String, LdapGroup> ldapGroupMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getName, Function.identity()));

        newGroups.forEach(acmGroup -> {
            LdapGroup ldapGroup = ldapGroupMap.get(acmGroup.getName());
            ldapGroup.getMemberGroups()
                    .forEach(group -> {
                        AcmGroup acmMemberGroup = currentGroups.get(group.getName());
                        acmGroup.addGroupMember(acmMemberGroup);
                        acmMemberGroup.getUserMembers().forEach(user -> {
                            addUserNewGroup(user.getUserId(), acmGroup.getName());
                            acmGroup.getAscendants()
                                    .filter(StringUtils::isNotEmpty)
                                    .forEach(it ->
                                            addUserNewGroup(user.getUserId(), it)
                                    );
                        });
                    });
        });
    }

    private void addAndRemoveGroupMemberGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups,
                                               Map<String, AcmGroup> updatedGroups)
    {
        ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .forEach(ldapGroup -> {
                    AcmGroup currentGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, ldapGroup.getName());

                    Set<String> groupMemberGroups = currentGroup.getGroupMemberNames().collect(Collectors.toSet());

                    Set<String> addedGroups = ldapGroup.groupNewGroups(groupMemberGroups);
                    addedGroups.forEach(group ->
                            collectUserNewGroups(currentGroups, updatedGroups, currentGroup, group));

                    Set<String> removedGroups = ldapGroup.groupRemovedGroups(groupMemberGroups);
                    removedGroups.forEach(group ->
                            collectUserRemovedGroups(currentGroups, updatedGroups, currentGroup, group));
                });
    }

    private void collectUserNewGroups(Map<String, AcmGroup> currentGroups, Map<String, AcmGroup> updatedGroups,
                                      AcmGroup currentGroup, String group)
    {
        AcmGroup acmGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, group);
        currentGroup.addGroupMember(acmGroup);
        acmGroup.addAscendant(currentGroup.getName());
        updatedGroups.put(currentGroup.getName(), currentGroup);
        acmGroup.getUserMembers().forEach(user -> {
            addUserNewGroup(user.getUserId(), currentGroup.getName());
            // add user new group for all ascendants of currentGroup
            currentGroup.getAscendants()
                    .filter(StringUtils::isNotEmpty)
                    .forEach(it -> addUserNewGroup(user.getUserId(), it));
        });
    }

    private void collectUserRemovedGroups(Map<String, AcmGroup> currentGroups, Map<String, AcmGroup> updatedGroups,
                                          AcmGroup currentGroup, String group)
    {
        AcmGroup acmGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, group);
        currentGroup.removeGroupMember(acmGroup);
        acmGroup.removeAscendant(currentGroup.getName());
        updatedGroups.put(acmGroup.getName(), acmGroup);
        acmGroup.getUserMembers().stream()
                .filter(user -> !currentGroup.getUserMembers().contains(user))
                .forEach(user -> {
                    addUserRemovedGroup(user.getUserId(), currentGroup.getName());
                    // remove user group for all ascendants of currentGroup
                    Set<AcmGroup> allGroups = new HashSet<>(updatedGroups.values());
                    allGroups.addAll(currentGroups.values());
                    currentGroup.getAscendants()
                            .filter(StringUtils::isNotEmpty)
                            .map(it -> getAcmGroupToUpdate(updatedGroups, currentGroups, it))
                            .filter(it -> !it.hasUserMember(user))
                            .forEach(it ->
                                    addUserRemovedGroup(user.getUserId(), it.getName())
                            );
                });
    }

    private void addAndRemoveGroupMemberUsers(List<LdapGroup> ldapGroups, Map<String, AcmUser> currentUsers,
                                              Map<String, AcmGroup> currentGroups, Map<String, AcmGroup> updatedGroups)
    {
        Map<String, AcmUser> dnUserMap = getUsersByDnMap(currentUsers);

        ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .forEach(ldapGroup -> {
                    AcmGroup currentGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, ldapGroup.getName());

                    Set<String> groupMemberUsers = currentGroup.getUserMemberDns().collect(Collectors.toSet());

                    Set<String> newUsers = ldapGroup.groupNewUsers(groupMemberUsers);
                    newUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.addUserMember(acmUser);
                        updatedGroups.put(currentGroup.getName(), currentGroup);
                        addUserNewGroup(acmUser.getUserId(), currentGroup.getName());
                        ldapGroup.getAscendants()
                                .forEach(group -> addUserNewGroup(acmUser.getUserId(), group.getName()));
                    });

                    Set<String> removedUsers = ldapGroup.groupRemovedUsers(groupMemberUsers);
                    removedUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.removeUserMember(acmUser);
                        updatedGroups.put(currentGroup.getName(), currentGroup);
                        addUserRemovedGroup(acmUser.getUserId(), currentGroup.getName());
                        ldapGroup.getAscendants().stream()
                                // avoid removing ascendant group if user has direct link to that group
                                .filter(group -> !ldapGroup.hasUserMember(acmUser.getUserId()))
                                .forEach(group -> addUserRemovedGroup(acmUser.getUserId(), group.getName()));
                    });
                });
    }

    private List<AcmGroup> findAndUpdateModifiedGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        return ldapGroups.stream()
                .filter(ldapGroup -> currentGroups.containsKey(ldapGroup.getName()))
                .filter(ldapGroup -> ldapGroup.isChanged(currentGroups.get(ldapGroup.getName())))
                .map(ldapGroup -> {
                    AcmGroup currentGroup = currentGroups.get(ldapGroup.getName());
                    return ldapGroup.setAcmGroupEditableFields(currentGroup);
                })
                .collect(Collectors.toList());
    }

    private List<AcmGroup> findAndCreateNewGroups(List<LdapGroup> ldapGroups,
                                                  Map<String, AcmGroup> currentGroups)
    {
        return ldapGroups.stream()
                .filter(it -> !currentGroups.containsKey(it.getName()))
                .map(LdapGroup::toAcmGroup)
                .collect(Collectors.toList());
    }

    private void mapNewGroupsUserMembership(List<LdapGroup> ldapGroups, Map<String,
            AcmUser> currentUsers)
    {
        Map<String, AcmUser> dnAcmUserMap = getUsersByDnMap(currentUsers);

        Map<String, LdapGroup> nameLdapGroupMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getName, Function.identity()));

        newGroups.forEach(acmGroup -> {
            LdapGroup group = nameLdapGroupMap.get(acmGroup.getName());
            group.getMemberUsers()
                    .forEach(userDn -> {
                        AcmUser acmUser = dnAcmUserMap.get(userDn);
                        acmGroup.addUserMember(acmUser);
                        addUserNewGroup(acmUser.getUserId(), acmGroup.getName());
                        group.getAscendants()
                                .forEach(it -> addUserNewGroup(acmUser.getUserId(), it.getName()));
                    });
        });
    }

    private void addUserNewGroup(String userId, String group)
    {
        Set<String> groups = userNewGroups.getOrDefault(userId, new HashSet<>());
        groups.add(group);
        userNewGroups.put(userId, groups);
    }

    private void addUserRemovedGroup(String userId, String group)
    {
        Set<String> groups = userRemovedGroups.getOrDefault(userId, new HashSet<>());
        groups.add(group);
        userRemovedGroups.put(userId, groups);
    }

    private AcmGroup getAcmGroupToUpdate(Map<String, AcmGroup> updatedGroups, Map<String, AcmGroup> currentGroups, String groupName)
    {
        //group can already be updated, so check in updated groups to make further changes
        if (updatedGroups.containsKey(groupName))
        {
            return updatedGroups.get(groupName);
        }
        return currentGroups.get(groupName);
    }

    /**
     * Transforms a map of groups into map of (user -> Set<AcmGroup.name>)
     *
     * @param groupsByIdMap group.id -> group map
     * @return user -> Set<group.name> map
     */
    public Map<String, Set<String>> getGroupNamesByUserIdMap(Map<String, AcmGroup> groupsByIdMap)
    {
        return groupsByIdMap.values()
                .stream()
                .filter(acmGroup -> acmGroup.getUserMembers() != null)
                .flatMap(acmGroup -> acmGroup.getUserMembers().stream()
                        .map(acmUser -> new AbstractMap.SimpleEntry<>(acmUser, acmGroup))
                )
                .collect(Collectors.groupingBy(it -> it.getKey().getUserId(),
                        Collectors.mapping(it -> it.getValue().getName(), Collectors.toSet())));
    }

    public Map<String, Set<String>> getUserNewGroups()
    {
        return userNewGroups;
    }

    public Map<String, Set<String>> getUserRemovedGroups()
    {
        return userRemovedGroups;
    }

    public List<AcmGroup> getNewGroups()
    {
        return newGroups;
    }

    public List<AcmGroup> getChangedGroups()
    {
        return changedGroups;
    }
}
