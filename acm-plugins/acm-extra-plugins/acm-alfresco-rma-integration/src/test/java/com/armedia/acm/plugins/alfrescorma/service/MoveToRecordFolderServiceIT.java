package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-alfresco-records-service-test.xml",
        "/spring/spring-library-alfresco-service.xml",
        "/spring/spring-library-alfresco-services-requiring-ecm.xml",
        "/spring/spring-library-alfresco-rma-integration.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml"
})
public class MoveToRecordFolderServiceIT
{
    @Autowired
    private MuleContextManager muleContextManager;

    @Autowired
    @Qualifier("declareRecordService")
    private AlfrescoService<String> declareRecordService;

    @Autowired
    @Qualifier("setRecordMetadataService")
    private AlfrescoService<String> setRecordMetadataService;

    @Autowired
    @Qualifier("alfrescoFindCategoryFolderService")
    private AlfrescoService<CmisObject> findCategoryFolderService;

    @Autowired
    @Qualifier("createOrFindRecordFolderService")
    private AlfrescoService<String> findRecordFolderService;

    @Autowired
    @Qualifier("moveToRecordFolderService")
    private AlfrescoService<String> service;

    @Autowired
    @Qualifier("alfrescoGetTicketService")
    private AlfrescoService<String> ticketService;

    private String ecmFileId;

    private CmisFileWriter cmisFileWriter = new CmisFileWriter();

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        Document testFile = cmisFileWriter.writeTestFile(muleContextManager);
        ecmFileId = testFile.getVersionSeriesId();
    }

    @Test
    public void moveToRecordFolder() throws Exception
    {
        assertNotNull(declareRecordService);

        String ticket = ticketService.service(null);

        Map<String, Object> declareRecordContext = new HashMap<>();

        declareRecordContext.put("ecmFileId", ecmFileId);
        declareRecordContext.put("ticket", ticket);

        String actedOnId = declareRecordService.service(declareRecordContext);

        assertEquals(ecmFileId, actedOnId);

        Map<String, Object> metadataContext = new HashMap<>();
        metadataContext.put("ecmFileId", ecmFileId);
        metadataContext.put("ticket", ticket);
        metadataContext.put("publicationDate", new Date());
        metadataContext.put("originator", "Jerry Garcia");
        metadataContext.put("originatingOrganization", "Grateful Dead");
        metadataContext.put("dateReceived", new Date());

        String metadataId = setRecordMetadataService.service(metadataContext);
        assertEquals(ecmFileId, metadataId);

        // find a category folder
        Map<String, Object> categoryContext = new HashMap<>();
        // J1 only works in JSAP extension, when forward-porting to ArkCase use a different path here
        categoryContext.put("categoryFolderPath", "J1");
        CmisObject cmisObject = findCategoryFolderService.service(categoryContext);
        String categoryFolderName = cmisObject.getName();

        // create a record folder
        Map<String, Object> recordFolderContext = new HashMap<>();
        recordFolderContext.put("ticket", ticket);
        recordFolderContext.put("categoryFolder", cmisObject);
        String folderName = UUID.randomUUID().toString();
        recordFolderContext.put("recordFolderName", folderName);
        String recordFolderId = findRecordFolderService.service(recordFolderContext);


        // now we can finally move our record
        Map<String, Object> context = new HashMap<>();
        context.put("ecmFileId", ecmFileId);
        context.put("ticket", ticket);
        context.put("recordFolderId", recordFolderId);
        String movedId = service.service(context);

        assertEquals(ecmFileId, movedId);


    }
}
