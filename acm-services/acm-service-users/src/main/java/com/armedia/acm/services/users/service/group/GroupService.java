/**
 * 
 */
package com.armedia.acm.services.users.service.group;

import java.util.Set;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import org.mule.api.MuleException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * @author riste.tutureski
 *
 */
public interface GroupService {

	/**
	 * Add members to the group
	 * 
	 * @param group
	 * @param members
	 * @return
	 */
	public AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members);
	
	/**
	 * UI will send users and we need to take them from database (because users sent from UI don't have some of information)
	 * 
	 * @param members
	 * @return
	 */
	public Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members);

	/**
	 * Retrieve all LDAP groups that a user belongs to
	 *
	 * @param usernamePasswordAuthenticationToken
	 * @return LDAP groups
	 */
	public String getLdapGroupsForUser(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws MuleException;
}
