package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.auth.LoginEvent;

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

public class OnLoginCheckPasswordExpiration implements ApplicationListener<LoginEvent>
{
    private ResetPasswordService resetPasswordService;
    private final Logger log = LogManager.getLogger(getClass());
    private UserDao userDao;
    
    @Override
    public void onApplicationEvent(LoginEvent loginEvent)
    {
        if (!loginEvent.isSucceeded())
        {
            Authentication authentication = loginEvent.getAuthentication();
            if (authentication != null)
            {
                log.debug("Checking password expiration for user: [{}]", authentication.getName());
                if (resetPasswordService.isUserPasswordExpired(authentication.getName()))
                {
                    log.debug("Password for user [{}] is expired", authentication.getName());
                    AcmUser user = userDao.findByUserId(authentication.getName());
                    resetPasswordService.sendPasswordResetNotification(user);
                }
            }
        }
    }

    public void setResetPasswordService(ResetPasswordService resetPasswordService)
    {
        this.resetPasswordService = resetPasswordService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
