package com.armedia.acm.services.users.dao.ldap;


import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapConfig;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.RetryExecutor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.directory.SearchControls;
import java.util.List;

public class SpringLdapUserDao
{
    private Logger log = LoggerFactory.getLogger(getClass());

    public AcmUser findUser(String username, LdapTemplate template, AcmLdapSyncConfig config, String[] attributes)
    {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        AcmUserGroupsContextMapper userGroupsContextMapper = new AcmUserGroupsContextMapper(config);

        if (attributes != null)
        {
            String[] allAttributes = ArrayUtils.addAll(attributes,
                    config.getUserIdAttributeName(), config.getMailAttributeName());
            searchControls.setReturningAttributes(allAttributes);
        }

        List<AcmUser> results = template.search(config.getUserSearchBase(),
                String.format(config.getUserSearchFilter(), username), searchControls, userGroupsContextMapper);

        if (CollectionUtils.isNotEmpty(results))
        {
            // Return the first entity that will be found. The above search can return multiple results under one domain if
            // "sAMAccountName" is the same for two users. This in theory should not be the case, but just in case, return only the first one.
            AcmUser acmUser = results.get(0);

            // append user domain name if set. Used in Single Sign-On scenario.
            String userDomainSuffix = (StringUtils.isBlank(config.getUserDomain()) ? "" : "@" + config.getUserDomain());
            log.debug("Adding user domain suffix to the username: {}", userDomainSuffix);
            acmUser.setUserId(acmUser.getUserId() + userDomainSuffix);
            return acmUser;
        }

        throw new UsernameNotFoundException("User with id [" + username + "] cannot be found");
    }

    public void changeUserPassword(String dn, String password, LdapTemplate ldapTemplate, AcmLdapConfig config)
            throws AcmLdapActionFailedException
    {
        String strippedBaseDn = MapperUtils.stripBaseFromDn(dn, config.getBaseDC());

        try
        {
            DirContextOperations context = new RetryExecutor<DirContextOperations>()
                    .retryResult(() -> ldapTemplate.lookupContext(strippedBaseDn));
            context.setAttributeValue("userPassword", password.getBytes());
            new RetryExecutor().retry(() -> ldapTemplate.modifyAttributes(context));
        } catch (Exception e)
        {
            log.warn("Changing User's [{}] password failed. ", dn, e);
            throw new AcmLdapActionFailedException("LDAP Action Failed Exception", e);
        }
    }
}
