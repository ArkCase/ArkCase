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

import com.armedia.acm.services.users.model.AcmUserState;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

public class AcmUserContextMapper implements ContextMapper
{
    private static final int ACTIVE_DIRECTORY_DISABLED_BIT = 2;
    private Logger log = LogManager.getLogger(getClass());
    private AcmLdapSyncConfig acmLdapSyncConfig;

    public AcmUserContextMapper(AcmLdapSyncConfig acmLdapSyncConfig)
    {
        this.acmLdapSyncConfig = acmLdapSyncConfig;
    }

    @Override
    public LdapUser mapFromContext(Object ctx)
    {
        DirContextAdapter adapter = (DirContextAdapter) ctx;

        LdapUser user = mapToLdapUser(adapter);
        log.trace("Retrieved user [{}]", user.getDistinguishedName());
        return user;
    }

    protected LdapUser mapToLdapUser(DirContextAdapter adapter)
    {
        LdapUser user = new LdapUser();
        user.setDirectoryName(acmLdapSyncConfig.getDirectoryName());
        user.setLastName(MapperUtils.getAttribute(adapter, "sn"));
        user.setFirstName(MapperUtils.getAttribute(adapter, "givenName"));

        String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());
        user.setFullName(fullName);

        // because of how the LDAP query paging works, we can no longer return null for the disabled accounts.
        // so we return them, but mark them DISABLED. The DAO will filter them.
        String uac = MapperUtils.getAttribute(adapter, "userAccountControl");
        if (isUserDisabled(uac))
        {
            log.debug("User [{}] is disabled and won't be synced", fullName);
            user.setState(AcmUserState.DISABLED.name());
        }
        else
        {
            user.setState(AcmUserState.VALID.name());
        }

        user.setDistinguishedName(MapperUtils.appendToDn(adapter.getDn().toString(), acmLdapSyncConfig.getBaseDC()));
        String userId = MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getUserIdAttributeName());
        user.setUserId(MapperUtils.buildUserId(userId, acmLdapSyncConfig.getUserDomain()));
        user.setMail(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getMailAttributeName()));
        user.setCountry(MapperUtils.getAttribute(adapter, "co"));
        user.setCountryAbbreviation(MapperUtils.getAttribute(adapter, "c"));
        user.setCompany(MapperUtils.getAttribute(adapter, "company"));
        user.setDepartment(MapperUtils.getAttribute(adapter, "department"));
        user.setTitle(MapperUtils.getAttribute(adapter, "title"));
        user.setsAMAccountName(MapperUtils.getAttribute(adapter, "samAccountName"));
        user.setUserPrincipalName(MapperUtils.getAttribute(adapter, "userPrincipalName"));
        user.setUid(MapperUtils.getAttribute(adapter, "uid"));
        user.setSortableValue(MapperUtils.getAttribute(adapter, acmLdapSyncConfig.getAllUsersSortingAttribute()));
        user.setPasswordExpirationDate(Directory.valueOf(acmLdapSyncConfig.getDirectoryType()).getPasswordExpirationDate(adapter));
        return user;
    }

    protected boolean isUserDisabled(String uac)
    {
        try
        {
            long userAccountControl = Long.valueOf(uac);
            return (userAccountControl & ACTIVE_DIRECTORY_DISABLED_BIT) == ACTIVE_DIRECTORY_DISABLED_BIT;
        }
        catch (NumberFormatException nfe)
        {
            log.warn("user account control value [{}] is not a number!", uac);
            return false;
        }
    }
}
