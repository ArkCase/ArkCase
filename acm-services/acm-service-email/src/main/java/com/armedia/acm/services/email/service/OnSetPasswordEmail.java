package com.armedia.acm.services.email.service;

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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.event.SetPasswordEmailEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class OnSetPasswordEmail implements ApplicationListener<SetPasswordEmailEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ResetPasswordService resetPasswordService;

    @Override
    public void onApplicationEvent(SetPasswordEmailEvent setPasswordEmailEvent)
    {
        if (setPasswordEmailEvent.isSucceeded())
        {
            log.debug("On set password mail event...");
            AcmUser user = setPasswordEmailEvent.getAcmUser();
            resetPasswordService.sendPasswordResetEmail(user);
        }
    }

    public void setResetPasswordService(ResetPasswordService resetPasswordService)
    {
        this.resetPasswordService = resetPasswordService;
    }
}
