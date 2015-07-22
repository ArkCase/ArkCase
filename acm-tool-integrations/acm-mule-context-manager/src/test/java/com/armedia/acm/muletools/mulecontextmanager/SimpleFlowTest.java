package com.armedia.acm.muletools.mulecontextmanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-simple-flow.xml"
})
public class SimpleFlowTest
{
    @Autowired
    private MuleContextManager muleContextManager;

    @Test
    public void simpleFlow() throws Exception
    {
        muleContextManager.send("vm://simpleFlow.in", "payload");
    }
}
