package com.armedia.acm.services.dataaccess.service.impl;

import com.armedia.acm.services.dataaccess.web.api.AccessControlRulesAPIController;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-web-dac-api.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        // "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-particpants.xml",
        // "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        // "/spring/spring-library-search.xml",
        // "/spring/spring-library-user-service.xml",
        "/spring/spring-library-data-access-test.xml"
})
public class AspectIT
{

    @Inject
    AccessControlRulesAPIController accessControlRulesAPIController;

    @Test
    public void callCustomAnotationFucntion() throws Exception
    {
        String ret = testAnotationFunction();
        System.out.println(ret);
    }

    @DecoratedAssignedObjectParticipants
    public String testAnotationFunction()
    {
        return "just a test";
    }

}
