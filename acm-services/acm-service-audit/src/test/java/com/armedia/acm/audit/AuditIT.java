package com.armedia.acm.audit;

import com.armedia.commons.audit.AuditActivity;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by dmiller on 2/18/14.
 */
public class AuditIT
{
    private ApplicationContext appContext;

    @Before
    public void setUp() throws Exception
    {
        appContext = new ClassPathXmlApplicationContext("spring/spring-library-audit-service.xml", "spring-library-audit-test.xml");
    }

    @Test
    public void audit() throws Exception
    {
        AuditActivity.Parameter one = new AuditActivity.Parameter("ipAddress", "testAddress");
        AuditActivity.Parameter two = new AuditActivity.Parameter("someParam", "someValue");
        AuditActivity.audit("trackId", "actor", "success", one, two);
        AuditActivity.audit("track2", "actor", "failed", one, two);

        // wait for the auditor to see and process the audit events.
        Thread.sleep(10000);
    }
}
