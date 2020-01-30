/*
 * #%L
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail. Otherwise, the software is
 * provided under the following open source license terms:
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package com.armedia.acm.auth.ad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;

import javax.naming.NamingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveDirectoryUtils
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ActiveDirectoryUtils.class);

    private static final String USER_ACCOUNT_IS_DISABLED = "User account is disabled.";
    private static final String USER_ACCOUNT_HAS_EXPIRED = "User account and/or password has expired.";
    private static final String USER_ACCOUNT_IS_LOCKED = "User account is locked, please try again in some time.";
    private static final String USER_PASSWORD_HAS_EXPIRED = "User password has expired.";
    private static final String USER_MUST_RESET_PASSWORD = "User must reset password.";
    private static final String USER_NOT_PERMITTED_TO_LOGON_AT_THIS_TIME = "User not permitted to logon at this time.";
    private static final String BAD_CREDENTIALS = "Bad credentials";
    private static final Pattern SUB_ERROR_CODE = Pattern.compile(".*data\\s([0-9a-f]{3,4}).*");

    protected static MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public static void raiseExceptionForErrorCode(int code, NamingException exception)
    {
        String hexString = Integer.toHexString(code);
        Throwable cause = new AcmActiveDirectoryAuthenticationException(hexString, exception.getMessage(), exception);
        switch (ActiveDirectoryError.toActiveDirectoryError(code))
        {
        case PASSWORD_EXPIRED:
            throw new CredentialsExpiredException(USER_PASSWORD_HAS_EXPIRED, cause);
        case ACCOUNT_DISABLED:
            throw new DisabledException(USER_ACCOUNT_IS_DISABLED, cause);
        case ACCOUNT_EXPIRED:
            throw new AccountExpiredException(USER_ACCOUNT_HAS_EXPIRED, cause);
        case ACCOUNT_LOCKED:
            throw new LockedException(USER_ACCOUNT_IS_LOCKED, cause);
        case PASSWORD_NEEDS_RESET:
            throw new CredentialsExpiredException(USER_MUST_RESET_PASSWORD, cause);
        default:
            throw badCredentials(cause);
        }
    }

    public static String subCodeToLogMessage(int code)
    {
        switch (ActiveDirectoryError.toActiveDirectoryError(code))
        {

        case USERNAME_NOT_FOUND:
        case INVALID_PASSWORD:
            return BAD_CREDENTIALS;
        case INVALID_LOGON_HOURS:
            return USER_NOT_PERMITTED_TO_LOGON_AT_THIS_TIME;
        case PASSWORD_EXPIRED:
            return USER_PASSWORD_HAS_EXPIRED;
        case ACCOUNT_DISABLED:
            return USER_ACCOUNT_IS_DISABLED;
        case ACCOUNT_EXPIRED:
            return USER_ACCOUNT_HAS_EXPIRED;
        case PASSWORD_NEEDS_RESET:
            return USER_MUST_RESET_PASSWORD;
        case ACCOUNT_LOCKED:
            return USER_ACCOUNT_IS_LOCKED;
        default:
            return "Unknown (error code " + Integer.toHexString(code) + ")";
        }
    }

    public static void handleBindException(NamingException exception)
    {
        int subErrorCode = parseSubErrorCode(exception.getMessage());

        if (subErrorCode <= 0)
        {
            LOGGER.debug("Failed to locate AD-specific sub-error code in message");
            return;
        }

        LOGGER.info("Active Directory authentication failed: {}", subCodeToLogMessage(subErrorCode));

        raiseExceptionForErrorCode(subErrorCode, exception);
    }

    public static int parseSubErrorCode(String message)
    {
        Matcher m = SUB_ERROR_CODE.matcher(message);

        if (m.matches())
        {
            return Integer.parseInt(m.group(1), 16);
        }

        return -1;
    }

    public static BadCredentialsException badCredentials()
    {
        return new BadCredentialsException(BAD_CREDENTIALS);
    }

    public static BadCredentialsException badCredentials(Throwable cause)
    {
        return (BadCredentialsException) badCredentials().initCause(cause);
    }
}
