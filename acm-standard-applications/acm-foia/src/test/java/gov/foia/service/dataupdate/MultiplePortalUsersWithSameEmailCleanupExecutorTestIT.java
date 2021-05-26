package gov.foia.service.dataupdate;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.ldap.SpringLdapUserDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.AcmUserState;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Objects;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-websockets.xml",
        "classpath:/spring/spring-library-user-service-test.xml"
})
@IfProfileValue(name = "spring.profiles.active", value = "extension-foia")
public class MultiplePortalUsersWithSameEmailCleanupExecutorTestIT
{

    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("application.profile.reversed", "runtime");
    }

    @Autowired
    private MultiplePortalUsersWithSameEmailCleanupExecutor multiplePortalUsersWithSameEmailCleanupExecutor;

    @Autowired
    private UserDao userDao;

    @Autowired
    @Qualifier("foiaportal_sync")
    private AcmLdapSyncConfig acmLdapSyncConfig;

    @Autowired
    private SpringLdapUserDao springLdapUserDao;

    @Before
    public void setUp()
    {
        AcmUser user1 = new AcmUser();
        user1.setMail("test1@armedia.com");
        user1.setFirstName("TestUser1");
        user1.setLastName("TestUser1");
        user1.setUserDirectoryName("foiaportal");
        user1.setUserId("portal.jnasbjsdhb@arkcase.org");
        user1.setUserState(AcmUserState.VALID);
        userDao.save(user1);

        AcmUser user2 = new AcmUser();
        user2.setMail("test1@armedia.com");
        user2.setFirstName("TestUser2");
        user2.setLastName("TestUser2");
        user2.setUserDirectoryName("foiaportal");
        user2.setUserId("portal.kaskjkds@arkcase.org");
        user2.setUserState(AcmUserState.VALID);
        userDao.save(user2);

        AcmUser user3 = new AcmUser();
        user3.setMail("test1@armedia.com");
        user3.setFirstName("TestUser3");
        user3.setLastName("TestUser3");
        user3.setUserDirectoryName("foiaportal");
        user3.setUserId("portal.aklewhjb@arkcase.org");
        user3.setUserState(AcmUserState.VALID);
        userDao.save(user3);

        AcmUser user4 = new AcmUser();
        user4.setMail("test2@armedia.com");
        user4.setFirstName("TestUser4");
        user4.setLastName("TestUser4");
        user4.setUserDirectoryName("foiaportal");
        user4.setUserId("portal.asdaacxs@arkcase.org");
        user4.setUserState(AcmUserState.VALID);
        userDao.save(user4);

        AcmUser user5 = new AcmUser();
        user5.setMail("test2@armedia.com");
        user5.setFirstName("TestUser5");
        user5.setLastName("TestUser5");
        user5.setUserDirectoryName("foiaportal");
        user5.setUserId("portal.vceskml@arkcase.org");
        user5.setUserState(AcmUserState.VALID);
        userDao.save(user5);

    }

    @Test
    public void cleanDuplicateUsers() throws AcmLdapActionFailedException
    {
        expect(acmLdapSyncConfig.getUserPrefix()).andReturn("portal").anyTimes();
        springLdapUserDao.deleteUserEntry(anyString(), anyObject(AcmLdapSyncConfig.class));
        expectLastCall().anyTimes();

        replay(acmLdapSyncConfig);
        replay(springLdapUserDao);

        multiplePortalUsersWithSameEmailCleanupExecutor.execute();

        AcmUser user1 = userDao.findByPrefixAndEmailAddressAndValidState("portal", "test1@armedia.com");
        assertNotNull("Expected single user", user1);
        assertEquals(AcmUserState.VALID, user1.getUserState());

        AcmUser user2 = userDao.findByPrefixAndEmailAddressAndValidState("portal", "test2@armedia.com");
        assertNotNull("Expected single user", user2);
        assertEquals(AcmUserState.VALID, user2.getUserState());
    }
}
