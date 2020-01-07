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

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleMessage;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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

    public Document writeTestFile(MuleContextManager muleContextManager) throws Exception
    {
        CmisConfigUtils cmisConfigUtils = new CmisConfigUtils();
        cmisConfigUtils.setMuleContextManager(muleContextManager);

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
        ecmFile.setFileActiveVersionMimeType("text/plain");

        Map<String, Object> messageProperties = new HashMap<>();
        messageProperties.put("cmisFolderId", folderId);
        messageProperties.put("inputStream", is);
        messageProperties.put("configRef", alfresco);
        messageProperties.put(EcmFileConstants.VERSIONING_STATE, cmisConfigUtils.getVersioningState(cmisRepositoryId));

        MuleMessage addFileMessage = muleContextManager.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(addFileMessage);

        Document found = addFileMessage.getPayload(Document.class);

        LOG.info("Created file with id {}", found.getVersionSeriesId());

        return found;
    }
}
