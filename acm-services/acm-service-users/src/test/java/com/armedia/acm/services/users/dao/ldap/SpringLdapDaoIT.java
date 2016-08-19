package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.model.ldap.AcmUserGroupsContextMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by dmiller on 6/28/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"/spring/spring-library-user-service.xml",
                "/spring/spring-library-acm-encryption.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-config-user-service-test-dummy-beans.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-user-service-test-user-home-files.xml",
                "/spring/spring-library-search.xml"}
)

public class SpringLdapDaoIT
{
    static final Logger log = LoggerFactory.getLogger(SpringLdapDaoIT.class);

    static final int RUNS = 10;

    @Autowired
    private SpringLdapDao springLdapDao;

    @Autowired
    private AcmLdapSyncConfig acmSyncLdapConfig;

    @Test
    public void findUsersWithAllAttributes()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<AcmUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(acmUser ->
        {
            log.debug("AcmUser: {} : {} -> {}", acmUser.getUserId(), acmUser.getDistinguishedName(), acmUser.getLdapGroups());
        });
    }

    @Test
    public void findLdapGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapGroup> result = springLdapDao.findGroupsPaged(ldapTemplate, acmSyncLdapConfig);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(ldapGroup ->
        {
            log.trace("Ldap Group: {} -> {}", ldapGroup.getGroupName(), ldapGroup.getMemberOfGroups());
        });
    }

    @Test
    public void findUsersWithAllAttributesPerformance()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long sum = 0;
        for (int i = 0; i < RUNS; ++i)
        {
            long start = System.currentTimeMillis();
            List<AcmUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig);
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
        String[] attributes = new String[]{
                "cn", "sn", "givenName", "dn", "distinguishedname", "sAMAccountName", "mail"
        };
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long sum = 0;
        for (int i = 0; i < RUNS; ++i)
        {
            long start = System.currentTimeMillis();
            List<AcmUser> result = springLdapDao.findUsersPaged(ldapTemplate, acmSyncLdapConfig, attributes);
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
        AcmUser acmUser = springLdapDao.findUser(userName, ldapTemplate, acmSyncLdapConfig,
                AcmUserGroupsContextMapper.USER_LDAP_ATTRIBUTES);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("User found: {}", acmUser.getDistinguishedName());
    }
}
