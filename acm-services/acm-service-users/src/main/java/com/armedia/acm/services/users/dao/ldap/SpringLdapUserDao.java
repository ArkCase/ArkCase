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

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.model.ldap.PasswordLengthValidationRule;
import com.armedia.acm.services.users.service.RetryExecutor;
import com.armedia.acm.services.users.service.ldap.LdapEntryTransformer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.directory.*;

import java.util.List;

public class SpringLdapUserDao
{
    private LdapCrudDao ldapCrudDao;

    private LdapEntryTransformer ldapEntryTransformer;

    private PasswordLengthValidationRule passwordLengthValidationRule;

    private Logger log = LogManager.getLogger(getClass());

    public LdapUser findUser(String username, LdapTemplate template, AcmLdapSyncConfig config, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        AcmUserContextMapper userGroupsContextMapper = new AcmUserContextMapper(config);

        if (attributes != null)
        {
            String[] allAttributes = ArrayUtils.addAll(attributes, config.getUserIdAttributeName(), config.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        List<LdapUser> results = template.search(config.getUserSearchBase(), String.format(config.getUserSearchFilter(),
                username), searchControls, userGroupsContextMapper);

        if (CollectionUtils.isNotEmpty(results))
        {
            // Return the first entity that will be found. The above search can return multiple results under one domain
            // if
            // "sAMAccountName" is the same for two users. This in theory should not be the case, but just in case,
            // return only the first one.
            return results.get(0);
        }

        throw new UsernameNotFoundException("User with id [" + username + "] cannot be found");
    }

    public LdapUser findUserByLookup(String dn, LdapTemplate template, AcmLdapSyncConfig config)
    {
        return lookupUser(dn, template, config);
    }

    public LdapUser lookupUser(String dn, LdapTemplate template, AcmLdapSyncConfig config)
    {
        AcmUserContextMapper userGroupsContextMapper = new AcmUserContextMapper(config);

        String userDnStrippedBase = MapperUtils.stripBaseFromDn(dn, config.getBaseDC());

        String[] userSyncAttributes = config.getUserSyncAttributes();
        if (ArrayUtils.isNotEmpty(userSyncAttributes))
        {
            String[] allAttributes = ArrayUtils.addAll(userSyncAttributes, config.getUserIdAttributeName(), config.getMailAttributeName());
            return (LdapUser) template.lookup(userDnStrippedBase, allAttributes, userGroupsContextMapper);
        }
        else
        {
            return (LdapUser) template.lookup(userDnStrippedBase, userGroupsContextMapper);
        }
    }

    public void changeUserPassword(String dn, String password, String newPassword, LdapTemplate ldapTemplate, AcmLdapConfig config)
            throws AcmLdapActionFailedException, InvalidAttributeValueException {
        try
        {
            ContextSource contextSource = new RetryExecutor<ContextSource>().retryResult(ldapTemplate::getContextSource);
            DirContext context = contextSource.getContext(dn, password);

            // set old/new password attributes
            ModificationItem[] mods = new ModificationItem[2];
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                    Directory.valueOf(config.getDirectoryType()).getCurrentPasswordAttribute(password));
            mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                    Directory.valueOf(config.getDirectoryType()).getPasswordAttribute(newPassword));
            // Perform the update
            String strippedBaseDn = MapperUtils.stripBaseFromDn(dn, config.getBaseDC());
            new RetryExecutor().retryChecked(() -> context.modifyAttributes(strippedBaseDn, mods));
            context.close();
        }
        catch (AuthenticationException e)
        {
            throw e;
        }
        catch (AcmLdapActionFailedException e)
        {
            log.debug("AcmLdapActionFailedException occurs",e);
            throw e;
        }
        catch (InvalidAttributeValueException e)
        {
            log.debug("The password is too young to change ",e);
            throw e;
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP action failed to execute", e);
        }
    }

    public void changeUserPasswordWithAdministrator(String dn, String password, LdapTemplate ldapTemplate, AcmLdapConfig config)
            throws AcmLdapActionFailedException
    {
        String strippedBaseDn = MapperUtils.stripBaseFromDn(dn, config.getBaseDC());

        try
        {
            BasicAttribute passwordAttribute = Directory.valueOf(config.getDirectoryType())
                    .getPasswordAttribute(password);
            // set new password attributes
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, passwordAttribute);
            // Perform the update
            new RetryExecutor().retryChecked(() -> ldapTemplate.modifyAttributes(strippedBaseDn, mods));
        }
        catch (Exception e)
        {
            log.warn("Changing the password for User: [{}] failed. ", dn, e);
            throw new AcmLdapActionFailedException("LDAP action failed to execute", e);
        }
    }

    public void createUserEntry(AcmUser acmUser, String password, AcmLdapSyncConfig ldapSyncConfig)
            throws AcmLdapActionFailedException
    {
        if (password == null)
        {
            password = MapperUtils.generatePassword(passwordLengthValidationRule.getMinLength());
        }

        DirContextAdapter context;
        try
        {
            context = ldapEntryTransformer.createContextForNewUserEntry(ldapSyncConfig.getDirectoryName(),
                    acmUser, password, ldapSyncConfig.getBaseDC());
        }
        catch (Exception e)
        {
            throw new AcmLdapActionFailedException("LDAP action failed to execute", e);
        }
        ldapCrudDao.create(context, ldapSyncConfig);
    }

    public void updateUserEntry(AcmUser acmUser, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        DirContextOperations context = ldapCrudDao.lookup(acmUser.getDistinguishedName(), ldapSyncConfig);
        DirContextOperations editContext = ldapEntryTransformer.createContextForEditUserEntry(context, acmUser,
                ldapSyncConfig.getDirectoryName());
        ldapCrudDao.update(editContext, ldapSyncConfig);
    }

    public void deleteUserEntry(String dn, AcmLdapSyncConfig ldapSyncConfig) throws AcmLdapActionFailedException
    {
        ldapCrudDao.delete(dn, ldapSyncConfig);
    }

    public void setLdapCrudDao(LdapCrudDao ldapCrudDao)
    {
        this.ldapCrudDao = ldapCrudDao;
    }

    public void setLdapEntryTransformer(LdapEntryTransformer ldapEntryTransformer)
    {
        this.ldapEntryTransformer = ldapEntryTransformer;
    }

    public void setPasswordLengthValidationRule(PasswordLengthValidationRule passwordLengthValidationRule)
    {
        this.passwordLengthValidationRule = passwordLengthValidationRule;
    }
}
