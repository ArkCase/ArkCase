package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.utils.CmisConfigUtils;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dmiller on 11/7/2016.
 */
public class CmisFileWriter
{
    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

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
