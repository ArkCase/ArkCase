package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.LdapGroup;
import com.armedia.acm.services.users.model.ldap.LdapUser;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by dmiller on 6/28/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = { "/spring/spring-library-user-service.xml",
                "/spring/spring-library-acm-encryption.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-config-user-service-test-dummy-beans.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-user-service-test-user-home-files.xml",
                "/spring/spring-library-search.xml" }
)

public class SpringLdapDaoIT
{
    static final Logger log = LoggerFactory.getLogger(SpringLdapDaoIT.class);

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
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, null);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapUser ->
        {
            log.debug("AcmUser: {} : {}", ldapUser.getUserId(), ldapUser.getDistinguishedName());
        });
    }

    @Test
    public void findLdapGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapGroup> result = springLdapDao.findGroupsPaged(ldapTemplate, acmSyncLdapConfig, null);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapGroup ->
                log.trace("Ldap Group: {}", ldapGroup.getName())
        );
    }

    @Test
    public void findUsersWithAllAttributesPerformance()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long sum = 0;
        for (int i = 0; i < RUNS; ++i)
        {
            long start = System.currentTimeMillis();
            List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, null);
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
            List<LdapUser> result = springLdapDao.findUsers(ldapTemplate, acmSyncLdapConfig, attributes, null);
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

        String userName = "ann-acm";
        long start = System.currentTimeMillis();
        LdapUser
                ldapUser =
                springLdapUserDao.findUser(userName, ldapTemplate, acmSyncLdapConfig, acmSyncLdapConfig.getUserSyncAttributes());
        long time = System.currentTimeMillis() - start;
        log.debug("Time: [{}ms]", time);
        log.debug("User found: [{}]", ldapUser.getDistinguishedName());
    }

    @Test
    public void findUserByLookup()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);

        String dn = "uid=ann-acm,cn=Users,dc=armedia,dc=com";
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
                ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toString());
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapGroup ->
                log.debug("Ldap Group: {}", ldapGroup.getName())
        );
    }

    @Test
    public void findChangedUsersWithSpecificAttributes()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig,
                ZonedDateTime.now(ZoneOffset.UTC).minusDays(1).toString());
        long time = System.currentTimeMillis() - start;
        log.debug("Result: {}", result.size());
        log.debug("Time: {}ms", time);
    }
}

