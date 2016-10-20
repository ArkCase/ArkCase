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
        "/spring/spring-library-case-file-queue-service.xml",
        "/spring/spring-library-case-file-rules.xml",
        "/spring/spring-library-case-file-dao.xml",
        "/spring/spring-library-user-tracker.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
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
