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

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.CMIS_VERSION_SERIES_ID_S;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.camelcontext.utils.FileCamelUtils;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.services.search.model.solr.SolrContentDocument;
import com.armedia.acm.services.search.service.SendDocumentsToSolr;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-add-file-camel.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-acm-email.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-websockets.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-user-login.xml",
        "/spring/spring-library-ecm-file-sync.xml",
        "/spring/spring-library-plugin-manager.xml",
        "/spring/spring-library-audit-service.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-test-quartz-scheduler.xml"
})
public class ContentFileToSolrFlowIT
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

    private transient final Logger log = LogManager.getLogger(getClass());
    @Autowired
    private CamelContextManager camelContextManager;
    @Autowired
    private SendDocumentsToSolr sendDocumentsToSolr;

    private String testFolderId;

    private static final String testPath = "/Sites/acm/documentLibrary/test";

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
    public void sendFile() throws Exception
    {
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
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProperties.put(PropertyIds.NAME, FileCamelUtils.replaceSurrogateCharacters(ecmFile.getFileName(), 'X'));
        messageProperties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/plain");

        Document newDocument = (Document) camelContextManager.send(ArkCaseCMISActions.CREATE_DOCUMENT, messageProperties);

        assertNotNull(newDocument);

        assertNotNull(newDocument.getVersionSeriesId());
        assertNotNull(newDocument.getContentStreamMimeType());
        assertNotNull(newDocument.getVersionLabel());

        log.debug("doc id: {}", newDocument.getVersionSeriesId());

        SolrContentDocument solrContentDocument = new SolrContentDocument();
        solrContentDocument.setAdditionalProperty(CMIS_VERSION_SERIES_ID_S, newDocument.getVersionSeriesId());
        solrContentDocument.setAdditionalProperty("cmis_repository_id_s", ArkCaseCMISConstants.DEFAULT_CMIS_REPOSITORY_ID);
        solrContentDocument.setName("/spring/spring-library-add-file-camel" + System.currentTimeMillis() + ".xml");

        sendDocumentsToSolr.sendSolrContentFileIndexDocuments(Collections.singletonList(solrContentDocument));

        // wait for JMS to do its thing
        Thread.sleep(30000);

    }
}
