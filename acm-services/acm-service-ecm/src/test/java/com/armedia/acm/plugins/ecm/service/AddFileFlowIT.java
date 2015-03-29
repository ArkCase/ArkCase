package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-add-file-mule.xml",
        "/spring/spring-library-cmis-configuration.xml"
})
public class AddFileFlowIT
{

    @Autowired
    private MuleClient muleClient;

    private String testFolderId;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        String testPath = "/acm/test/folder";
        MuleMessage message = muleClient.send("vm://getTestFolderId.in", testPath, null);
        String folderId = message.getPayloadAsString();

        testFolderId = folderId;
    }

    @Test
    public void muleAddFile() throws Exception
    {
        assertNotNull(testFolderId);

        log.debug("Found folder id '" + testFolderId + "'");

        Resource uploadFile = new ClassPathResource("/log4j.properties");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("log4j.properties-" + System.currentTimeMillis());
        ecmFile.setFileMimeType("text/plain");

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put("inputStream", is);

        MuleMessage message = muleClient.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(message);

        Document found = message.getPayload(Document.class);
        assertNotNull(found.getVersionSeriesId());
        assertNotNull(found.getContentStreamMimeType());
        assertNotNull(found.getVersionLabel());

        MuleMessage downloadedFile = muleClient.send("vm://downloadFileFlow.in", found.getVersionSeriesId(), null);
        ContentStream filePayload = (ContentStream) downloadedFile.getPayload();

        assertNotNull(filePayload);

        InputStream payloadStream = null;
        try
        {
            payloadStream = filePayload.getStream();
            assertTrue(payloadStream.available() > 0);

            assertEquals(uploadFile.contentLength(), payloadStream.available());
        }
        finally
        {
            if (payloadStream != null )
            {
                payloadStream.close();
            }
        }
    }
}
