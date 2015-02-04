package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-search-service-test-content-file-mule.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-cmis-configuration.xml"
})
public class ContentFileToSolrFlowIT
{
    @Autowired
    private MuleClient muleClient;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * To run this test, just find a CMIS Object ID you want to test (by looking at your Alfresco installation),
     * and plug that into the EcmFileId.
     * @throws Exception
     */
    @Test
    @Ignore
    public void sendFile() throws Exception
    {
        EcmFile testFile = new EcmFile();

        testFile.setEcmFileId("workspace://SpacesStore/b556008e-a682-4e74-bace-3daf79f2fcfc");
        testFile.setFileName("ClearanceDenied.docx");
        testFile.setFileMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        testFile.setFileId(92389238L);

        Map<String, Object> headers = new HashMap<>();

        MuleMessage response = muleClient.send("jms://solrContentFile.in", testFile, headers);

        assertTrue(response.getPayload() != null && response.getPayload() instanceof String);

        assertNull(response.getExceptionPayload());

        log.debug("response: " + response.getPayloadAsString());




    }
}
