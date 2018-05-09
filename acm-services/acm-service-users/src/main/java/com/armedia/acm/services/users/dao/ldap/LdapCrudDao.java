package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.MapperUtils;
import com.armedia.acm.services.users.service.RetryExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NoPermissionException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;

class LdapCrudDao
{
    private SpringLdapDao ldapDao;

    private Logger log = LoggerFactory.getLogger(getClass());

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
