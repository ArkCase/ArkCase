package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.mule.api.MuleMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by dmiller on 11/7/2016.
 */
public class CmisFileWriter
{
    public Document writeTestFile(MuleContextManager muleContextManager) throws Exception
    {
        // create a file that we can then declare as a record and set metadata on it

        String testPath = "/acm/test/folder";
        MuleMessage createFolderMessage = muleContextManager.send("vm://createFolder.in", testPath, null);
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

        MuleMessage addFileMessage = muleContextManager.send("vm://addFile.in", ecmFile, messageProperties);

        assertNotNull(addFileMessage);

        Document found = addFileMessage.getPayload(Document.class);
        return found;
    }
}
