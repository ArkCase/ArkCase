package com.armedia.acm.services.users.dao.ldap;

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

import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.RetryExecutor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NoPermissionException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;

class LdapCrudDao
{
    private SpringLdapDao ldapDao;

    private Logger log = LogManager.getLogger(getClass());

    public void create(DirContextAdapter context, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        execute(ldapSyncConfig, ldapTemplate -> {
            new RetryExecutor<Void>().retry(() -> ldapTemplate.bind(context));
            log.debug("Entry with context [{}] was successfully created in LDAP", context.getAttributes());
            return null;
        });
    }

    public DirContextOperations lookup(String dn, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        return executeWithResult(ldapSyncConfig, ldapTemplate -> {
            log.debug("Lookup for entry with dn: [{}]", dn);
            return new RetryExecutor<DirContextOperations>()
                    .retryResult(() -> ldapTemplate.lookupContext(MapperUtils.stripBaseFromDn(dn, ldapSyncConfig.getBaseDC())));
        });
    }

    public void update(DirContextOperations context, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        execute(ldapSyncConfig, ldapTemplate -> {
            new RetryExecutor<Void>().retry(() -> ldapTemplate.modifyAttributes(context));
            log.debug("Updated entry with dn: [{}]", context.getDn());
            return null;
        });
    }

    public void delete(String dn, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        execute(ldapSyncConfig, ldapTemplate -> {
            new RetryExecutor<Void>().retry(() -> ldapTemplate.unbind(MapperUtils.stripBaseFromDn(dn,
                    ldapSyncConfig.getBaseDC())));
            log.debug("Entry [{}] was successfully deleted", dn);
            return null;
        });

    }

    private void execute(AcmLdapSyncConfig ldapSyncConfig, LdapTemplateFunction<Void> function)
            throws AcmLdapActionFailedException
    {

        LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapSyncConfig);
        try
        {
            function.apply(ldapTemplate);
        }
        catch (NameAlreadyBoundException e)
        {
            log.warn("Entry already exists");
            throw e;
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP action failed to execute", e);
        }
    }

    private DirContextOperations executeWithResult(AcmLdapSyncConfig ldapSyncConfig,
            LdapTemplateFunction<DirContextOperations> function)
            throws AcmLdapActionFailedException
    {

        LdapTemplate ldapTemplate = ldapDao.buildLdapTemplate(ldapSyncConfig);
        try
        {
            return function.apply(ldapTemplate);
        }
        catch (NoPermissionException e)
        {
            throw new AcmLdapActionFailedException("Insufficient access rights for this LDAP action", e);
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP action failed to execute", e);
        }
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    @FunctionalInterface
    private interface LdapTemplateFunction<T>
    {
        T apply(LdapTemplate template) throws Exception;
    }
}
