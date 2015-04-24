/**
 * 
 */
package com.armedia.acm.services.users.service.group;

import java.util.HashSet;
import java.util.Set;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
public class GroupServiceImpl implements GroupService {

	private UserDao userDao;
	
	@Override
	public AcmGroup updateGroupWithMembers(AcmGroup group, Set<AcmUser> members) 
	{
		if (members != null)
		{
			for (AcmUser member : members)
			{
				group.addMember(member);
			}
		}
		
		return group;
	}
	
	@Override
	public Set<AcmUser> updateMembersWithDatabaseInfo(Set<AcmUser> members)
	{
		Set<AcmUser> updatedMembers = new HashSet<>();
		
		if (members != null)
		{
			for (AcmUser member : members)
			{
				AcmUser updatedMember = getUserDao().findByUserId(member.getUserId());
				
				if (updatedMember != null)
				{
					updatedMembers.add(updatedMember);
				}
			}
		}
		
		return updatedMembers;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
