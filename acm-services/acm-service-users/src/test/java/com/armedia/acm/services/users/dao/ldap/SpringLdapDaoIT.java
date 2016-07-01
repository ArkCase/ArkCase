package com.armedia.acm.services.users.dao.ldap;

import com.armedia.acm.services.users.model.ldap.AcmLdapEntityContextMapper;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.UnknownHostException;


/**
 * Created by dmiller on 6/28/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        locations = {"/spring/spring-library-user-service.xml",
                "/spring/spring-library-acm-encryption.xml",
                "/spring/spring-library-user-service.xml",
                "/spring/spring-library-data-source.xml",
                "/spring/spring-library-property-file-manager.xml",
                "/spring/spring-config-user-service-test-dummy-beans.xml",
                "/spring/spring-library-context-holder.xml",
                "/spring/spring-library-user-service-test-user-home-files.xml"}
)

public class SpringLdapDaoIT
{
    @Autowired
    private SpringLdapDao springLdapDao;

    @Autowired
    private AcmLdapSyncConfig acmSyncLdapConfig;

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


}
