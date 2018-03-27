package com.armedia.acm.services.users.service;

import static org.junit.Assert.*;

import com.armedia.acm.services.users.state.AcmUsersState;
import com.armedia.acm.services.users.state.AcmUsersStateProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class AcmUsersStateProviderTestIT
{

    @Autowired
    private AcmUsersStateProvider acmUsersStateProvider;

    @Test
    public void getModuleName()
    {
        assertEquals("acm-users", acmUsersStateProvider.getModuleName());
    }

    @Test
    public void getModuleState()
    {
        AcmUsersState acmUsersState = (AcmUsersState) acmUsersStateProvider.getModuleState();
        assertNotNull(acmUsersState.getNumberOfUsers());
        assertTrue(acmUsersState.getNumberOfUsers() >= 0);
    }
}