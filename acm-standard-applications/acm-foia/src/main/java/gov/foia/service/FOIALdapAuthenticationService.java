package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapAuthenticateConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 1, 2018
 *
 */
public class FOIALdapAuthenticationService extends LdapAuthenticateService
{

    private Logger log = LogManager.getLogger(getClass());
    /*
     * Authenticates user against LDAP
     */
    public Boolean authenticate(String userName, String password)
    {
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());

        String userIdAttributeName = getLdapAuthenticateConfig().getUserIdAttributeName();
        String searchBase = getLdapAuthenticateConfig().getSearchBase();

        String filter = "(" + userIdAttributeName + "=" + userName + ")";
        boolean authenticated = template.authenticate(searchBase, filter, password);

        log.debug("searchBase[{}], filter[{}], authenticated[{}]", searchBase, filter, authenticated);

        return authenticated;
    }

    public void resetPortalUserPassword(String userName, String password) throws AcmUserActionFailedException
    {
        // TODO change the search for user, external portal has a different reset mechanism.
        AcmUser acmUser = getUserDao().findByUserId(userName);
        if (acmUser == null)
        {
            throw new AcmUserActionFailedException("reset password", "USER", null, "User not found!", null);
        }
        LdapTemplate ldapTemplate = getLdapDao().buildLdapTemplate(getLdapAuthenticateConfig());
        try
        {
            log.debug("Changing password for user: [{}]", acmUser.getUserId());

            getLdapUserDao().changeUserPasswordWithAdministrator(acmUser.getDistinguishedName(), password, ldapTemplate, getLdapAuthenticateConfig());
        }
        catch (AcmLdapActionFailedException e)
        {
            log.debug(e.getMessage(), e);
            throw new AcmUserActionFailedException("reset password", "USER", null, "Change password action failed!", e);
        }
        try
        {
            savePasswordExpirationDate(acmUser, ldapTemplate);
            invalidateToken(acmUser);
        }
        catch (AuthenticationException e)
        {
            log.warn("Password expiration date was not set for user [{}]", acmUser.getUserId(), e);
        }
    }
}
