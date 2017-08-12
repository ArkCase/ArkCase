package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.group.AcmGroup;

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

    public Map<String, AcmGroup> sync(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups, Map<String, AcmUser> currentUsers)
    {
        Map<String, AcmGroup> currentGroups = getGroupsByIdMap(acmGroups);

        newGroups = findAndCreateNewGroups(ldapGroups, currentGroups);
        separateUserAndGroupsDnsFromGroupMembers(ldapGroups, currentGroups, newGroups, currentUsers);

        changedGroups = findAndUpdateModifiedGroups(ldapGroups, currentGroups);
        Map<String, AcmGroup> changedGroupsMap = getGroupsByIdMap(changedGroups);
        addAndRemoveGroupMemberUsers(ldapGroups, currentUsers, currentGroups, changedGroupsMap);
        addAndRemoveGroupMemberGroups(ldapGroups, currentGroups, changedGroupsMap);
        changedGroups = new ArrayList<>(changedGroupsMap.values());

        mapNewGroupsUserMembership(newGroups, ldapGroups, currentUsers);
        newGroups.forEach(acmGroup -> currentGroups.put(acmGroup.getName(), acmGroup));
        mapNewGroupsGroupMembership(ldapGroups, currentGroups);
        return currentGroups;
    }

    private void separateUserAndGroupsDnsFromGroupMembers(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups,
                                                          List<AcmGroup> newGroups, Map<String, AcmUser> currentUsers)
    {
        Map<String, AcmGroup> allGroupsByDnMap = newGroups.stream()
                .collect(Collectors.toMap(AcmGroup::getDistinguishedName, Function.identity()));
        currentGroups.values()
                .forEach(acmGroup -> allGroupsByDnMap.put(acmGroup.getDistinguishedName(), acmGroup));

        Map<String, AcmUser> allUsersByDnMap = getUsersByDnMap(currentUsers);

        ldapGroups.forEach(ldapGroup -> ldapGroup.getMembers()
                .forEach(dn -> {
                    if (allGroupsByDnMap.containsKey(dn))
                    {
                        AcmGroup acmGroup = allGroupsByDnMap.get(dn);
                        ldapGroup.addGroupMember(acmGroup.getName());
                    } else if (allUsersByDnMap.containsKey(dn))
                    {
                        AcmUser acmUser = allUsersByDnMap.get(dn);
                        ldapGroup.addUserMember(acmUser.getDistinguishedName());
                    }
                })
        );
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

    public Map<String, Set<String>> getGroupsByUserIdMap(Map<String, AcmGroup> groupsByIdMap)
    {
        return groupsByIdMap.values()
                .stream()
                .filter(acmGroup -> acmGroup.getMembers() != null)
                .flatMap(acmGroup -> acmGroup.getMembers().stream()
                        .map(acmUser -> new AbstractMap.SimpleEntry<>(acmUser, acmGroup))
                )
                .collect(Collectors.groupingBy(it -> it.getKey().getUserId(),
                        Collectors.mapping(it -> it.getValue().getName(), Collectors.toSet())));
    }

    private void mapNewGroupsGroupMembership(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        Map<String, LdapGroup> ldapGroupMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getName, Function.identity()));

        newGroups.forEach(acmGroup -> {
            LdapGroup ldapGroup = ldapGroupMap.get(acmGroup.getName());
            ldapGroup.getMemberGroups()
                    .forEach(memberGroup -> {
                        AcmGroup childGroup = currentGroups.get(memberGroup);
                        childGroup.setParentGroup(acmGroup);
                        if (childGroup.getMembers() != null)
                        {
                            childGroup.getMembers().forEach(member -> {
                                acmGroup.addMember(member);
                                addUserNewGroup(member.getUserId(), acmGroup.getName());
                            });

                        }
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

                    Set<String> childGroupNames = currentGroup.getChildGroupNames();

                    Set<String> addedGroups = ldapGroup.groupAddedGroupMembers(childGroupNames);
                    addedGroups.forEach(group -> {
                        AcmGroup acmGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, group);
                        if (acmGroup.getParentGroup() == null)
                        {
                            acmGroup.setParentGroup(currentGroup);
                            if (acmGroup.getMembers() != null)
                            {
                                acmGroup.getMembers().forEach(currentGroup::addMember);
                                updatedGroups.put(currentGroup.getName(), currentGroup);
                            }
                            updatedGroups.put(acmGroup.getName(), acmGroup);
                        }
                    });

                    Set<String> removedGroups = ldapGroup.groupRemovedGroupMembers(childGroupNames);
                    removedGroups.forEach(group -> {
                        AcmGroup acmGroup = getAcmGroupToUpdate(updatedGroups, currentGroups, group);
                        if (acmGroup.getMembers() != null)
                        {
                            acmGroup.getMembers().forEach(currentGroup::removeMember);
                            updatedGroups.put(currentGroup.getName(), currentGroup);
                        }
                        acmGroup.setParentGroup(null);
                        updatedGroups.put(acmGroup.getName(), acmGroup);
                    });
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
                    AcmGroup parentGroup = currentGroup.getParentGroup();

                    Set<String> membersDns = currentGroup.getMembersDns();

                    Set<String> newUsers = ldapGroup.groupAddedUserDns(membersDns);
                    newUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.addMember(acmUser);
                        updatedGroups.put(currentGroup.getName(), currentGroup);
                        addUserNewGroup(acmUser.getUserId(), currentGroup.getName());
                        if (parentGroup != null)
                        {
                            parentGroup.addMember(acmUser);
                            updatedGroups.put(parentGroup.getName(), parentGroup);
                            addUserNewGroup(acmUser.getUserId(), parentGroup.getName());
                        }
                    });

                    Set<String> removedUsers = ldapGroup.groupRemovedUserDns(membersDns);
                    removedUsers.stream()
                            .filter(s -> currentGroup.getChildGroups().stream()
                                    .noneMatch(group -> group.getMembersDns().contains(s)))
                            .forEach(user -> {
                                AcmUser acmUser = dnUserMap.get(user);
                                currentGroup.removeMember(acmUser);
                                updatedGroups.put(currentGroup.getName(), currentGroup);
                                addUserRemovedGroup(acmUser.getUserId(), currentGroup.getName());
                                if (parentGroup != null)
                                {
                                    parentGroup.removeMember(acmUser);
                                    updatedGroups.put(parentGroup.getName(), parentGroup);
                                    addUserRemovedGroup(acmUser.getUserId(), parentGroup.getName());
                                }
                            });
                });
    }

    private List<AcmGroup> findAndUpdateModifiedGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        return ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .filter(it -> it.isChanged(currentGroups.get(it.getName())))
                .map(it -> {
                    AcmGroup currentGroup = currentGroups.get(it.getName());
                    return it.setAcmGroupEditableFields(currentGroup);
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

    private void mapNewGroupsUserMembership(List<AcmGroup> newGroups, List<LdapGroup> ldapGroups, Map<String,
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
                        acmGroup.addMember(acmUser);
                        addUserNewGroup(acmUser.getUserId(), acmGroup.getName());
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

    private AcmGroup getAcmGroupToUpdate(Map<String, AcmGroup> updatedGroups, Map<String, AcmGroup> currentGroups,
                                         String groupName)
    {
        if (updatedGroups.containsKey(groupName))
        {
            return updatedGroups.get(groupName);
        }
        return currentGroups.get(groupName);
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
