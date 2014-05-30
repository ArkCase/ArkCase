package com.armedia.acm.services.users.service.ldap;

import com.armedia.acm.services.users.dao.ldap.UserDao;
import com.armedia.acm.services.users.model.AcmRole;
import com.armedia.acm.services.users.model.AcmUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-config-user-service-test-dummy-beans.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class LdapSyncServiceIT
{
    @Autowired
    private LdapSyncService ldapSyncService;

    @Autowired
    private UserDao userDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void ldapSync()
    {
        // the sync is done via init-bean so we don't even have to call it here.  Just starting the Sprign
        // context makes the sync run.
//        ldapSyncService.ldapSync();

        List<AcmRole> roles = userDao.findAllRoles();

        log.info("Found " + roles.size() + " roles");

        if ( roles != null && ! roles.isEmpty() )
        {
            String firstRole = roles.get(0).getRoleName();

            List<AcmUser> usersWithRole = userDao.findUserWithRole(firstRole);

            log.info("Found " + usersWithRole.size() + " users with role " + firstRole);
        }

        List<String> roleNames = new ArrayList<>(roles.size());
        for ( AcmRole role : roles )
        {
            roleNames.add(role.getRoleName());
        }

        List<AcmUser> usersWithAnyRole = userDao.findUsersWithRoles(roleNames);
        log.info("Found " + usersWithAnyRole.size() + " users with any role.");
    }
}
