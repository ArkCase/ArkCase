package com.armedia.acm.cmis;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml"
})
public class MuleCmisIT
{
    @Autowired
    private MuleContextManager muleContextManager;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void createOrFindFolder() throws Exception
    {
        String path = "/Sites/acm/documentLibrary/Complaints/testComplaint";

        Map<String, Object> properties = new HashMap<>();
        properties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("alfresco"));

        MuleMessage reply = muleContextManager.send("vm://createFolder.in", path, properties);

        log.info("Reply payload of type: " + reply.getPayload().getClass().getName());
    }
}
