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
package com.armedia.acm.services.users.service.ldap;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryAuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

/**
 * <p>
 * Thrown as a translation of an {@link javax.naming.AuthenticationException} when attempting to authenticate against
 * Active Directory using {@link ActiveDirectoryLdapAuthenticationProvider}. Typically this error is wrapped by an
 * {@link AuthenticationException} since it does not provide a user friendly message. When wrapped, the original
 * Exception can be caught and {@link ActiveDirectoryAuthenticationException} can be accessed using
 * {@link AuthenticationException#getCause()} for custom error handling.
 * </p>
 * <p>
 * The {@link #getDataCode()} will return the error code associated with the data portion of the error message. For
 * example, the following error message would return 773 for {@link #getDataCode()}.
 * </p>
 *
 * <pre>
 * javax.naming.AuthenticationException: [LDAP: error code 49 - 80090308: LdapErr: DSID-0C090334, comment: AcceptSecurityContext error, data 775, vece ]
 * </pre>
 *
 * @author Rob Winch
 */
@SuppressWarnings("serial")
public class AcmActiveDirectoryAuthenticationException extends AuthenticationException
{
    private final String dataCode;

    public AcmActiveDirectoryAuthenticationException(String dataCode, String message, Throwable cause)
    {
        super(message, cause);
        this.dataCode = dataCode;
    }

    public AcmActiveDirectoryAuthenticationException(String message, Throwable cause)
    {
        super(message, cause);
        this.dataCode = null;
    }

    public String getDataCode()
    {
        return dataCode;
    }
}
