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
     * Retrieve all LDAP groups that a user belongs to
     *
     * @param usernamePasswordAuthenticationToken
     * @return LDAP groups
     */
    String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException;

    /**
     * Retrieve all LDAP groups that a user belongs to
     *
     * @param auth
     * @return LDAP groups
     */
    String test(Authentication auth, String searchFilter, String sortBy, String sortDirection, int startRow, int maxRows)
            throws MuleException;

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
    AcmGroup removeGroupMembership(String groupName, String parentGroupName, boolean flushInstructions) throws AcmObjectNotFoundException;

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
}
