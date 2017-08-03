package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmRoleType;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.group.AcmGroup;

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

    public void sync(List<LdapGroup> ldapGroups, List<AcmGroup> acmGroups, Map<String, AcmUser> currentUsers)
    {
        Map<String, AcmGroup> currentGroups = acmGroups.stream()
                .collect(Collectors.toMap(AcmGroup::getName, Function.identity()));

        newGroups = findNewGroups(ldapGroups, currentUsers, currentGroups);
        changedGroups = findModifiedGroups(ldapGroups, currentGroups);

        addAndRemoveGroupMemberUsers(ldapGroups, currentUsers, currentGroups);
        addAndRemoveGroupMemberGroups(ldapGroups, currentGroups);

        newGroups.forEach(acmGroup -> currentGroups.put(acmGroup.getName(), acmGroup));
        setNewGroupsMembership(ldapGroups, currentGroups);
    }

    private void setNewGroupsMembership(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
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
                        if(acmGroup.getMembers() != null){
                            acmGroup.getMembers().forEach(currentGroup::addMember);
                        }

                    });

                    Set<String> removedGroups = ldapGroup.groupRemovedGroupMembers(childGroupNames);
                    removedGroups.forEach(group -> {
                        AcmGroup acmGroup = currentGroups.get(group);
                        if(acmGroup.getMembers() != null)
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
        Map<String, AcmUser> dnUserMap = currentUsers.values().stream()
                .collect(Collectors.toMap(AcmUser::getDistinguishedName, Function.identity()));

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
                        addUserNewGroup(user, currentGroup.getName());
                        if (parentGroup != null)
                        {
                            parentGroup.addMember(acmUser);
                            addUserNewGroup(user, parentGroup.getName());
                        }
                    });

                    Set<String> removedUsers = ldapGroup.groupRemovedUserDns(membersDns);
                    removedUsers.forEach(user -> {
                        AcmUser acmUser = dnUserMap.get(user);
                        currentGroup.removeMember(acmUser);
                        addUserRemovedGroup(user, currentGroup.getName());
                        if (parentGroup != null)
                        {
                            parentGroup.removeMember(acmUser);
                            addUserRemovedGroup(user, parentGroup.getName());
                        }
                    });
                });
    }

    private List<AcmGroup> findModifiedGroups(List<LdapGroup> ldapGroups, Map<String, AcmGroup> currentGroups)
    {
        return ldapGroups.stream()
                .filter(it -> currentGroups.containsKey(it.getName()))
                .filter(it -> it.isChanged(currentGroups.get(it.getName())))
                .map(it -> {
                    AcmGroup currentGroup = currentGroups.get(it.getName());
                    return updateEditableGroupFields(currentGroup, it);
                })
                .collect(Collectors.toList());
    }

    private List<AcmGroup> findNewGroups(List<LdapGroup> ldapGroups, Map<String, AcmUser> currentUsers,
                                         Map<String, AcmGroup> currentGroups)
    {
        Map<String, AcmUser> dnUserMap = currentUsers.values().stream()
                .collect(Collectors.toMap(AcmUser::getDistinguishedName, Function.identity()));

        return ldapGroups.stream()
                .filter(it -> !currentGroups.containsKey(it.getName()))
                .map(it -> {
                    AcmGroup acmGroup = new AcmGroup();
                    acmGroup.setName(it.getName());
                    acmGroup.setType(AcmRoleType.LDAP_GROUP.getRoleName());
                    acmGroup.setDirectoryName(it.getDirectoryName());
                    acmGroup.setDistinguishedName(it.getDistinguishedName());
                    updateEditableGroupFields(acmGroup, it);
                    it.getMemberUsers()
                            .forEach(userDn -> {
                                AcmUser acmUser = dnUserMap.get(userDn);
                                acmGroup.addMember(acmUser);
                                addUserNewGroup(acmUser.getUserId(), acmGroup.getName());
                            });
                    return acmGroup;
                })
                .collect(Collectors.toList());
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

    private AcmGroup updateEditableGroupFields(AcmGroup existingGroup, LdapGroup ldapGroup)
    {
        existingGroup.setDescription(ldapGroup.getDescription());
        existingGroup.setStatus("ACTIVE"); // TODO: fix status
        return existingGroup;
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
