package com.armedia.acm.services.users.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-data-source.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-config-user-service-test-dummy-beans.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-converter.xml"
})
public class UserDaoIT
{

    @Autowired
    private UserDao userDao;

    @Test
    public void getUserCount()
    {
        assertNotNull(userDao);
        assertTrue(userDao.getUserCount(LocalDate.now().atTime(23, 59, 59)) >= 0);
    }
}