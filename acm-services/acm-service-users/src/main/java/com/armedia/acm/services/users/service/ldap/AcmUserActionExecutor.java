/**
 * 
 */
package com.armedia.acm.services.users.service.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import com.armedia.acm.services.users.dao.ldap.UserActionDao;
import com.armedia.acm.services.users.model.AcmUserAction;
import com.armedia.acm.services.users.model.AcmUserActionName;

/**
 * @author riste.tutureski
 *
 */
public class AcmUserActionExecutor {
	
	private Logger LOG = LoggerFactory.getLogger(getClass());
	private UserActionDao userActionDao;

	public boolean execute(Long objectId, String actionName, String userId)
	{
		LOG.info("Last user action: ObjectId: " + objectId + ", Action: " + actionName + ", User: " + userId);
		
		boolean success = true;
		try
		{
			// Record user action
			AcmUserAction userAction = getUserActionDao().findByUserIdAndName(userId, actionName);
			
			if (null == userAction)
			{
				userAction = new AcmUserAction();
			}
			
			userAction.setUserId(userId);
			userAction.setObjectId(objectId);
			userAction.setName(actionName);
			
			getUserActionDao().save(userAction);
		}
		catch (Exception e)
		{
			LOG.error("The user action cannot be saved!", e);
			success = false;
		}
		
		return success;
	}
	
	/**
	 * @return the userActionDao
	 */
	public UserActionDao getUserActionDao() {
		return userActionDao;
	}

	/**
	 * @param userActionDao the userActionDao to set
	 */
	public void setUserActionDao(UserActionDao userActionDao) {
		this.userActionDao = userActionDao;
	}
	
	

}