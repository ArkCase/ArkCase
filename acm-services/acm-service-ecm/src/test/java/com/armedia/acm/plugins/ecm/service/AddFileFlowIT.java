package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ecm-plugin-test-mule.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-add-file-mule.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml"})
public class AddFileFlowIT
{

    @Autowired
    private MuleContextManager muleContextManager;

    private String testFolderId;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
        
        String testPath = "/acm/test/folder";
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("alfresco"));
        MuleMessage message = muleContextManager.send("vm://getTestFolderId.in", testPath, messageProperties);
        String folderId = message.getPayloadAsString();

        testFolderId = folderId;

    }

    @Test
    public void muleAddFileAlfresco() throws Exception
    {
        assertNotNull(testFolderId);

        log.debug("Found folder id '{}'", testFolderId);

        Resource uploadFile = new ClassPathResource("/spring/spring-library-ecm-plugin-test-mule.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-library-ecm-plugin-test-mule.xml-" + System.currentTimeMillis());
        ecmFile.setFileActiveVersionMimeType("text/plain");

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put("inputStream", is);

        messageProperties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("alfresco"));
        messageProperties.put("versioningState", "MAJOR");
        MuleMessage message = muleContextManager.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(message);

        Document found = message.getPayload(Document.class);
        assertNotNull(found.getVersionSeriesId());
        assertNotNull(found.getContentStreamMimeType());
        assertNotNull(found.getVersionLabel());

        log.debug("doc id: {}", found.getVersionSeriesId());

        MuleMessage downloadedFile = muleContextManager.send("vm://downloadFileFlow.in", found.getVersionSeriesId(), messageProperties);
        ContentStream filePayload = (ContentStream) downloadedFile.getPayload();

        assertNotNull(filePayload);

        try ( InputStream foundIs = filePayload.getStream(); InputStream originalIs = uploadFile.getInputStream() )
        {
            List<String> downloadedLines = IOUtils.readLines(foundIs);
            List<String> originalLines = IOUtils.readLines(originalIs);
            assertNotNull(downloadedLines);
            assertTrue(!downloadedLines.isEmpty());

            assertEquals(originalLines, downloadedLines);
        }
    }

    @Test
    public void muleAddFileOpencmis() throws Exception
    {
        String testPath = "/acm/test/folder";
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("opencmis"));
        MuleMessage message = muleContextManager.send("vm://getTestFolderId.in", testPath, messageProperties);
        testFolderId = message.getPayloadAsString();
        assertNotNull(testFolderId);

        log.debug("Found folder id '{}'", testFolderId);

        Resource uploadFile = new ClassPathResource("/spring/spring-library-ecm-plugin-test-mule.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-library-ecm-plugin-test-mule.xml-" + System.currentTimeMillis());
        ecmFile.setFileActiveVersionMimeType("text/plain");

        messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put("inputStream", is);

        messageProperties.put("configRef", muleContextManager.getMuleContext().getRegistry().lookupObject("opencmis"));
        messageProperties.put("versioningState", "NONE");
        message = muleContextManager.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(message);

        Document found = message.getPayload(Document.class);
        assertNotNull(found.getVersionSeriesId());
        assertNotNull(found.getContentStreamMimeType());
        assertNotNull(found.getVersionLabel());

        log.debug("doc id: {}", found.getVersionSeriesId());

        MuleMessage downloadedFile = muleContextManager.send("vm://downloadFileFlow.in", found.getVersionSeriesId(), messageProperties);
        ContentStream filePayload = (ContentStream) downloadedFile.getPayload();

        assertNotNull(filePayload);

        try (InputStream foundIs = filePayload.getStream(); InputStream originalIs = uploadFile.getInputStream())
        {
            List<String> downloadedLines = IOUtils.readLines(foundIs);
            List<String> originalLines = IOUtils.readLines(originalIs);
            assertNotNull(downloadedLines);
            assertTrue(!downloadedLines.isEmpty());

            assertEquals(originalLines, downloadedLines);
        }
    }
}
