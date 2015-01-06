/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

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
public class SaveSupervisorToGroupAPIController {

	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
	@RequestMapping(value="/group/{groupId}/supervisor/save/{applyToAll}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup addSupervisorsToGroup(@RequestBody AcmUser supervisor,
							    		   @PathVariable("groupId") String groupId, 
							    		   @PathVariable("applyToAll") boolean applyToAll,
										   Authentication auth) throws AcmUserActionFailedException
    {		
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving supervisors to the group with ID = " + groupId);
		}
		
		try
		{
			AcmGroup group = getGroupDao().findByName(groupId);
			
			group.setSupervisor(supervisor);
			
			AcmGroup saved = getGroupDao().save(group);
			
			if (applyToAll) 
			{
				// TODO: Apply supervisors to all objects assigned to this group
			}
			
			return saved;
		}
		catch(Exception e)
		{
			LOG.error("Failed to add supervisors to the group with ID = " + groupId, e);
			throw new AcmUserActionFailedException("Save Supervisors", "Group", -1L, e.getMessage(), e);
		}
    }
	

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
}
