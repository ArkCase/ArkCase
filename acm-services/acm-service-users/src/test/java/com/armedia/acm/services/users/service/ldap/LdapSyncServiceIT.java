
package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.model.AcmUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class LdapSyncServiceIT
{
    static final Logger log = LoggerFactory.getLogger(LdapSyncServiceIT.class);

    @Autowired
    private LdapSyncService ldapSyncService;

    @Test
    public void userLdapSync()
    {
        String username = "ann-acm";
        AcmUser acmUser = ldapSyncService.ldapUserSync(username);
        log.debug("User '{}' synced", acmUser.getDistinguishedName());
        log.debug("User's groups {}", acmUser.getLdapGroups());
    }

}
