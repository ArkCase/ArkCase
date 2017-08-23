package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * Sync the user-related database tables with an LDAP directory. To support multiple LDAP configurations, create multiple Spring beans, each
 * with its own ldapSyncConfig.
 * <p/>
 * Both application roles and LDAP groups are synced.
 * <ul>
 * <li>Application roles drive role-based access control, and every deployment has the same application role names regardless of the LDAP
 * group names. The ldapSyncConfig includes a mapping from the logical role name to the physical LDAP group name. For each entry in this
 * mapping, the members of the indicated LDAP group are linked to the indicated logical application role.</li>
 * <li>LDAP groups are also synced, to be available for data access control; this allows users to grant or deny access to specific groups.
 * The groups could be more granular than application roles; for example, all case agents share the same application roles, but different
 * LDAP groups could represent different functional or geographic areas. So granting access at the LDAP group level could be more
 * appropriate - i.e., would restrict access to only those case agents in the appropriate functional or geographic area.</li>
 * </ul>
 */
public class LdapSyncService
{
    private SpringLdapDao ldapDao;
    private AcmLdapSyncConfig ldapSyncConfig;
    private boolean syncEnabled = true;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SpringLdapUserDao springLdapUserDao;
    private PropertyFileManager propertyFileManager;
    private String ldapLastSyncPropertyFileLocation;
    private LdapSyncProcessor ldapSyncProcessor;

    private final Logger log = LoggerFactory.getLogger(getClass());

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration folder ($HOME/.acm).
    @Transactional
    public void ldapSync()
    {
        if (!isSyncEnabled())
        {
            log.debug("Sync is disabled - stopping now.");
            return;
        }

        log.info("Starting sync of directory: [{}]; ldap URL: [{}]", getLdapSyncConfig().getDirectoryName(),
                getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        // all the ldap work first, then all the database work; because the ldap queries could be very time consuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out. So we run all the LDAP queries first, then do all the database operations all at once.
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());
        List<LdapUser> ldapUsers = getLdapDao().findUsersPaged(template, getLdapSyncConfig(), null);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), null);

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, ldapSyncConfig);
    }

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration folder ($HOME/.acm).
    public void ldapPartialSync()
    {
        if (!isSyncEnabled())
        {
            log.debug("Partial sync is disabled - stopping now.");
            return;
        }

        log.info("Starting partial sync of directory: [{}]; ldap URL: [{}]", getLdapSyncConfig().getDirectoryName(),
                getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        String ldapLastSyncDate = readLastLdapSyncDate(getLdapSyncConfig().getDirectoryName());

        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        // only changed users and groups are retrieved
        List<LdapUser> ldapUsers = getLdapDao().findUsersPaged(template, getLdapSyncConfig(), ldapLastSyncDate);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), ldapLastSyncDate);

        getLdapSyncProcessor().sync(ldapUsers, ldapGroups, getLdapSyncConfig());

        writeLastLdapSync(getLdapSyncConfig().getDirectoryName());
    }

    /**
     * Try to sync user from LDAP by given username
     *
     * @param username - username of the user
     */
    public LdapUser ldapUserSync(String username)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user [{}] from ldap [{}]", username, getLdapSyncConfig().getLdapUrl());

        LdapUser user = getSpringLdapUserDao().findUser(username, template, getLdapSyncConfig(),
                getLdapSyncConfig().getUserSyncAttributes());
        List<LdapUser> ldapUsers = Arrays.asList(user);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), null);

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, getLdapSyncConfig());
        return user;
    }

    /**
     * Try to sync user from LDAP by given dn
     *
     * @param dn - distinguished name of the user
     */
    public LdapUser syncUserByDn(String dn)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user with DN: [{}] from ldap [{}]", dn, getLdapSyncConfig().getLdapUrl());

        LdapUser user = getSpringLdapUserDao().findUserByLookup(dn, template, getLdapSyncConfig());
        List<LdapUser> ldapUsers = Arrays.asList(user);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), null);

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, getLdapSyncConfig());
        return user;
    }

    public String readLastLdapSyncDate(String directoryName)
    {
        String lastSyncDate = null;
        try
        {
            lastSyncDate = propertyFileManager.load(ldapLastSyncPropertyFileLocation,
                    String.format("%s.%s", directoryName, AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY), null);
        } catch (AcmEncryptionException e)
        {
            log.warn("Failed to read [{}] date property. All users will be synced ", AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY,
                    e.getMessage());
        }
        return lastSyncDate;
    }

    public void writeLastLdapSync(String directoryName)
    {
        propertyFileManager.store(String.format("%s.%s", directoryName, AcmLdapConstants.LDAP_LAST_SYNC_PROPERTY_KEY),
                ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
                ldapLastSyncPropertyFileLocation, false);
    }

    public SpringLdapDao getLdapDao()
    {
        return ldapDao;
    }

    public void setLdapDao(SpringLdapDao ldapDao)
    {
        this.ldapDao = ldapDao;
    }

    public AcmLdapSyncConfig getLdapSyncConfig()
    {
        return ldapSyncConfig;
    }

    public void setLdapSyncConfig(AcmLdapSyncConfig ldapSyncConfig)
    {
        this.ldapSyncConfig = ldapSyncConfig;
    }

    public boolean isSyncEnabled()
    {
        return syncEnabled;
    }

    public void setSyncEnabled(boolean syncEnabled)
    {
        this.syncEnabled = syncEnabled;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public SpringLdapUserDao getSpringLdapUserDao()
    {
        return springLdapUserDao;
    }

    public void setSpringLdapUserDao(SpringLdapUserDao springLdapUserDao)
    {
        this.springLdapUserDao = springLdapUserDao;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getLdapLastSyncPropertyFileLocation()
    {
        return ldapLastSyncPropertyFileLocation;
    }

    public void setLdapLastSyncPropertyFileLocation(String ldapLastSyncPropertyFileLocation)
    {
        this.ldapLastSyncPropertyFileLocation = ldapLastSyncPropertyFileLocation;
    }

    public LdapSyncProcessor getLdapSyncProcessor()
    {
        return ldapSyncProcessor;
    }

    public void setLdapSyncProcessor(LdapSyncProcessor ldapSyncProcessor)
    {
        this.ldapSyncProcessor = ldapSyncProcessor;
    }
}
