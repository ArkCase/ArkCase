package com.armedia.acm.services.users.service.group;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.mule.api.MuleException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author riste.tutureski
 */
public interface GroupService
{
    AcmGroup findByName(String name);

    AcmGroup save(AcmGroup groupToSave);

    /**
     * Add members to the group
     *
     * @param group
     * @param members
     * @return
     */
    AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members);

    /**
     * UI will send users and we need to take them from database (because users sent from UI don't have some of information)
     *
     * @param members
     * @return
     */
    Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members);

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
     * @param groupName list users for this specific group
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
    void renameGroup(AcmGroup acmGroup, String newName);

    List<AcmGroup> findByUserMember(AcmUser user);

    AcmGroup markGroupDeleted(String groupId);

    AcmGroup setSupervisor(AcmUser supervisor, String groupId, boolean applyToAll) throws AcmUserActionFailedException;

    AcmGroup addMembersToAdHocGroup(Set<AcmUser> members, String groupId) throws AcmUserActionFailedException;

    AcmGroup removeSupervisor(String groupId, boolean applyToAll) throws AcmUserActionFailedException;

    AcmGroup removeMembersFromAdHocGroup(Set<AcmUser> members, String groupId);

    AcmGroup saveAdHocSubGroup(AcmGroup subGroup, String parentId) throws AcmCreateObjectFailedException;
}
