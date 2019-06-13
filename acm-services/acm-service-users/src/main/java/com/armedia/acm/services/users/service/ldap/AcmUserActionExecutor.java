/**
 * 
 */
package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.services.users.dao.UserActionDao;
import com.armedia.acm.services.users.model.AcmUserAction;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author riste.tutureski
 *
 */
public class AcmUserActionExecutor
{

    private Logger LOG = LogManager.getLogger(getClass());
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
    public UserActionDao getUserActionDao()
    {
        return userActionDao;
    }

    /**
     * @param userActionDao
     *            the userActionDao to set
     */
    public void setUserActionDao(UserActionDao userActionDao)
    {
        this.userActionDao = userActionDao;
    }

}
