package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

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
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-drools-rule-monitor.xml"
})
public class CompleteRecordServiceIT
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
    @Qualifier("findFolderService")
    private AlfrescoService<Folder> findFolderService;

    @Autowired
    @Qualifier("createOrFindRecordFolderService")
    private AlfrescoService<String> findRecordFolderService;

    @Autowired
    @Qualifier("moveToRecordFolderService")
    private AlfrescoService<String> moveToRecordFolderService;

    @Autowired
    @Qualifier("completeRecordService")
    private AlfrescoService<String> service;

    private String ecmFileId;

    private CmisFileWriter cmisFileWriter = new CmisFileWriter();

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        Document testFile = cmisFileWriter.writeTestFile(muleContextManager);
        ecmFileId = testFile.getVersionSeriesId();
    }

    @Test
    public void completeRecord() throws Exception
    {
        assertNotNull(declareRecordService);

        Map<String, Object> declareRecordContext = new HashMap<>();

        LOG.info("Working with ecmFileId {}", ecmFileId);
        declareRecordContext.put("ecmFileId", ecmFileId);

        String actedOnId = declareRecordService.service(declareRecordContext);

        assertEquals(ecmFileId, actedOnId);

        Map<String, Object> metadataContext = new HashMap<>();
        metadataContext.put("ecmFileId", ecmFileId);
        metadataContext.put("publicationDate", new Date());
        metadataContext.put("originator", "Jerry Garcia");
        metadataContext.put("originatingOrganization", "Grateful Dead");
        metadataContext.put("dateReceived", new Date());

        String metadataId = setRecordMetadataService.service(metadataContext);
        assertEquals(ecmFileId, metadataId);

        // find a category folder
        Map<String, Object> categoryContext = new HashMap<>();
        categoryContext.put("folderPath", "Complaints");
        Folder folder = findFolderService.service(categoryContext);

        // create a record folder
        Map<String, Object> recordFolderContext = new HashMap<>();
        recordFolderContext.put("parentFolder", folder);
        String folderName = UUID.randomUUID().toString();
        recordFolderContext.put("recordFolderName", folderName);
        String recordFolderId = findRecordFolderService.service(recordFolderContext);

        // move record
        Map<String, Object> moveContext = new HashMap<>();
        moveContext.put("ecmFileId", ecmFileId);
        moveContext.put("recordFolderId", recordFolderId);
        String movedId = moveToRecordFolderService.service(moveContext);

        assertEquals(ecmFileId, movedId);

        // complete the record
        Map<String, Object> context = new HashMap<>();
        context.put("ecmFileId", ecmFileId);
        String completeMessage = service.service(context);
        assertTrue(completeMessage.contains(ecmFileId));

    }
}
