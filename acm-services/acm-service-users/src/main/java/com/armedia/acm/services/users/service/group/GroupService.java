package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectAlreadyExistsException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

import org.mule.api.MuleException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

/**
 * @author riste.tutureski
 */
public interface GroupService
{
    AcmGroup findByName(String name);

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
    String buildGroupsSolrQuery() throws MuleException;

    /**
     * Retrieve all adHoc groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     *
     * @return groups
     */
    String buildGroupsAdHocSolrQuery() throws MuleException;

    /**
     * Retrieve all adHoc groups
     *
     * @params startRow,
     *         maxRows, sortDirection
     *
     * @return groups
     */
    String buildGroupsAdHocByNameSolrQuery(String fq) throws MuleException;

    /**
     * Retrieve groups by name
     *
     * @params fq
     *
     * @return groups
     */
    String buildGroupsByNameSolrQuery(String fq) throws MuleException;

    /**
     * Retrieve all groups that belongs to specific group type
     *
     * @params type,
     *         memberId, searchFilter
     *
     * @return groups
     */
    String buildGroupsForUserByNameSolrQuery(Boolean authorized, String memberId, String searchFilter) throws MuleException;

    /**
     * Retrieve all groups that belongs to specific group type
     *
     * @params type,
     *         memberId, searchFilter
     *
     * @return groups
     */
    String buildGroupsForGroupByNameSolrQuery(Boolean authorized, String memberId, String searchFilter) throws MuleException;

    /**
     * Retrieve all groups that a user belongs to
     *
     * @params type,
     *         userId
     * @return groups
     */
    String buildGroupsForUserSolrQuery(Boolean authorized, String userId) throws MuleException;

    /**
     * Retrieve all groups that a group belongs to
     *
     * @params type,
     *         groupId
     * @return groups
     */
    String buildGroupsForGroupSolrQuery(Boolean authorized, String groupId) throws MuleException;

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
     * @throws MuleException
     */
    String getGroupsByNameFilter(Authentication authentication, String nameFilter, int start, int max, String sortBy, String sortDir)
            throws MuleException;

    /**
     * Retrieve all LDAP groups that a user belongs to
     *
     * @param usernamePasswordAuthenticationToken
     * @return LDAP groups
     */
    String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException;

    /**
     * @param groupName
     *            list users for this specific group
     * @param userStatus
     *            optional value for "status_lcs" field to be included in the solr query
     * @return solr results for user members in specific group
     * @throws MuleException
     */
    String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws MuleException;

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
    AcmGroup removeGroupMembershipp(String groupName, String parentGroupName, boolean flushInstructions)
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
     * @throws MuleException
     */
    String getGroupsByParent(String groupId, int startRow, int maxRows, String sort, Authentication auth) throws MuleException;

    String getTopLevelGroups(List<String> groupSubtype, int startRow, int maxRows, String sort, Authentication auth)
            throws MuleException;
}
