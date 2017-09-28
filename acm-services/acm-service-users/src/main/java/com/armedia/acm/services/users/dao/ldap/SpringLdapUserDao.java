package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserContextMapper;
import com.armedia.acm.services.users.model.ldap.Directory;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.RetryExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import java.util.List;

public class SpringLdapUserDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

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
            // Return the first entity that will be found. The above search can return multiple results under one domain if
            // "sAMAccountName" is the same for two users. This in theory should not be the case, but just in case, return only the first one.
            LdapUser ldapUser = results.get(0);
            ldapUser = appendDomainNameIfSet(ldapUser, config);
            return ldapUser;
        }

        throw new UsernameNotFoundException("User with id [" + username + "] cannot be found");
    }

    private LdapUser appendDomainNameIfSet(LdapUser ldapUser, AcmLdapSyncConfig config)
    {
        // append user domain name if set. Used in Single Sign-On scenario.
        String userDomainSuffix = (StringUtils.isBlank(config.getUserDomain()) ? "" : "@" + config.getUserDomain());
        log.debug("Adding user domain suffix to the username: [{}]", userDomainSuffix);
        ldapUser.setUserId(ldapUser.getUserId() + userDomainSuffix);
        return ldapUser;
    }

    public LdapUser findUserByLookup(String dn, LdapTemplate template, AcmLdapSyncConfig config)
    {
        LdapUser user = lookupUser(dn, template, config);
        user = appendDomainNameIfSet(user, config);
        return user;
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
        } else
        {
            return (LdapUser) template.lookup(userDnStrippedBase, userGroupsContextMapper);
        }
    }

    public void changeUserPassword(String dn, String password, String newPassword, LdapTemplate ldapTemplate, AcmLdapConfig config)
            throws AcmLdapActionFailedException
    {
        String strippedBaseDn = MapperUtils.stripBaseFromDn(dn, config.getBaseDC());

        try
        {
            ContextSource contextSource = new RetryExecutor<ContextSource>().retryResult(ldapTemplate::getContextSource);
            DirContext context = contextSource.getContext(dn, password);

            // set old/new password attributes
            ModificationItem[] mods = new ModificationItem[2];
            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                    Directory.valueOf(config.getDirectoryType()).getPasswordAttribute(password));
            mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                    Directory.valueOf(config.getDirectoryType()).getPasswordAttribute(newPassword));
            // Perform the update
            new RetryExecutor().retryChecked(() -> context.modifyAttributes(strippedBaseDn, mods));
            context.close();
        } catch (AuthenticationException e)
        {
            log.warn("User: [{}] failed to authenticate. ", dn);
            throw e;
        } catch (Exception e)
        {
            log.warn("Changing the password for User: [{}] failed. ", dn, e);
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
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
        } catch (Exception e)
        {
            log.warn("Changing the password for User: [{}] failed. ", dn, e);
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
    }
}
