package com.armedia.acm.services.users.service.group;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.util.AcmSolrUtil;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.service.AcmGroupEventPublisher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.FlushModeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GroupServiceImpl implements GroupService
{
    private final Logger log = LogManager.getLogger(getClass());

    private UserDao userDao;
    private AcmGroupDao groupDao;
    private ExecuteSolrQuery executeSolrQuery;
    private AcmGroupEventPublisher acmGroupEventPublisher;

    @Override
    public AcmGroup findByName(String name)
    {
        return groupDao.findByName(name);
    }

    @Override
    public AcmGroup findByName(String name, FlushModeType flushModeType)
    {
        return groupDao.findByName(name, flushModeType);
    }

    @Override
    public AcmGroup save(AcmGroup group)
    {
        return groupDao.save(group);
    }

    @Override
    public AcmGroup createGroup(AcmGroup group) throws AcmObjectAlreadyExistsException
    {
        String groupName = group.getName();
        AcmGroup acmGroup = groupDao.findByName(groupName);
        if (acmGroup != null && acmGroup.getStatus() == AcmGroupStatus.ACTIVE)
        {
            throw new AcmObjectAlreadyExistsException("Group " + group.getName() + " already exists.");
        }

        group.setStatus(AcmGroupStatus.ACTIVE);
        group.setName(groupName);
        group.setDisplayName(groupName);
        return groupDao.save(group);
    }

    @Override
    @Transactional
    public AcmGroup saveAndFlush(AcmGroup group)
    {
        AcmGroup managed = groupDao.save(group);
        groupDao.getEm().flush();
        return managed;
    }

    @Override
    public String buildGroupsSolrQuery()
    {
        String query = "object_type_s:GROUP AND status_lcs:ACTIVE";

        return query;
    }

    @Override
    public String buildGroupsSolrQuery(Authentication auth, Integer startRow, Integer maxRows, String sortBy, String sortDirection)
            throws SolrException
    {
        String query = "object_type_s:GROUP AND status_lcs:ACTIVE";

        log.debug("User [{}] is searching for [{}]", auth.getName(), query);

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @Override
    public String buildGroupsAdHocSolrQuery()
    {
        String query = "object_type_s:GROUP AND object_sub_type_s:ADHOC_GROUP AND status_lcs:ACTIVE";

        return query;
    }

    @Override
    public String buildGroupsAdHocByNameSolrQuery(String fq)
    {
        String query = "object_type_s:GROUP AND object_sub_type_s:ADHOC_GROUP AND status_lcs:ACTIVE AND name_partial:" + fq;

        return query;
    }

    @Override
    public String buildGroupsByNameSolrQuery(String fq)
    {
        String query = "object_type_s:GROUP AND status_lcs:ACTIVE AND name_partial:"
                + fq;

        return query;
    }

    @Override
    public String buildGroupsForUserByNameSolrQuery(Boolean authorized, String userId, String searchFilter)
    {
        String query = buildGroupsForUserSolrQuery(authorized, userId) + " AND name_partial:" + searchFilter;

        return query;
    }

    @Override
    public String getAdHocMemberGroupsByMatchingName(Authentication auth, Integer startRow, Integer maxRows, String sortBy,
            String sortDirection,
            Boolean authorized, String groupId, String searchFilter, String groupType) throws SolrException
    {
        String query = "object_type_s:GROUP AND -object_id_s:" + groupId + " AND status_lcs:ACTIVE AND object_sub_type_s:"
                + groupType
                + (authorized ? " AND groups_member_of_id_ss:" + groupId
                        : " AND -groups_member_of_id_ss:" + groupId + " AND -child_id_ss:" + groupId)
                + " AND name_partial:" + searchFilter;

        log.debug("User [{}] is searching for [{}]", auth.getName(), query);

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @Override
    public String buildGroupsForUserSolrQuery(Boolean authorized, String userId)
    {
        AcmUser user = userDao.findByUserId(userId);
        if (user == null)
        {
            return "";
        }
        return "object_type_s:GROUP AND "
                + "(object_sub_type_s:ADHOC_GROUP OR (object_sub_type_s:LDAP_GROUP AND directory_name_s:"
                + user.getUserDirectoryName()
                + ")) AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED"
                + (authorized ? " AND member_id_ss:" : " AND -member_id_ss:") + userId;
    }

    @Override
    public String getAdHocMemberGroups(Authentication auth, Integer startRow, Integer maxRows, String sortBy, String sortDirection,
            Boolean authorized, String groupId, String groupType) throws SolrException
    {
        groupId = groupId.replace("\\", "\\\\");
        String query = "object_type_s:GROUP AND -object_id_s:" + groupId
                + " AND status_lcs:ACTIVE AND object_sub_type_s:"
                + groupType
                + (authorized ? " AND groups_member_of_id_ss:" + groupId
                        : " AND -groups_member_of_id_ss:" + groupId + " AND -child_id_ss:"
                                + groupId);

        log.debug("User [{}] is searching for [{}]", auth.getName(), query);

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows,
                sortBy + " " + sortDirection);
    }

    @Override
    public String getGroupsByNameFilter(Authentication authentication, String nameFilter, int start, int max, String sortBy, String sortDir)
            throws SolrException
    {
        String query = "object_type_s:GROUP AND status_lcs:ACTIVE AND -ascendants_id_ss:* AND name_partial:"
                + nameFilter;
        return executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, start, max,
                sortBy + " " + sortDir);
    }

    @Override
    public String getLdapGroupsForUser(Authentication authentication) throws SolrException
    {
        log.info("Taking all groups and ascendant groups from Solr. Authenticated user is [{}]",
                authentication.getName());

        String query = "object_type_s:GROUP AND object_sub_type_s:LDAP_GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE "
                + "AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        return executeSolrQuery.getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, 0, 1000,
                "name asc");
    }

    @Override
    public String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws SolrException
    {
        String statusQuery = userStatus.map(it -> {
            try
            {
                AcmUserState state = AcmUserState.valueOf(it);
                return String.format(" AND status_lcs:%s", state);
            }
            catch (IllegalArgumentException e)
            {
                log.debug("usersStatus: [{}] is not a valid value. Won't be included in the query!", userStatus);
                return "";
            }
        }).orElse("");

        String query = String.format("object_type_s:USER AND groups_id_ss:%s", buildSafeGroupNameForSolrSearch(groupName));
        // query = query.replace("_002E_", ".");
        query += statusQuery;

        log.debug("Executing query for users in group: [{}]", query);
        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1000, "");
    }

    private String buildSafeGroupNameForSolrSearch(String groupName)
    {
        if (AcmSolrUtil.hasSpecialCharacters(groupName))
        {
            groupName = "\"" + groupName + "\"";
        }
        groupName = groupName.replace("%", "%25"); // instead of URL encoding
        groupName = groupName.replace("&", "%26"); // instead of URL encoding
        groupName = groupName.replace("?", "%3F"); // instead of URL encoding
        return groupName;
    }

    @Override
    public List<AcmGroup> findByUserMember(AcmUser user)
    {
        return groupDao.findByUserMember(user);
    }

    @Override
    @Transactional
    public AcmGroup markGroupDeleted(String groupName) throws AcmObjectNotFoundException
    {
        return markGroupDeleted(groupName, false);
    }

    @Override
    @Transactional
    public AcmGroup markGroupDeleted(String groupName, boolean flushInstructions) throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = findByName(groupName);
        if (acmGroup == null)
        {
            throw new AcmObjectNotFoundException("GROUP", null, "Group with name " + groupName + " not found");
        }

        Assert.isTrue(acmGroup.getMemberOfGroups().isEmpty());

        AcmGroupType groupType = acmGroup.getType();
        Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);

        acmGroup.setAscendantsList(null);
        acmGroup.setStatus(AcmGroupStatus.DELETE);

        acmGroup.removeMembers();

        acmGroup.removeUserMembers();

        groupDao.deleteGroup(acmGroup);

        descendantGroups.forEach(group -> {
            String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ancestorsStringList);
            save(group);
        });

        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }

        if (groupType == AcmGroupType.ADHOC_GROUP)
        {
            acmGroupEventPublisher.publishAdHocGroupDeletedEvent(acmGroup);
        }
        return acmGroup;
    }

    @Override
    @Transactional
    public AcmGroup removeGroupMembership(String groupName, String parentGroupName) throws AcmObjectNotFoundException
    {
        return removeGroupMembership(groupName, parentGroupName, false);
    }

    @Override
    @Transactional
    public List<AcmGroup> removeGroupsMembership(String parentGroupName, List<String> subGroups) throws AcmObjectNotFoundException
    {
        List<AcmGroup> result = new ArrayList<>();
        subGroups.forEach(group -> {
            try
            {
                log.warn("Removing group [{}] from parent [{}]", group, parentGroupName);
                result.add(removeGroupFromParent(group, parentGroupName, false));
            }
            catch (AcmObjectNotFoundException e)
            {
                log.warn("Group [{}] cannot be removed", group);
            }
        });
        return result;
    }

    @Override
    @Transactional
    public AcmGroup removeGroupFromParent(String groupName, String parentGroupName, boolean flushInstructions)
            throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = findByName(groupName);
        AcmGroup parentGroup = findByName(parentGroupName);

        if (acmGroup == null || parentGroup == null)
        {
            String groupNotFound = acmGroup == null ? groupName : parentGroupName;
            log.warn("Group [{}] not found", groupNotFound);
            throw new AcmObjectNotFoundException("GROUP", null, "Group with name " + groupNotFound + " not found");
        }

        log.debug("Remove group member [{}] from group [{}]", groupName, parentGroupName);
        parentGroup.removeGroupMember(acmGroup);

        log.debug("Build ancestors string for group: [{}]", groupName);
        acmGroup.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(acmGroup));
        save(acmGroup);

        Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);

        descendantGroups.forEach(group -> {
            log.debug("Build ancestors string for descendants group: [{}]", groupName);
            String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
            group.setAscendantsList(ancestorsStringList);
            save(group);
        });

        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }
        return acmGroup;
    }

    @Override
    @Transactional
    public AcmGroup removeGroupMembership(String groupName, String parentGroupName, boolean flushInstructions)
            throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = findByName(groupName);
        AcmGroup parentGroup = findByName(parentGroupName);

        if (acmGroup == null || parentGroup == null)
        {
            String groupNotFound = acmGroup == null ? groupName : parentGroupName;
            log.warn("Group [{}] not found", groupNotFound);
            throw new AcmObjectNotFoundException("GROUP", null, "Group with name " + groupNotFound + " not found");
        }

        log.debug("Remove group member [{}] from group [{}]", groupName, parentGroupName);
        parentGroup.removeGroupMember(acmGroup);

        if (acmGroup.getMemberOfGroups().isEmpty())
        {
            log.debug("Group [{}] has no other parent groups, will be deleted", groupName);
            return markGroupDeleted(groupName);
        }
        else
        {
            log.debug("Build ancestors string for group: [{}]", groupName);
            acmGroup.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(acmGroup));
            save(acmGroup);

            Set<AcmGroup> descendantGroups = AcmGroupUtils.findDescendantsForAcmGroup(acmGroup);

            descendantGroups.forEach(group -> {
                log.debug("Build ancestors string for descendants group: [{}]", groupName);
                String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(group);
                group.setAscendantsList(ancestorsStringList);
                save(group);
            });

            if (flushInstructions)
            {
                groupDao.getEm().flush();
            }
            return acmGroup;
        }
    }

    @Override
    @Transactional
    public AcmGroup setSupervisor(AcmUser supervisor, String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.error("Failed to set supervisor to group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Set supervisor", "Group", -1L,
                    "Failed to set supervisor to group. Group " + groupId + " was not found.", null);
        }

        supervisor = userDao.findByUserId(supervisor.getUserId());

        group.setSupervisor(supervisor);

        if (applyToAll)
        {
            // TODO: Apply supervisors to all objects assigned to this group
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup addUserMembersToGroup(List<String> members, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = null;
        for (String userId : members)
        {
            AcmUser user = userDao.findByUserId(userId);
            if (user != null)
            {
                group = addUserMemberToGroup(user, groupId);
            }
            else
            {
                log.warn("User with id [{}] not found", userId);
            }
        }
        return group;
    }

    @Override
    public AcmGroup addUserMemberToGroup(AcmUser user, String groupId, boolean flushInstructions) throws AcmObjectNotFoundException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.warn("Group [{}] was not found.", groupId);
            throw new AcmObjectNotFoundException("GROUP", null, "Group " + groupId + " was not found");
        }

        Optional<AcmUser> foundUser = group.getUserMembers(true).stream().filter(u -> u.getUserId().equals(user.getUserId())).findFirst();
        if (foundUser.isPresent())
        {
            log.debug("User [{}] is already a member to the Group [{}]", user.getUserId(), group.getName());
            return group;
        }

        log.debug("Add User [{}] as member to Group [{}]", user.getUserId(), group.getName());
        group.addUserMember(user);

        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }
        return group;
    }

    @Override
    public AcmGroup addUserMemberToGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException
    {
        return addUserMemberToGroup(user, groupId, false);
    }

    @Override
    public AcmGroup removeSupervisor(String groupId, boolean applyToAll) throws AcmUserActionFailedException
    {
        AcmGroup group = groupDao.findByName(groupId);
        if (group == null)
        {
            log.error("Failed to remove supervisor from group. Group [{}] was not found.", groupId);
            throw new AcmUserActionFailedException("Remove Supervisor", "Group", -1L,
                    "Failed to remove supervisor from group. Group " + groupId + " was not found.", null);
        }
        group.setSupervisor(null);

        if (applyToAll)
        {
            // TODO: Remove supervisors from all objects assigned to this group
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMembersFromGroup(List<String> members, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = null;
        for (String user : members)
        {
            group = removeUserMemberFromGroup(user, groupId);
        }
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException
    {
        AcmGroup group = groupDao.findByName(groupId);

        if (group == null)
        {
            log.warn("Group [{}] was not found.", groupId);
            throw new AcmObjectNotFoundException("GROUP", null, "Group " + groupId + " was not found");
        }

        group.removeUserMember(user);
        return group;
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(String userId, String groupId) throws AcmObjectNotFoundException
    {
        AcmUser user = userDao.findByUserId(userId);
        if (user == null)
        {
            log.warn("User [{}] was not found.", userId);
            throw new AcmObjectNotFoundException("USER", null, "User " + userId + " was not found");
        }
        log.debug("Removing User [{}] from Group [{}]", user.getUserId(), groupId);
        return removeUserMemberFromGroup(user, groupId);
    }

    @Override
    @Transactional
    public AcmGroup removeUserMemberFromGroup(String userMember, String groupId, boolean flushInstructions)
            throws AcmObjectNotFoundException
    {
        AcmGroup acmGroup = removeUserMemberFromGroup(userMember, groupId);
        if (flushInstructions)
        {
            groupDao.getEm().flush();
        }

        return acmGroup;
    }

    @Override
    @Transactional
    public AcmGroup addGroupMember(String subGroupId, String parentId) throws AcmCreateObjectFailedException

    {
        AcmGroup parent = groupDao.findByName(parentId);
        AcmGroup subGroup = groupDao.findByName(subGroupId);

        if (parent == null || subGroup == null)
        {
            StringBuilder errorMessage = new StringBuilder();
            if (parent == null)
            {
                errorMessage.append("Parent group with id [").append(parentId).append("] not found.");
            }
            if (subGroup == null)
            {
                if (errorMessage.length() > 0)
                {
                    errorMessage.append(" ");
                }
                errorMessage.append("Subgroup with id [").append(subGroupId).append("] not found.");
            }

            throw new AcmCreateObjectFailedException("GROUP", errorMessage.toString(), null);
        }

        // If supervisor for the subgroup is empty, get from the parent group
        if (subGroup.getSupervisor() == null)
        {
            subGroup.setSupervisor(parent.getSupervisor());
        }

        parent.addGroupMember(subGroup);
        String ancestorsStringList = AcmGroupUtils.buildAncestorsStringForAcmGroup(subGroup);
        subGroup.setAscendantsList(ancestorsStringList);
        Set<AcmGroup> descendants = AcmGroupUtils.findDescendantsForAcmGroup(subGroup);
        descendants.forEach(group -> {
            group.setAscendantsList(AcmGroupUtils.buildAncestorsStringForAcmGroup(group));
            groupDao.save(group);
        });
        return subGroup;
    }

    @Override
    @Transactional
    public List<AcmGroup> addGroupMembers(String parentId, List<String> memberIds) throws AcmCreateObjectFailedException
    {
        List<AcmGroup> members = new ArrayList<>();
        for (String groupId : memberIds)
        {
            AcmGroup acmGroup = groupDao.findByName(groupId);
            if (acmGroup != null)
            {
                members.add(addGroupMember(groupId, parentId));
            }
            else
            {
                log.warn("Group with id [{}] not found", groupId);
            }
        }
        return members;
    }

    @Override
    @Transactional
    public AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId)
            throws AcmCreateObjectFailedException, AcmObjectAlreadyExistsException
    {
        AcmGroup parent = groupDao.findByName(parentId);
        if (parent == null)
        {
            throw new AcmCreateObjectFailedException("GROUP", "Parent group with id [" + parentId + "] not found", null);
        }

        // If supervisor for the subgroup is empty, get from the parent group
        if (subGroup.getSupervisor() == null)
        {
            subGroup.setSupervisor(parent.getSupervisor());
        }
        subGroup.setAscendantsList(parent.getAscendantsList());
        subGroup.addAscendant(parentId);
        AcmGroup acmGroup = createGroup(subGroup);
        parent.addGroupMember(acmGroup);
        return acmGroup;
    }

    @Override
    public String getGroupsByParent(String groupId, int startRow, int maxRows, String sort, Authentication auth)
            throws SolrException
    {
        groupId = buildSafeGroupNameForSolrSearch(groupId);
        String query = "ascendants_id_ss:" + groupId
                + " AND object_type_s:GROUP AND -status_lcs:COMPLETE AND -status_lcs:DELETE "
                + "AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query,
                startRow, maxRows, sort);
    }

    @Override
    public String getTopLevelGroups(List<String> groupSubtype, int startRow, int maxRows, String sort, Authentication auth)
            throws SolrException
    {
        String query = "object_type_s:GROUP AND -ascendants_id_ss:* AND -status_lcs:COMPLETE AND -status_lcs:DELETE "
                + "AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        if (groupSubtype != null && !groupSubtype.isEmpty())
        {
            query += " AND object_sub_type_s:(" + String.join(" OR ", groupSubtype) + ")";
        }
        return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query,
                startRow, maxRows, sort);
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public void setAcmGroupEventPublisher(AcmGroupEventPublisher acmGroupEventPublisher)
    {
        this.acmGroupEventPublisher = acmGroupEventPublisher;
    }

    @Override
    public boolean isUserMemberOfGroup(String userId, String groupName)
    {
        AcmUser user = userDao.findByUserId(userId);
        List<AcmGroup> groups = findByUserMember(user);

        for (AcmGroup group : groups)
        {
            if (group.getName().equals(groupName))
            {
                return true;
            }

            // check ascendant groups
            Set<String> ascendantGroupNames = group.getAscendants();
            for (String ascendantGroupName : ascendantGroupNames)
            {
                if (ascendantGroupName.equals(groupName))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
