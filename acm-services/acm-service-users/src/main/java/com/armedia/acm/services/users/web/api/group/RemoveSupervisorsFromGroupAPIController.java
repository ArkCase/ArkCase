/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

import java.util.ArrayList;
import java.util.List;

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
public class RemoveSupervisorsFromGroupAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
	@RequestMapping(value="/group/{groupId}/supervisors/remove/{applyToAll}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeMembersFromGroup(@RequestBody List<AcmUser> supervisors,
								    		   @PathVariable("groupId") String groupId, 
								    		   @PathVariable("applyToAll") boolean applyToAll,
											   Authentication auth) throws AcmUserActionFailedException
    {		
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Removing supervisors from the group with ID = " + groupId);
		}
		
		try
		{
			AcmGroup group = getGroupDao().findByName(groupId);
			
			List<AcmUser> cleanedSupervisors = cleanSupervisors(group.getSupervisors(), supervisors);
			group.setSupervisors(cleanedSupervisors);
			
			AcmGroup saved = getGroupDao().save(group);
			
			if (applyToAll) 
			{
				// TODO: Remove supervisors from all objects assigned to this group
			}
			
			return saved;
		}
		catch(Exception e)
		{
			LOG.error("Failed to remove supervisors from the group with ID = " + groupId, e);
			throw new AcmUserActionFailedException("Remove Supervisors", "Group", -1L, e.getMessage(), e);
		}
    }
	
	private List<AcmUser> cleanSupervisors(List<AcmUser> supervisors, List<AcmUser> toRemoveArray)
	{
		List<AcmUser> result = new ArrayList<AcmUser>();
		
		if (toRemoveArray != null && toRemoveArray.size() > 0 && supervisors != null && supervisors.size() > 0)
		{
			for (AcmUser supervisor : supervisors)
			{
				boolean found = false;
				
				for (AcmUser toRemove : toRemoveArray)
				{
					if (supervisor.getUserId().equals(toRemove.getUserId()))
					{
						found = true;
					}
				}
				
				if (!found)
				{
					result.add(supervisor);
				}
			}
		}
		
		return result;
	}
	

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
}
