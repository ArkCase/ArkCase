package com.armedia.acm.services.users.model.ldap;

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

import com.armedia.acm.services.users.model.AcmUsersConstants;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by sharmilee.sivakumaran on 6/12/17.
 */
public class PasswordShouldNotContainUserId implements PasswordValidationRule
{
    @Override
    public String runValidationAndGetMessage(String username, String password)
    {
        if (StringUtils.isNotBlank(username))
        {
            // If the samAccountName (username) is less than three characters long, this check is skipped.
            // https://technet.microsoft.com/en-us/library/cc786468(v=ws.10).aspx
            if (username.length() > AcmUsersConstants.USER_ID_MIN_CHAR_LENGTH)
            {
                if (password.contains(username))
                {
                    return "Password cannot contain username.";
                }
            }
        }
        return null;
    }
}
