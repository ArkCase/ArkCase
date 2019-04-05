package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by Vladimir Cherepnalkovski
 */
public class TranscribeSystemUserUpdateExecutor implements AcmDataUpdateExecutor
{

    private UserDao userDao;

    @Override
    public String getUpdateId()
    {
        return "system-user-transcribe-insert";
    }

    @Transactional
    @Override
    public void execute()
    {
        if (getUserDao().findByUserId("TRANSCRIBE_SERVICE") == null)
        {
            AcmUser user = new AcmUser();
            user.setUserId("TRANSCRIBE_SERVICE");
            user.setUserDirectoryName("");
            user.setFullName("TRANSCRIBE_SERVICE");
            user.setCreated(new Date());
            user.setModified(new Date());
            user.setUserState(AcmUserState.VALID);
            user.setFirstName("TRANSCRIBE");
            user.setLastName("SERVICE");
            user.setMail("TRANSCRIBE_SERVICE");
            user.setUid("TRANSCRIBE_SERVICE");
            getUserDao().save(user);
        }
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
