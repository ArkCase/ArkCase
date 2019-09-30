package com.armedia.acm.auth;

/*-
 * #%L
 * ACM Service: User Login and Authentication
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

import com.armedia.acm.core.model.ApplicationConfig;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

/**
 * @author ivana.shekerova on 6/17/2019.
 */
public class ExternalAuthenticationUtils
{

    private ApplicationConfig applicationConfig;
    private UserDao userDao;

    /*
     * alfrescoUserIdLdapAttribute controls the user id that is sent to Alfresco in the
     * X-Alfresco-Remote-User header, so that Alfresco knows who the real user is. In
     * Kerberos and CAC (smart card) environments, sometimes the ArkCase user id is not the
     * same as the Alfresco user id... The ArkCase user id could be "david.miller@armedia.com"
     * and the Alfresco user id would be some number from the smart card, e.g. "9283923892".
     * So with this attribute we can control what is sent to Alfresco.
     * Valid values: uid, samAccountName, userPrincipalName, distinguishedName
     */
    public String getEcmServiceUserId(AcmUser acmUser)
    {
        switch (getApplicationConfig().getAlfrescoUserIdLdapAttribute().toLowerCase())
        {
        case "samaccountname":
            return acmUser.getsAMAccountName();
        case "userprincipalname":
            return acmUser.getUserPrincipalName();
        case "uid":
            return acmUser.getUid();
        case "dn":
        case "distinguishedname":
            return acmUser.getDistinguishedName();
        default:
            return acmUser.getsAMAccountName();
        }
    }

    public AcmUser getArkcaseUserByLdapAttributeUserIdValue(String userIdLdapValue)
    {
        switch (getApplicationConfig().getAlfrescoUserIdLdapAttribute().toLowerCase())
        {
        case "samaccountname":
            return getUserDao().findBysAMAccountName(userIdLdapValue);
        case "userprincipalname":
            return getUserDao().findByUserPrincipalName(userIdLdapValue);
        case "uid":
            return getUserDao().findByUid(userIdLdapValue);
        case "dn":
        case "distinguishedname":
            return getUserDao().findByDistinguishedName(userIdLdapValue);
        default:
            return getUserDao().findBysAMAccountName(userIdLdapValue);
        }
    }

    public AcmUser getUserByUserId(String userId)
    {
        return getUserDao().findByUserId(userId);
    }

    public String getEcmServiceUserIdByParticipantLdapId(String participantLdapId)
    {
        AcmUser acmUser = getUserByUserId(participantLdapId);
        if (acmUser == null)
        {
            return null;
        }
        return getEcmServiceUserId(acmUser);
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return applicationConfig;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig)
    {
        this.applicationConfig = applicationConfig;
    }
}
