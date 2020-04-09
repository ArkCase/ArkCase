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

public class AcmLdapConfig
{
    private String[] ldapUrl;
    private String baseDC;
    private String authUserDn;
    private String authUserPassword;
    private String userIdAttributeName;
    private String mailAttributeName;
    private boolean ignorePartialResultException;
    private String referral;
    private String directoryName;
    private String directoryType;
    private boolean autoGenerateUserId;

    public String getAuthUserPassword()
    {
        return authUserPassword;
    }

    public void setAuthUserPassword(String authUserPassword)
    {
        this.authUserPassword = authUserPassword;
    }

    public String[] getLdapUrl()
    {
        return ldapUrl;
    }

    public void setLdapUrl(String[] ldapUrl)
    {
        this.ldapUrl = ldapUrl;
    }

    public String getAuthUserDn()
    {
        return authUserDn;
    }

    public void setAuthUserDn(String authUserDn)
    {
        this.authUserDn = authUserDn;
    }

    public String getUserIdAttributeName()
    {
        return userIdAttributeName;
    }

    public void setUserIdAttributeName(String userIdAttributeName)
    {
        this.userIdAttributeName = userIdAttributeName;
    }

    public boolean isIgnorePartialResultException()
    {
        return ignorePartialResultException;
    }

    public void setIgnorePartialResultException(boolean ignorePartialResultException)
    {
        this.ignorePartialResultException = ignorePartialResultException;
    }

    public String getReferral()
    {
        return referral;
    }

    public void setReferral(String referral)
    {
        this.referral = referral;
    }

    public String getMailAttributeName()
    {
        return mailAttributeName;
    }

    public void setMailAttributeName(String mailAttributeName)
    {
        this.mailAttributeName = mailAttributeName;
    }

    public String getBaseDC()
    {
        return baseDC;
    }

    public void setBaseDC(String baseDC)
    {
        this.baseDC = baseDC;
    }

    public String getDirectoryName()
    {
        return directoryName;
    }

    public void setDirectoryName(String directoryName)
    {
        this.directoryName = directoryName;
    }

    public String getDirectoryType()
    {
        return directoryType;
    }

    public void setDirectoryType(String directoryType)
    {
        this.directoryType = directoryType;
    }

    public boolean isAutoGenerateUserId()
    {
        return autoGenerateUserId;
    }

    public void setAutoGenerateUserId(boolean autoGenerateUserId)
    {
        this.autoGenerateUserId = autoGenerateUserId;
    }
}
