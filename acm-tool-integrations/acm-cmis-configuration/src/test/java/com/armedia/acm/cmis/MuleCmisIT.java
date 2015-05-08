package com.armedia.acm.cmis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class MuleCmisIT
{
    @Autowired
    private MuleClient muleClient;


    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void createOrFindFolder() throws Exception
    {
        String path = "/Sites/acm/documentLibrary/Complaints/testComplaint";

        MuleMessage reply = muleClient.send("vm://createFolder.in", path, null);

        log.info("Reply payload of type: " + reply.getPayload().getClass().getName());



    }
}
