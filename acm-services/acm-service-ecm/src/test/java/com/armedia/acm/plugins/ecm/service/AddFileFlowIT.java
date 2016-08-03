package com.armedia.acm.plugins.ecm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/spring-library-ecm-plugin-test-mule.xml", "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-context-holder.xml", "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml", "/spring/spring-library-add-file-mule.xml",
        "/spring/spring-library-cmis-configuration.xml", "/spring/spring-library-audit-service.xml" })
public class AddFileFlowIT
{

    @Autowired
    private MuleContextManager muleContextManager;

    private String testFolderId;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        String testPath = "/acm/test/folder";
        MuleMessage message = muleContextManager.send("vm://getTestFolderId.in", testPath, null);
        String folderId = message.getPayloadAsString();

        testFolderId = folderId;
    }

    @Test
    public void muleAddFile() throws Exception
    {
        assertNotNull(testFolderId);

        log.debug("Found folder id '" + testFolderId + "'");

        Resource uploadFile = new ClassPathResource("/spring/spring-library-ecm-plugin-test-mule.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-library-ecm-plugin-test-mule.xml-" + System.currentTimeMillis());
        ecmFile.setFileActiveVersionMimeType("text/plain");

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put("inputStream", is);

        MuleMessage message = muleContextManager.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(message);

        Document found = message.getPayload(Document.class);
        assertNotNull(found.getVersionSeriesId());
        assertNotNull(found.getContentStreamMimeType());
        assertNotNull(found.getVersionLabel());

        MuleMessage downloadedFile = muleContextManager.send("vm://downloadFileFlow.in", found.getVersionSeriesId(), null);
        ContentStream filePayload = (ContentStream) downloadedFile.getPayload();

        assertNotNull(filePayload);

        InputStream payloadStream = null;
        try
        {
            payloadStream = filePayload.getStream();
            assertTrue(payloadStream.available() > 0);

            assertEquals(uploadFile.contentLength(), payloadStream.available());
        } finally
        {
            if (payloadStream != null)
            {
                payloadStream.close();
            }
        }
    }
}
