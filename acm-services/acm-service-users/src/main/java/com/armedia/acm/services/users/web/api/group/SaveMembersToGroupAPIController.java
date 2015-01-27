/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import java.util.HashSet;
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
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class SaveMembersToGroupAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
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
			
			if (group.getMembers() == null)
			{
				group.setMembers(new HashSet<AcmUser>());
			}
			
			group.getMembers().addAll(members);
			
			// Add members for all parent groups
			AcmGroup parent = group.getParentGroup();
			while (parent != null)
			{
				if (parent.getMembers() == null)
				{
					parent.setMembers(new HashSet<AcmUser>());
				}
				
				parent.getMembers().addAll(members);
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
	
}
