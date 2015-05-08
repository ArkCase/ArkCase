/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.service.group.GroupService;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class SaveMembersToGroupAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	private UserDao userDao;
	private GroupService groupService;
	
	@RequestMapping(value="/group/{groupId}/members/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveSupervisorsToGroup(@RequestBody Set<AcmUser> members,
								    		   @PathVariable("groupId") String groupId, 
											   Authentication auth) throws AcmUserActionFailedException
    {		
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving members to the group with ID = " + groupId);
		}
		
		try
		{
			AcmGroup group = getGroupDao().findByName(groupId);
			Set<AcmUser> updatedMembers = getGroupService().updateMembersWithDatabaseInfo(members);
			group = getGroupService().updateGroupWithMembers(group, updatedMembers);
			
			// Add members for all parent groups
			AcmGroup parent = group.getParentGroup();
			while (parent != null)
			{				
				parent = getGroupService().updateGroupWithMembers(parent, updatedMembers);
				getGroupDao().save(parent);
				
				parent = parent.getParentGroup();
			}
			
			AcmGroup saved = getGroupDao().save(group);
			
			return saved;
		}
		catch(Exception e)
		{
			LOG.error("Failed to save members to the group with ID = " + groupId, e);
			throw new AcmUserActionFailedException("Save Members", "Group", -1L, e.getMessage(), e);
		}
    }	

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
		
}
