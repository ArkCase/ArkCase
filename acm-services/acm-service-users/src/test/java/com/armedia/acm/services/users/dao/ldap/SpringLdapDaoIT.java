package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.AcmLdapEntity;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.LdapGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.UnknownHostException;
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
                "/spring/spring-library-user-service-test-user-home-files.xml"}
)

public class SpringLdapDaoIT
{
    static final Logger log = LoggerFactory.getLogger(SpringLdapDaoIT.class);

    static final int RUNS = 10;

    @Autowired
    private SpringLdapDao springLdapDao;

    @Autowired
    private AcmLdapSyncConfig acmSyncLdapConfig;

    @Autowired
    private UserDao userDao;

    @Test
    public void userWithForwardSlash()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        AcmLdapEntityContextMapper mapper = springLdapDao.getMapper();

        // this is the example DN from JSAP that exposed the problem
        String dn = "CN=Long\\, Bradley D LCDR JCS J8/RAMO,OU=J8,OU=Users,OU=PNT,OU=Joint Staff,OU=Enterprise Tenants,DC=usr,DC=osd,DC=mil";

        // uncomment the following line to cause the test to fail.
        dn = dn.replaceAll("\\/", "\\\\/");

        System.out.println("New DN: " + dn);

        // if we get a CommunicationException / UnknownHostException, all is well, Java has procesed the DN ok
        // see http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4307193 for why this is necessary
        try
        {
            ldapTemplate.lookup(dn, mapper);
        } catch (CommunicationException ce)
        {
            boolean ok = false;
            if (ce.getCause() instanceof javax.naming.CommunicationException)
            {
                if (ce.getCause().getCause() instanceof UnknownHostException)
                {
                    // good - LDAP has tried to follow to "usr.osd.mil" but of course we can't find it
                    ok = true;
                }
            }
            if (!ok)
            {
                throw ce;
            }

        }
    }

    @Test
    public void findUsersWithAllAttributes()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<AcmUser> result = springLdapDao.findUsers(ldapTemplate, acmSyncLdapConfig);
        long time = System.currentTimeMillis() - start;
        log.debug("Time: {}ms", time);
        log.debug("Result: {}", result.size());
        result.forEach(acmUser ->
        {
            log.trace("AcmUser: {} -> {}", acmUser.getDistinguishedName(), acmUser.getLdapGroups());
        });
    }

    @Test
    public void findLdapGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        long start = System.currentTimeMillis();
        List<LdapGroup> result = springLdapDao.findGroups(ldapTemplate, acmSyncLdapConfig);
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
            List<AcmUser> result = springLdapDao.findUsers(ldapTemplate, acmSyncLdapConfig);
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
            List<AcmUser> result = springLdapDao.findUsers(ldapTemplate, acmSyncLdapConfig, attributes);
            long time = System.currentTimeMillis() - start;
            sum += time;
            log.debug("Result: {}", result.size());
            log.debug("Time: {}ms", time);
        }

        log.debug("Avg Time: {}ms", sum * 1.0 / RUNS);
    }

    @Test
    public void findUsersByLookup()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);
        List<AcmUser> users = userDao.findAll();
        long sum = 0;
        for (int i = 0; i < users.size(); ++i)
        {
            AcmUser user = users.get(0);
            String dn = String.format("CN=%s,CN=Users,DC=armedia,DC=com", user.getFullName());
            long start = System.currentTimeMillis();
            AcmLdapEntity result = springLdapDao.lookupUser(ldapTemplate, acmSyncLdapConfig, dn);
            long time = System.currentTimeMillis() - start;
            sum += time;
            log.debug("Result: {}", result);
            log.debug("Time: {}ms", time);
        }

        log.debug("Avg Time: {}ms", sum * 1.0 / users.size());
    }

    @Test
    public void findGroups()
    {
        LdapTemplate ldapTemplate = springLdapDao.buildLdapTemplate(acmSyncLdapConfig);

        List<LdapGroup> result = springLdapDao.findGroups(ldapTemplate, acmSyncLdapConfig);
        for (LdapGroup group : result)
        {
            log.debug("Group: {}", group.getGroupName());
        }
    }
}
