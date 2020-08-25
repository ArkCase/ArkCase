package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.ldap.LdapAuthenticateService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.LdapTemplate;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARLdapAuthenticationService
{
    private static SARLdapAuthenticationService SARLdapAuthenticationService;

    private LdapAuthenticateService ldapAuthenticateService;

    private Logger log = LogManager.getLogger(getClass());

    private SARLdapAuthenticationService(LdapAuthenticateService ldapAuthenticateService)
    {
        this.ldapAuthenticateService = ldapAuthenticateService;
    }

    public static SARLdapAuthenticationService getInstance(LdapAuthenticateService ldapAuthenticateService)
    {
        if (SARLdapAuthenticationService == null)
        {
            synchronized (SARLdapAuthenticationService.class)
            {
                if (SARLdapAuthenticationService == null)
                {
                    SARLdapAuthenticationService = new SARLdapAuthenticationService(ldapAuthenticateService);
                }
            }
        }
        return SARLdapAuthenticationService;
    }

    /*
     * Authenticates user against LDAP
     */
    public Boolean authenticate(String userName, String password)
    {
        LdapTemplate template = ldapAuthenticateService.getLdapDao().buildLdapTemplate(ldapAuthenticateService.getLdapAuthenticateConfig());

        String userIdAttributeName = ldapAuthenticateService.getLdapAuthenticateConfig().getUserIdAttributeName();
        String searchBase = ldapAuthenticateService.getLdapAuthenticateConfig().getSearchBase();

        String filter = "(" + userIdAttributeName + "=" + userName + ")";
        boolean authenticated = template.authenticate(searchBase, filter, password);

        log.debug("searchBase[{}], filter[{}], authenticated[{}]", searchBase, filter, authenticated);

        return authenticated;
    }

    public void resetPortalUserPassword(String userName, String password) throws AcmUserActionFailedException
    {
        // TODO change the search for user, external portal has a different reset mechanism.
        AcmUser acmUser = ldapAuthenticateService.getUserDao().findByUserId(userName);
        if (acmUser == null)
        {
            throw new AcmUserActionFailedException("reset password", "USER", null, "User not found!", null);
        }
        LdapTemplate ldapTemplate = ldapAuthenticateService.getLdapDao()
                .buildLdapTemplate(ldapAuthenticateService.getLdapAuthenticateConfig());
        try
        {
            log.debug("Changing password for user: [{}]", acmUser.getUserId());

            ldapAuthenticateService.getLdapUserDao().changeUserPasswordWithAdministrator(acmUser.getDistinguishedName(), password,
                    ldapTemplate, ldapAuthenticateService.getLdapAuthenticateConfig());
        }
        catch (AcmLdapActionFailedException e)
        {
            log.debug(e.getMessage(), e);
            throw new AcmUserActionFailedException("reset password", "USER", null, "Change password action failed!", e);
        }
        try
        {
            ldapAuthenticateService.savePasswordExpirationDate(acmUser, ldapTemplate);
            ldapAuthenticateService.invalidateToken(acmUser);
        }
        catch (AuthenticationException e)
        {
            log.warn("Password expiration date was not set for user [{}]", acmUser.getUserId(), e);
        }
    }

    public LdapAuthenticateService getLdapAuthenticateService()
    {
        return ldapAuthenticateService;
    }
}
