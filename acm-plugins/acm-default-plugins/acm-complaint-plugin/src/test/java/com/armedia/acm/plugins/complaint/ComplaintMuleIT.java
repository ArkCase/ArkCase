package com.armedia.acm.plugins.complaint;

import com.armedia.acm.plugins.complaint.model.Complaint;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.api.context.MuleContextFactory;
import org.mule.config.ConfigResource;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/spring-library-data-source.xml",
        "/spring/spring-library-complaint-plugin-test.xml",
        "/spring/spring-library-complaint.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class ComplaintMuleIT
{
    private MuleContext muleContext;

    @Autowired
    private ApplicationContext applicationContext;

    private ComplaintFactory complaintFactory = new ComplaintFactory();

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {

        assertNotNull(applicationContext);

        // mule
        ConfigResource[] configs = { new ConfigResource("flows/saveComplaintFlow.xml") };

        SpringXmlConfigurationBuilder builder = new SpringXmlConfigurationBuilder(configs);

        builder.setParentContext(applicationContext);
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        muleContext = muleContextFactory.createMuleContext(builder);
        muleContext.start();
    }

    @After
    public void shutDown() throws Exception
    {
        muleContext.stop();
        muleContext.dispose();
    }

    @Test
    @Transactional
    public void saveComplaintFlow() throws Exception
    {
        Complaint complaint = complaintFactory.complaint();

        MuleMessage message = muleContext.getClient().send("vm://saveComplaint.in", complaint, null);

        Complaint saved = message.getPayload(Complaint.class);

        assertNotNull(saved.getId());

        log.info("New complaint id: " + saved.getId());
    }
}
