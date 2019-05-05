package com.armedia.acm.services.notification.service.provider;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import java.util.List;
import java.util.stream.Collectors;

public class ForgotUsernameTemplateModelProvider implements TemplateModelProvider<Notification>
{

    private UserDao userDao;

    @Override
    public Notification getModel(Object object)
    {
        Notification notification = (Notification) object;
        List<AcmUser> users = userDao.findByEmailAddress(notification.getUser());
        users = users.stream()
                .filter(user -> user.getUserState() == AcmUserState.VALID)
                .collect(Collectors.toList());
        List<String> userAccounts = users.stream()
                .map(AcmUser::getUserId)
                .collect(Collectors.toList());
        notification.setAccountsNumber(users.size());
        notification.setUserAccounts(String.join(",", userAccounts));
        return notification;
    }

    @Override
    public Class<Notification> getType()
    {
        return Notification.class;
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
