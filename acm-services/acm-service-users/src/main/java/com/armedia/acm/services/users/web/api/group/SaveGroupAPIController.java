/**
 * 
 */
package com.armedia.acm.services.users.web.api.group;

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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupStatus;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class SaveGroupAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
	@RequestMapping(value="/group/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveGroup(@RequestBody AcmGroup group, Authentication auth) throws AcmCreateObjectFailedException
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving ad-hoc group " + group);
		}
		
		try
		{
			// Check if the group already exist
			group = checkGroupExistance(group, auth);
			group.setParentGroup(null);
			
			AcmGroup saved = getGroupDao().save(group);
			
			return saved;
		}
		catch(Exception e)
		{
			throw new AcmCreateObjectFailedException("Group", e.getMessage(), e);
		}
		
    }
	
	@RequestMapping(value="/group/save/{parentId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup saveSubGroup(@RequestBody AcmGroup subGroup, 
    							   @PathVariable("parentId") String parentId, 
    							   Authentication auth) throws AcmCreateObjectFailedException
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Saving ad-hoc subgroup " + subGroup);
		}
		
		try
		{
			AcmGroup parent = getGroupDao().findByName(parentId);
			
			// If supervisor for the subgroup is empty, get from the parent group
			if (subGroup.getSupervisor() == null) {
				subGroup.setSupervisor(parent.getSupervisor());
			}
			
			// Check if the group already exist
			subGroup = checkGroupExistance(subGroup, auth);
			subGroup.setParentGroup(parent);
			
			AcmGroup saved = getGroupDao().save(subGroup);
			
			return saved;
		}
		catch(Exception e)
		{
			throw new AcmCreateObjectFailedException("Group", e.getMessage(), e);
		}
		
    }
	
	private AcmGroup checkGroupExistance(AcmGroup group, Authentication auth)
	{
		AcmGroup found = getGroupDao().findByName(group.getName());
		if (found != null)
		{
			if (AcmGroupStatus.DELETE.equals(found.getStatus()))
			{
				// If group already exist and it's deleted, set only creator and modifier 
				// (all other fields are set from AcmGroup.beforeInsert(..) and AcmGroup.beforeUpdate(..) methods)
				group.setCreator(auth.getName());
				group.setModifier(auth.getName());
			}
			else
			{
				// If it's not deleted, just proceed with what we found 
				// (we don't want to lose existing group if we are trying to create a group that already exist)
				group = found;
			}
		}
		
		return group;
	}

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
}
