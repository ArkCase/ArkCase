package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.group.AcmGroup;

import java.util.AbstractMap;
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

        changedGroups = findAndUpdateModifiedGroups(ldapGroups, currentGroups);
        newGroups = findAndCreateNewGroups(ldapGroups, currentGroups);

        separateUserAndGroupsDnsFromGroupMembers(ldapGroups, currentGroups, newGroups, currentUsers);

        addAndRemoveGroupMemberUsers(ldapGroups, currentUsers, currentGroups);
        addAndRemoveGroupMemberGroups(ldapGroups, currentGroups);

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

    private void addAndRemoveGroupMemberGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .forEach(ldapGroup -> {
                    AcmGroup currentGroup = currentGroups.get(ldapGroup.getName());

                    Set<String> childGroupNames = currentGroup.getChildGroupNames();

                    Set<String> addedGroups = ldapGroup.groupAddedGroupMembers(childGroupNames);
                    addedGroups.forEach(group -> {
                        AcmGroup acmGroup = currentGroups.get(group);
                        acmGroup.setParentGroup(currentGroup);
                        if (acmGroup.getMembers() != null)
                        {
                            acmGroup.getMembers().forEach(currentGroup::addMember);
                        }

                    });

                    Set<String> removedGroups = ldapGroup.groupRemovedGroupMembers(childGroupNames);
                    removedGroups.forEach(group -> {
                        AcmGroup acmGroup = currentGroups.get(group);
                        if (acmGroup.getMembers() != null)
                        {
                            acmGroup.getMembers().forEach(currentGroup::removeMember);
                        }
                        acmGroup.setParentGroup(null);
                    });
                });
    }

    private void addAndRemoveGroupMemberUsers(List<LdapGroup> ldapGroups, Map<String, AcmUser> currentUsers,
                                              Map<String, AcmGroup> currentGroups)
    {
        Map<String, AcmUser> dnUserMap = getUsersByDnMap(currentUsers);

        ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .forEach(ldapGroup -> {
                    AcmGroup currentGroup = currentGroups.get(ldapGroup.getName());
                    AcmGroup parentGroup = currentGroup.getParentGroup();

                    Set<String> membersDns = currentGroup.getMembersDns();

                    Set<String> newUsers = ldapGroup.groupAddedUserDns(membersDns);
                    newUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.addMember(acmUser);
                        addUserNewGroup(acmUser.getUserId(), currentGroup.getName());
                        if (parentGroup != null)
                        {
                            parentGroup.addMember(acmUser);
                            addUserNewGroup(acmUser.getUserId(), parentGroup.getName());
                        }
                    });

                    Set<String> removedUsers = ldapGroup.groupRemovedUserDns(membersDns);
                    removedUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.removeMember(acmUser);
                        addUserRemovedGroup(acmUser.getUserId(), currentGroup.getName());
                        if (parentGroup != null)
                        {
                            parentGroup.removeMember(acmUser);
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

    private void mapNewGroupsUserMembership(List<AcmGroup> acmGroups, List<LdapGroup> ldapGroups, Map<String,
            AcmUser> currentUsers)
    {
        Map<String, AcmUser> dnAcmUserMap = getUsersByDnMap(currentUsers);

        Map<String, LdapGroup> nameLdapGroupMap = ldapGroups.stream()
                .collect(Collectors.toMap(LdapGroup::getName, Function.identity()));

        acmGroups.forEach(it -> {
            LdapGroup group = nameLdapGroupMap.get(it.getName());
            group.getMemberUsers()
                    .forEach(userDn -> {
                        AcmUser acmUser = dnAcmUserMap.get(userDn);
                        it.addMember(acmUser);
                        addUserNewGroup(acmUser.getUserId(), it.getName());
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
