package com.armedia.acm.services.users.service.ldap;

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

import com.armedia.acm.data.AcmServiceLdapSyncEvent;
import com.armedia.acm.data.AcmServiceLdapSyncResult;
import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.quartz.scheduler.AcmSchedulerService;
import com.armedia.acm.services.users.dao.ldap.SpringLdapDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.scheduling.annotation.Async;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Sync the user-related database tables with an LDAP directory. To support multiple LDAP configurations, create
 * multiple Spring beans, each
 * with its own ldapSyncConfig.
 * <p/>
 * Both application roles and LDAP groups are synced.
 * <ul>
 * <li>Application roles drive role-based access control, and every deployment has the same application role names
 * regardless of the LDAP
 * group names. The ldapSyncConfig includes a mapping from the logical role name to the physical LDAP group name. For
 * each entry in this
 * mapping, the members of the indicated LDAP group are linked to the indicated logical application role.</li>
 * <li>LDAP groups are also synced, to be available for data access control; this allows users to grant or deny access
 * to specific groups.
 * The groups could be more granular than application roles; for example, all case agents share the same application
 * roles, but different
 * LDAP groups could represent different functional or geographic areas. So granting access at the LDAP group level
 * could be more
 * appropriate - i.e., would restrict access to only those case agents in the appropriate functional or geographic
 * area.</li>
 * </ul>
 */
public class LdapSyncService implements ApplicationEventPublisherAware
{
    private final Logger log = LogManager.getLogger(getClass());
    private SpringLdapDao ldapDao;
    private AcmLdapSyncConfig ldapSyncConfig;
    private boolean syncEnabled = true;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SpringLdapUserDao springLdapUserDao;
    private LdapSyncProcessor ldapSyncProcessor;
    private ApplicationEventPublisher applicationEventPublisher;
    private AcmSchedulerService schedulerService;

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration folder
    // ($HOME/.acm).
    public void ldapSync()
    {
        if (!isSyncEnabled())
        {
            log.debug("Sync is disabled - stopping now.");
            return;
        }

        log.info("Starting full sync of directory: [{}]; ldap URL: [{}]", getLdapSyncConfig().getDirectoryName(),
                getLdapSyncConfig().getLdapUrl());

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        // all the ldap work first, then all the database work; because the ldap queries could be very time consuming.
        // If we opened up a database transaction, then spend a minute or so querying LDAP, the database transaction
        // could time out. So we run all the LDAP queries first, then do all the database operations all at once.
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());
        List<LdapUser> ldapUsers = getLdapDao().findUsersPaged(template, getLdapSyncConfig(), Optional.empty());
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), Optional.empty());

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, ldapSyncConfig, true);
    }

    // this method is used by scheduled jobs in Spring beans loaded dynamically from the ACM configuration folder
    // ($HOME/.acm).
    public void ldapPartialSync(Date lastRun)
    {
        if (!isSyncEnabled())
        {
            log.debug("Partial sync is disabled - stopping now.");
            return;
        }

        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());

        Optional<String> lastRunDate = lastRun == null ? Optional.empty()
                : Optional.of(DateTimeFormatter.ISO_INSTANT.format(lastRun.toInstant()));

        log.info("Starting {} sync of directory: [{}]; ldap URL: [{}]", lastRunDate.map(it -> "partial").orElse("full"),
                getLdapSyncConfig().getDirectoryName(),
                getLdapSyncConfig().getLdapUrl());

        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        // only changed users are retrieved
        List<LdapUser> ldapUsers = getLdapDao().findUsersPaged(template, getLdapSyncConfig(), lastRunDate);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), Optional.empty());

        getLdapSyncProcessor().sync(ldapUsers, ldapGroups, getLdapSyncConfig(), !lastRunDate.isPresent());
    }

    @Async
    public void initiateSync(String principal, boolean fullSync)
    {
        String syncType = fullSync ? "Full" : "Partial";

        if (!isSyncEnabled())
        {
            log.debug("{} sync is disabled - stopping now.", syncType);
            AcmServiceLdapSyncResult ldapSyncResult = new AcmServiceLdapSyncResult();
            ldapSyncResult.setMessage(String.format("Ldap %s sync is not enabled", syncType));
            ldapSyncResult.setResult(false);
            ldapSyncResult.setService("LDAP");
            ldapSyncResult.setUser(principal);
            applicationEventPublisher.publishEvent(new AcmServiceLdapSyncEvent(ldapSyncResult));
            return;
        }

        boolean successResult = true;
        try
        {
            if (fullSync)
            {
                ldapSync();
            }
            else
            {
                ldapPartialSync(schedulerService.getTriggerPreviousFireTime(String.format("%s_ldapPartialSyncJobTrigger",
                        getLdapSyncConfig().getDirectoryName())));
            }

        }
        catch (Exception e)
        {
            successResult = false;
            log.error("LDAP {} sync failed to complete.", syncType, e);
        }
        AcmServiceLdapSyncResult ldapSyncResult = new AcmServiceLdapSyncResult();
        ldapSyncResult.setMessage(successResult ? String.format("LDAP %s sync completed", syncType)
                : String.format("LDAP %s sync failed to complete", syncType));
        ldapSyncResult.setResult(successResult);
        ldapSyncResult.setService("LDAP");
        ldapSyncResult.setUser(principal);
        applicationEventPublisher.publishEvent(new AcmServiceLdapSyncEvent(ldapSyncResult));
    }

    /**
     * Try to sync user from LDAP by given username
     *
     * @param username
     *            - username of the user
     */
    public LdapUser ldapUserSync(String username)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user [{}] from ldap [{}]", username, getLdapSyncConfig().getLdapUrl());

        LdapUser user = getSpringLdapUserDao().findUser(username, template, getLdapSyncConfig(),
                getLdapSyncConfig().getUserSyncAttributes());
        List<LdapUser> ldapUsers = Arrays.asList(user);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), Optional.ofNullable(null));

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, getLdapSyncConfig(), false);
        return user;
    }

    /**
     * Try to sync user from LDAP by given dn
     *
     * @param dn
     *            - distinguished name of the user
     */
    public LdapUser syncUserByDn(String dn)
    {
        getAuditPropertyEntityAdapter().setUserId(getLdapSyncConfig().getAuditUserId());
        LdapTemplate template = getLdapDao().buildLdapTemplate(getLdapSyncConfig());

        log.info("Starting sync user with DN: [{}] from ldap [{}]", dn, getLdapSyncConfig().getLdapUrl());

        LdapUser user = getSpringLdapUserDao().findUserByLookup(dn, template, getLdapSyncConfig());
        List<LdapUser> ldapUsers = Arrays.asList(user);
        List<LdapGroup> ldapGroups = getLdapDao().findGroupsPaged(template, getLdapSyncConfig(), Optional.empty());

        ldapSyncProcessor.sync(ldapUsers, ldapGroups, getLdapSyncConfig(), false);
        return user;
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

    public LdapSyncProcessor getLdapSyncProcessor()
    {
        return ldapSyncProcessor;
    }

    public void setLdapSyncProcessor(LdapSyncProcessor ldapSyncProcessor)
    {
        this.ldapSyncProcessor = ldapSyncProcessor;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public AcmSchedulerService getSchedulerService()
    {
        return schedulerService;
    }

    public void setSchedulerService(AcmSchedulerService schedulerService)
    {
        this.schedulerService = schedulerService;
    }
}
