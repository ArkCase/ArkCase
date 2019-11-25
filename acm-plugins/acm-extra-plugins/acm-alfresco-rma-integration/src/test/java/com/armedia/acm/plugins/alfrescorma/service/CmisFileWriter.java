package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import com.armedia.acm.plugins.ecm.utils.EcmFileCamelUtils;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.api.MuleMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class CmisFileWriter
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

    public Document writeTestFile(CamelContextManager camelContextManager) throws Exception
    {
        CmisConfigUtils cmisConfigUtils = new CmisConfigUtils();
        cmisConfigUtils.setCamelContextManager(camelContextManager);

        // create a file that we can then declare as a record and set metadata on it
        Map<String, Object> properties = new HashMap<>();
        String cmisRepositoryId = "alfresco";
        Object alfresco = muleContextManager.getMuleContext().getRegistry().lookupObject(cmisRepositoryId);
        properties.put("configRef", alfresco);

        String testPath = "/acm/test/folder";
        MuleMessage createFolderMessage = muleContextManager.send("vm://createFolder.in", testPath, properties);
        CmisObject folder = (CmisObject) createFolderMessage.getPayload();
        String folderId = folder.getId();

        Resource uploadFile = new ClassPathResource("/spring/spring-alfresco-records-service-test.xml");
        InputStream is = uploadFile.getInputStream();

        EcmFile ecmFile = new EcmFile();

        ecmFile.setFileName("spring-alfresco-records-service-test.xml-" + System.currentTimeMillis());

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", folderId);
        messageProperties.put("inputStream", is);
        messageProperties.put(EcmFileConstants.CMIS_REPOSITORY_ID, ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID);
        messageProperties.put(EcmFileConstants.VERSIONING_STATE, camelContextManager.getRepositoryConfigs()
                .get(ArkCaseCMISConstants.CAMEL_CMIS_DEFAULT_REPO_ID).getCmisVersioningState());
        messageProperties.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, EcmFileCamelUtils.getCmisUser());
        messageProperties.put(PropertyIds.NAME, ecmFile.getFileName());
        messageProperties.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, "text/plain");

        Document newDocument = (Document) camelContextManager.send(ArkCaseCMISActions.CREATE_DOCUMENT, messageProperties);

        assertNotNull(newDocument);

        LOG.info("Created file with id {}", newDocument.getVersionSeriesId());

        return newDocument;
    }
}
