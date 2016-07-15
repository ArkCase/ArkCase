/**
 *
 */
package com.armedia.acm.services.users.service.group;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.mule.api.MuleException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Set;

/**
 * @author riste.tutureski
 */
public interface GroupService
{

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
     * @param str
     * @return true or false
     */
    boolean isUUIDPresentInTheGroupName(String str);
}
