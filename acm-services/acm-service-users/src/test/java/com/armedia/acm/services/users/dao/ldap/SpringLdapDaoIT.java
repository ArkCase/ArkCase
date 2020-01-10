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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Created by dmiller on 6/28/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-user-service.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-config-user-service-test-dummy-beans.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-user-service-test-user-home-files.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-configuration.xml" })

public class SpringLdapDaoIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    static final Logger log = LogManager.getLogger(SpringLdapDaoIT.class);

    static final int RUNS = 10;

    @Autowired
    private CustomPagedLdapDao springLdapDao;

    @Autowired
    private AcmLdapSyncConfig acmSyncLdapConfig;

    @Autowired
    private SpringLdapUserDao springLdapUserDao;

    @Test
    public void findUsersWithAllAttributes()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, Optional.ofNullable(null));
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapUser -> {
            log.debug("AcmUser: {} : {}", ldapUser.getUserId(), ldapUser.getDistinguishedName());
        });
    }

    @Test
    public void findLdapGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapGroup> result = springLdapDao.findGroupsPaged(ldapTemplate, acmSyncLdapConfig, Optional.ofNullable(null));
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapGroup -> log.trace("Ldap Group: {}", ldapGroup.getName()));
    }

    @Test
    public void findUsersWithAllAttributesPerformance()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long sum = 0;
        for (int i = 0; i < RUNS; ++i)
        {
            long start = System.currentTimeMillis();
            List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, Optional.ofNullable(null));
            long time = System.currentTimeMillis() - start;
            sum += time;
            log.debug("Result: {}", result.size());
            log.debug("Time: {}ms", time);
        }
        log.debug("Avg Time: {}ms", sum * 1.0 / RUNS);
    }

    @Test
    public void findUsersWithSpecificAttributes()
    {
        String[] attributes = new String[] {
                "cn", "sn", "givenName", "dn", "distinguishedname", "sAMAccountName", "mail"
        };
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long sum = 0;
        for (int i = 0; i < RUNS; ++i)
        {
            long start = System.currentTimeMillis();
            List<LdapUser> result = springLdapDao.findUsers(ldapTemplate, acmSyncLdapConfig, attributes, Optional.ofNullable(null));
            long time = System.currentTimeMillis() - start;
            sum += time;
            log.debug("Result: {}", result.size());
            log.debug("Time: {}ms", time);
        }

        log.debug("Avg Time: {}ms", sum * 1.0 / RUNS);
    }

    @Test
    public void findUser()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, Optional.ofNullable(null));
        assertNotNull(result);
        assertTrue(!result.isEmpty());

        String userName = result.get(0).getUid();

        long start = System.currentTimeMillis();
        LdapUser ldapUser = springLdapUserDao.findUser(userName, ldapTemplate, acmSyncLdapConfig,
                acmSyncLdapConfig.getUserSyncAttributes());
        long time = System.currentTimeMillis() - start;
        log.debug("Time: [{}ms]", time);
        log.debug("User found: [{}]", ldapUser.getDistinguishedName());
    }

    @Test
    public void findUserByLookup()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, Optional.ofNullable(null));
        assertNotNull(result);
        assertTrue(!result.isEmpty());

        String userName = result.get(0).getUid();

        LdapUser testUser = springLdapUserDao.findUser(userName, ldapTemplate, acmSyncLdapConfig,
                acmSyncLdapConfig.getUserSyncAttributes());

        String dn = testUser.getDistinguishedName();

        long start = System.currentTimeMillis();
        LdapUser ldapUser = springLdapUserDao.findUserByLookup(dn, ldapTemplate, acmSyncLdapConfig);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("User found: {}", ldapUser.getDistinguishedName());
    }

    @Test
    public void findChangedLdapGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapGroup> result = springLdapDao.findGroupsPaged(ldapTemplate, acmSyncLdapConfig,
                Optional.of(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toString()));
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapGroup -> log.debug("Ldap Group: {}", ldapGroup.getName()));
    }

    @Test
    public void findChangedUsersWithSpecificAttributes()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig,
                Optional.of(ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toString()));
        long time = System.currentTimeMillis() - start;
        log.debug("Result: {}", result.size());
        log.debug("Time: {}ms", time);
    }
}
