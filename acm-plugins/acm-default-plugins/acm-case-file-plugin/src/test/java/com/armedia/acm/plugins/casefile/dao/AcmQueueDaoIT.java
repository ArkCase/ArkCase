package com.armedia.acm.plugins.casefile.dao;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-object-history.xml",
        "/spring/spring-library-case-file.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/test-case-file-context.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-ms-outlook-plugin.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-profile.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-task.xml",
        "/spring/spring-library-event.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-note.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-business-process.xml",
        "/spring/spring-library-person.xml"
})
@TransactionConfiguration(defaultRollback = true)
public class AcmQueueDaoIT
{
    @Autowired
    private AcmQueueDao acmQueueDao;

    @Autowired
    private AuditPropertyEntityAdapter auditAdapter;

    @Before
    public void setUp()
    {

        auditAdapter.setUserId("auditUser");
    }

    @Test
    public void getQueues()
    {
        assertNotNull(acmQueueDao);

        acmQueueDao.findAll();
    }
}
