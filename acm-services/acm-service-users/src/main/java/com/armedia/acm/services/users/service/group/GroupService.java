package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
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

    AcmGroup findByMatchingName(String name);

    AcmGroup save(AcmGroup groupToSave);

    AcmGroup saveAndFlush(AcmGroup group);

    /**
     * Retrieve all LDAP groups that a user belongs to
     *
     * @param usernamePasswordAuthenticationToken
     * @return LDAP groups
     */
    String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException;

    /**
     * Checks if group with same name exists in the DB on the same tree level
     *
     * @param group
     * @return The new saved group or null if group with given name already exists in the same tree level
     */
    AcmGroup checkAndSaveAdHocGroup(AcmGroup group);

    /**
     * Checks if given string matches the regex .*-UUID-[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}
     *
     * @param str
     * @return true or false
     */
    boolean isUUIDPresentInTheGroupName(String str);

    /**
     * @param groupName  list users for this specific group
     * @param userStatus optional value for "status_lcs" field to be included in the solr query
     * @return solr results for user members in specific group
     * @throws MuleException
     */
    String getUserMembersForGroup(String groupName, Optional<String> userStatus, Authentication auth) throws MuleException;

    /**
     * Creates or updates ad-hoc group based on the client info coming in from CRM
     *
     * @param acmGroup group we want to rename
     * @param newName  group new name
     */
    void renameGroup(AcmGroup acmGroup, String newName) throws AcmObjectNotFoundException;

    List<AcmGroup> findByUserMember(AcmUser user);

    /**
     * AcmGroups are not deleted from the system. This method sets the group with status
     * {@link com.armedia.acm.services.users.model.group.AcmGroupStatus#DELETE} and relations to
     * groups, users and roles for users per the target group are removed.
     *
     * @param groupId name of the group
     * @return group with updated status, ancestors and removed user and group relations
     * @throws AcmObjectNotFoundException in case group with groupId is not found
     */
    AcmGroup markGroupDeleted(String groupId) throws AcmObjectNotFoundException;

    /**
     * AcmGroups are not deleted from the system. This method sets the group with status
     * {@link com.armedia.acm.services.users.model.group.AcmGroupStatus#DELETE} and relations to
     * groups, users and roles for users per the target group are removed.
     *
     * @param groupId           name of the group
     * @param flushInstructions if set to true there is an explicit flush before the end of the method
     * @return group with updated status, ancestors and removed user and group relations
     * @throws AcmObjectNotFoundException in case group with groupId is not found
     */
    AcmGroup markGroupDeleted(String groupId, boolean flushInstructions) throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is
     * not member to any other group, the group is deleted.
     *
     * @param groupName       name of the group to be removed
     * @param parentGroupName name of the parent group
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException in case group with groupName or parentGroupName is not found
     */
    AcmGroup removeGroupMembership(String groupName, String parentGroupName) throws AcmObjectNotFoundException;

    /**
     * Removes group membership to the given parent group. In case this group is
     * not member to any other group, the group is deleted.
     *
     * @param groupName         name of the group to be removed
     * @param parentGroupName   name of the parent group
     * @param flushInstructions if set to true there is an explicit flush before the end of the method
     * @return updated AcmGroup
     * @throws AcmObjectNotFoundException in case group with groupName or parentGroupName is not found
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

    AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId) throws AcmCreateObjectFailedException;
}
