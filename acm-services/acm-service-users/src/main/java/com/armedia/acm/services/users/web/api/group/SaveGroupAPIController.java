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
			subGroup.setParentGroup(parent);
			
			// If supervisors for the subgroup us empty, get from the parent group
			if (subGroup.getSupervisors() == null || subGroup.getSupervisors().size() == 0) {
				subGroup.setSupervisors(parent.getSupervisors());
			}
			
			AcmGroup saved = getGroupDao().save(subGroup);
			
			return saved;
		}
		catch(Exception e)
		{
			throw new AcmCreateObjectFailedException("Group", e.getMessage(), e);
		}
		
    }

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
}
