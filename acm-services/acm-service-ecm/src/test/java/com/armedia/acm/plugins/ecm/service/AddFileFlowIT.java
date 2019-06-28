package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-ecm-plugin-test-mule.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-add-file-mule.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-test-quartz-scheduler.xml" })
public class AddFileFlowIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    @Autowired
    private MuleContextManager muleContextManager;

    private String testFolderId;

    private Logger log = LogManager.getLogger(getClass());

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

        try (InputStream foundIs = filePayload.getStream(); InputStream originalIs = uploadFile.getInputStream())
        {
            List<String> downloadedLines = IOUtils.readLines(foundIs);
            List<String> originalLines = IOUtils.readLines(originalIs);
            assertNotNull(downloadedLines);
            assertTrue(!downloadedLines.isEmpty());

            assertEquals(originalLines, downloadedLines);
        }
    }

    @Ignore
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
        try
        {

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
        catch (TransformerException te)
        {
            if (te.getMessage() != null)
            {
                log.debug("Transformer message: {}", te.getMessage());
                if (te.getMessage().contains("Could not find a transformer to transform"))
                {
                    log.info("Chemistry is not running - skipping this test.");
                }
                else
                {
                    throw te;
                }
            }
        }
    }
}
