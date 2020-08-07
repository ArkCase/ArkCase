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
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.springframework.security.core.Authentication;

import javax.persistence.FlushModeType;

import java.util.List;
import java.util.Optional;

/**
 * @author riste.tutureski
 */
public interface GroupService
{
    AcmGroup findByName(String name);

    AcmGroup findByName(String name, FlushModeType flushModeType);

    AcmGroup save(AcmGroup group);

    AcmGroup saveAndFlush(AcmGroup group);

    AcmGroup createGroup(AcmGroup group) throws AcmObjectAlreadyExistsException;

    /**
     * Retrieve all groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     * 
     * @return groups
     */
    String buildGroupsSolrQuery() throws SolrException;

    /**
     * Retrieve all groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     *
     * @return groups
     */
    String buildGroupsSolrQuery(Authentication auth, Integer startRow, Integer maxRows, String sortBy, String sortDirection)
            throws SolrException;

    /**
     * Retrieve all adHoc groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     *
     * @return groups
     */
    String buildGroupsAdHocSolrQuery() throws SolrException;

    /**
     * Retrieve all adHoc groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     *
     * @return groups
     */
    String buildGroupsAdHocByNameSolrQuery(String fq) throws SolrException;

    /**
     * Retrieve groups by name
     *
     * @params fq
     *
     * @return groups
     */
    String buildGroupsByNameSolrQuery(String fq) throws SolrException;

    /**
     * Retrieve all groups that belongs to specific group type
     *
     * @params type,
     *         memberId, searchFilter
     *
     * @return groups
     */
    String buildGroupsForUserByNameSolrQuery(Boolean authorized, String memberId, String searchFilter) throws SolrException;

    /**
     * Retrieve all groups that belongs to specific group type
     *
     * @params auth, startRow, maxRows, sortBy, sortDirection, authorized, groupId, searchFilter groupDirectory,
     *         groupType
     *
     * @return groups
     */
    String getAdHocMemberGroupsByMatchingName(Authentication auth, Integer startRow, Integer maxRows, String sortBy,
            String sortDirection,
            Boolean authorized, String groupId, String searchFilter, String groupType) throws SolrException;

    /**
     * Retrieve all groups that a user belongs to
     *
     * @params type,
     *         userId
     * @return groups
     */
    String buildGroupsForUserSolrQuery(Boolean authorized, String userId) throws SolrException;

    /**
     * Retrieve all groups that a group belongs to
     *
     * @params auth, startRow, maxRows, sortBy, sortDirection, authorized, groupId, groupDirectory, groupType
     * 
     * @return groups
     */
    String getAdHocMemberGroups(Authentication auth, Integer startRow, Integer maxRows, String sortBy, String sortDirection,
            Boolean authorized,
            String groupId, String groupType) throws SolrException;

    /**
     * Returns solr search results for GROUP filtered by name
     * 
     * @param authentication
     * @param nameFilter
     * @param start
     * @param max
     * @param sortBy
     * @param sortDir
     * @return groups
     * @throws SolrException
     */
    String getGroupsByNameFilter(Authentication authentication, String directoryName, String nameFilter, int start, int max, String sortBy,
            String sortDir)
            throws SolrException;

    /**
     * Retrieve all LDAP groups that a user belongs to
     *
     * @param authentication
     * @return LDAP groups
     */
    String getLdapGroupsForUser(Authentication authentication) throws SolrException;

    /**
     * Returns true if the user is a member of the given group
     * 
     * @param userId
     *            the user id
     * @param groupName
     *            the group name
     * @return
     *         true if the user is a member of the given group, false otherwise.
     */
    boolean isUserMemberOfGroup(String userId, String groupName);

    /**
     * @param groupName
     *            list users for this specific group
     * @param userStatus
     *            optional value for "status_lcs" field to be included in the solr query
     * @return solr results for user members in specific group
     * @throws SolrException
     */
    String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws SolrException;

    List<AcmGroup> findByUserMember(AcmUser user);

    /**
     * AcmGroups are not deleted from the system. This method sets the group with status
     * {@link com.armedia.acm.services.users.model.group.AcmGroupStatus#DELETE} and relations to groups, users and roles
     * for users per the target group are removed.
     *
     * @param groupId
     *            name of the group
     * @return group with updated status, ancestors and removed user and group relations
     * @throws AcmObjectNotFoundException
     *             in case group with groupId is not found
     */
    AcmGroup markGroupDeleted(String groupId) throws AcmObjectNotFoundException;

    /**
     * AcmGroups are not deleted from the system. This method sets the group with status
     * {@link com.armedia.acm.services.users.model.group.AcmGroupStatus#DELETE} and relations to groups, users and roles
     * for users per the target group are removed.
     *
     * @param groupId
     *            name of the group
     * @param flushInstructions
     *            if set to true there is an explicit flush before the end of the method
     * @return group with updated status, ancestors and removed user and group relations
     * @throws AcmObjectNotFoundException
     *             in case group with groupId is not found
     */
    AcmGroup markGroupDeleted(String groupId, boolean flushInstructions) throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is not member to any other group, the
     * group is deleted.
     *
     * @param groupName
     *            name of the group to be removed
     * @param parentGroupName
     *            name of the parent group
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException
     *             in case group with groupName or parentGroupName is not found
     */
    AcmGroup removeGroupMembership(String groupName, String parentGroupName) throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is not member to any other group, the
     * group is deleted.
     *
     * @param parentGroupName
     *            name of the parent group
     * @param subGroups
     *            name of the groups to be removed
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException
     *             in case group with groupName or parentGroupName is not found
     */
    List<AcmGroup> removeGroupsMembership(String parentGroupName, List<String> subGroups) throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is not member to any other group, the
     * group is deleted.
     *
     * @param groupName
     *            name of the group to be removed
     * @param parentGroupName
     *            name of the parent group
     * @param flushInstructions
     *            if set to true there is an explicit flush before the end of the method
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException
     *             in case group with groupName or parentGroupName is not found
     */
    AcmGroup removeGroupMembership(String groupName, String parentGroupName, boolean flushInstructions)
            throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is not member to any other group, the
     * group is deleted.
     *
     * @param groupName
     *            name of the group to be removed
     * @param parentGroupName
     *            name of the parent group
     * @param flushInstructions
     *            if set to true there is an explicit flush before the end of the method
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException
     *             in case group with groupName or parentGroupName is not found
     */
    AcmGroup removeGroupFromParent(String groupName, String parentGroupName, boolean flushInstructions)
            throws AcmObjectNotFoundException;

    AcmGroup setSupervisor(AcmUser supervisor, String groupId, boolean applyToAll) throws AcmUserActionFailedException;

    AcmGroup addUserMemberToGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException;

    AcmGroup addUserMemberToGroup(AcmUser user, String groupId, boolean flushInstructions) throws AcmObjectNotFoundException;

    AcmGroup removeSupervisor(String groupId, boolean applyToAll) throws AcmUserActionFailedException;

    AcmGroup addUserMembersToGroup(List<String> members, String groupId) throws AcmObjectNotFoundException;

    AcmGroup removeUserMembersFromGroup(List<String> members, String groupId) throws AcmObjectNotFoundException;

    AcmGroup removeUserMemberFromGroup(String userMember, String groupId) throws AcmObjectNotFoundException;

    AcmGroup removeUserMemberFromGroup(String userMember, String groupId, boolean flushInstructions) throws AcmObjectNotFoundException;

    AcmGroup removeUserMemberFromGroup(AcmUser user, String groupId) throws AcmObjectNotFoundException;

    /**
     * Saves new ADHOC group and adds it as member to parent group.
     *
     * @param subGroup
     *            group to be created
     * @param parentId
     *            name of the parent group
     * @return new AcmGroup
     * @throws AcmCreateObjectFailedException
     *             in case when parent group is not found
     * @throws AcmObjectAlreadyExistsException
     *             in case when this group already exists
     */
    AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId) throws AcmCreateObjectFailedException, AcmObjectAlreadyExistsException;

    /**
     * Adds group as member to parent group
     *
     * @param subGroupId
     *            member group name
     * @param parentId
     *            parent group name
     * @return updated group member AcmGroup
     * @throws AcmCreateObjectFailedException
     *             in case when subgroup or parent group are not found
     */
    AcmGroup addGroupMember(String subGroupId, String parentId) throws AcmCreateObjectFailedException;

    /**
     * Adds group as member to parent group
     *
     * @param parentId
     *            parent group name
     * @param memberIds
     *            member groups names
     * @return list of member groups
     * @throws AcmCreateObjectFailedException
     *             in case when subgroup or parent group are not found
     */
    List<AcmGroup> addGroupMembers(String parentId, List<String> memberIds) throws AcmCreateObjectFailedException;

    /**
     * retrive groups for given parent group id
     *
     * @param groupId
     *            parent group id
     * @param startRow
     *            start row
     * @param maxRows
     * @param sort
     * @param auth
     * @return
     * @throws SolrException
     */
    String getGroupsByParent(String groupId, int startRow, int maxRows, String sort, Authentication auth) throws SolrException;

    String getTopLevelGroups(List<String> groupSubtype, int startRow, int maxRows, String sort, Authentication auth, String directoryName)
            throws SolrException;
}
