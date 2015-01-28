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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.group.AcmGroup;

/**
 * @author riste.tutureski
 *
 */
@Controller
@RequestMapping({ "/api/v1/users", "/api/latest/users" })
public class RemoveGroupAPIController {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AcmGroupDao groupDao;
	
	@RequestMapping(value="/group/{groupId}/remove", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmGroup removeGroup(@PathVariable("groupId") String groupId, Authentication auth) throws AcmUserActionFailedException
    {
		if (LOG.isInfoEnabled()) 
		{
			LOG.info("Removing ad-hoc group with ID = " + groupId);
		}
		
		try
		{
			AcmGroup retval = getGroupDao().markGroupDelete(groupId);

			return retval;
		}
		catch(Exception e)
		{
			throw new AcmUserActionFailedException("Delete", "Group", -1L, e.getMessage(), e);
		}
		
    }

	public AcmGroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(AcmGroupDao groupDao) {
		this.groupDao = groupDao;
	}
	
}
