package com.armedia.acm.services.users.state;

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

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;
import com.armedia.acm.services.users.dao.UserDao;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.LocalDate;

public class AcmUsersStateProvider implements StateOfModuleProvider
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private UserDao userDao;

    @Override
    public String getModuleName()
    {
        return "acm-users";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmUsersState acmUsersState = new AcmUsersState();
        try
        {
            acmUsersState.setNumberOfUsers(userDao.getUserCount(day.atTime(23, 59, 59)));
        }
        catch (Exception e)
        {
            log.error("Not able to provide users state.", e.getMessage());
            acmUsersState.addProperty("error", "Not able to provide status." + e.getMessage());
        }
        return acmUsersState;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
