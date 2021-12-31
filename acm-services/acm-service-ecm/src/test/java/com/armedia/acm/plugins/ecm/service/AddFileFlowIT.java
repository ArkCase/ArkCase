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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.utils.FileCamelUtils;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.camel.component.cmis.CamelCMISConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-add-file-camel.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-test-quartz-scheduler.xml" })
public class AddFileFlowIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
        System.setProperty("javax.net.ssl.trustStore", userHomePath + "/.arkcase/acm/private/arkcase.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
        System.setProperty("application.profile.reversed", "runtime");
    }

    private static final String testPath = "/Sites/acm/documentLibrary/test";

    @Autowired
    private CamelContextManager camelContextManager;

    private String testFolderId;

    private Logger log = LogManager.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "ann-acm");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        Map<String, Object> messageProperties = new HashMap<>();

        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        messageProperties.put(PropertyIds.PATH, testPath);
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        Folder folder = (Folder) camelContextManager.send(ArkCaseCMISActions.GET_OR_CREATE_FOLDER_BY_PATH, messageProperties);

        String folderId = folder.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID);

        testFolderId = folderId;
    }

    @After
    public void teardown() throws Exception
    {
        Map<String, Object> messageProperties = new HashMap<>();

        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProperties.put(ArkCaseCMISConstants.ACM_FOLDER_ID, testFolderId);
        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);

        camelContextManager.send(ArkCaseCMISActions.DELETE_FOLDER, messageProperties);
    }

    @Test
    public void camelAddFileAlfresco() throws Exception
    {
        assertNotNull(testFolderId);

        log.debug("Found folder id '{}'", testFolderId);

        Resource uploadFile = new ClassPathResource("/spring/spring-library-add-file-camel.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-library-add-file-camel.xml-" + System.currentTimeMillis());
        ecmFile.setFileActiveVersionMimeType("text/plain");

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", testFolderId);

        messageProperties.put(ArkCaseCMISConstants.INPUT_STREAM, is);

        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        messageProperties.put("versioningState", "MAJOR");
        messageProperties.put(PropertyIds.NAME, FileCamelUtils.replaceSurrogateCharacters(ecmFile.getFileName(), 'X'));
        messageProperties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/plain");
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        Document newDocument = (Document) camelContextManager.send(ArkCaseCMISActions.CREATE_DOCUMENT, messageProperties);

        assertNotNull(newDocument);

        assertNotNull(newDocument.getVersionSeriesId());
        assertNotNull(newDocument.getContentStreamMimeType());
        assertNotNull(newDocument.getVersionLabel());

        log.debug("doc id: {}", newDocument.getVersionSeriesId());

        messageProperties.put(CamelCMISConstants.CMIS_OBJECT_ID, newDocument.getVersionSeriesId());
        ContentStream filePayload = (ContentStream) camelContextManager.send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProperties);

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
    public void camelAddFileOpencmis() throws Exception
    {
        String testPath = "/acm/test/folder";
        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, "opencmis");
        messageProperties.put(PropertyIds.PATH, testPath);

        // TODO : Get or create folder by path
        Folder folder = (Folder) camelContextManager.send(ArkCaseCMISActions.GET_FOLDER, messageProperties);

        String folderId = folder.getPropertyValue(EcmFileConstants.REPOSITORY_VERSION_ID);
        testFolderId = folderId;
        assertNotNull(testFolderId);

        log.debug("Found folder id '{}'", testFolderId);

        Resource uploadFile = new ClassPathResource("/spring/spring-library-add-file-camel.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-library-add-file-camel.xml-" + System.currentTimeMillis());
        ecmFile.setFileActiveVersionMimeType("text/plain");

        messageProperties = new HashMap<>();

        messageProperties.put("cmisFolderId", testFolderId);
        messageProperties.put(ArkCaseCMISConstants.INPUT_STREAM, is);

        messageProperties.put(ArkCaseCMISConstants.CMIS_REPOSITORY_ID, "opencmis");
        messageProperties.put("versioningState", "NONE");
        messageProperties.put(PropertyIds.NAME, FileCamelUtils.replaceSurrogateCharacters(ecmFile.getFileName(), 'X'));
        messageProperties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/plain");
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());

        try
        {

            Document newDocument = (Document) camelContextManager.send(ArkCaseCMISActions.CREATE_DOCUMENT, messageProperties);

            assertNotNull(newDocument);

            assertNotNull(newDocument.getVersionSeriesId());
            assertNotNull(newDocument.getContentStreamMimeType());
            assertNotNull(newDocument.getVersionLabel());

            log.debug("doc id: {}", newDocument.getVersionSeriesId());
            messageProperties.put(CamelCMISConstants.CMIS_OBJECT_ID, newDocument.getVersionSeriesId());
            Document document = (Document) camelContextManager.send(ArkCaseCMISActions.DOWNLOAD_DOCUMENT, messageProperties);

            ContentStream filePayload = document.getContentStream();

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
        catch (Exception e)
        {
            if (e.getMessage() != null)
            {
                log.debug("Transformer message: {}", e.getMessage());
                if (e.getMessage().contains("Could not find a transformer to transform"))
                {
                    log.info("Chemistry is not running - skipping this test.");
                }
                else
                {
                    throw e;
                }
            }
        }
    }
}
