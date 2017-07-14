package com.armedia.acm.services.users.model.ldap;

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
