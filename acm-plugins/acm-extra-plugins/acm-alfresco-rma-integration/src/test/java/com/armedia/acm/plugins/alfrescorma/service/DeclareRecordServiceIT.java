package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-alfresco-records-service-test.xml",
        "/spring/spring-library-alfresco-service.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class DeclareRecordServiceIT
{
    @Autowired
    private MuleContextManager muleContextManager;

    @Autowired
    @Qualifier("declareRecordService")
    private AlfrescoService<String> service;

    @Autowired
    @Qualifier("alfrescoGetTicketService")
    private AlfrescoService<String> ticketService;

    private String ecmFileId;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        // create a file that we can then declare as a record

        String testPath = "/acm/test/folder";
        MuleMessage createFolderMessage = muleContextManager.send("vm://createFolder.in", testPath, null);
        CmisObject folder = (CmisObject) createFolderMessage.getPayload();
        String folderId = folder.getId();

        LOG.debug("Folder ID: {}", folderId);

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
        ecmFileId = found.getId();
    }

    @Test
    public void declareRecord() throws Exception
    {
        assertNotNull(service);

        String ticket = ticketService.service(null);

        Map<String, Object> context = new HashMap<>();

        context.put("ecmFileId", ecmFileId);
        context.put("ticket", ticket);

        String retval = service.service(context);

        assertEquals(ecmFileId, retval);


    }
}
