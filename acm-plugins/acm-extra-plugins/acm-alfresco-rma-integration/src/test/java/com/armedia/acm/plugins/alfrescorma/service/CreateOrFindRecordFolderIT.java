package com.armedia.acm.plugins.alfrescorma.service;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class CreateOrFindRecordFolderIT
{
    @Autowired
    @Qualifier("createOrFindRecordFolderService")
    private AlfrescoService<String> service;

    @Autowired
    @Qualifier("alfrescoFindCategoryFolderService")
    private AlfrescoService<CmisObject> categoryFolderService;

    @Autowired
    @Qualifier("alfrescoGetTicketService")
    private AlfrescoService<String> ticketService;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Test
    public void createOrFindRecordFolder() throws Exception
    {
        assertNotNull(service);

        String ticket = ticketService.service(null);

        Map<String, Object> findFolderContext = new HashMap<>();

        // J1 only works in JSAP extension, when forward-porting to ArkCase use a different path here
        findFolderContext.put("categoryFolderPath", "J1");
        CmisObject categoryFolder = categoryFolderService.service(findFolderContext);

        assertNotNull(categoryFolder);

        Map<String, Object> context = new HashMap<>();
        context.put("ticket", ticket);
        context.put("categoryFolder", categoryFolder);

        String folderName = UUID.randomUUID().toString();
        context.put("recordFolderName", folderName);

        // first try should create it.
        String folderIdFirstLookup = service.service(context);

        assertNotNull(folderIdFirstLookup);

        // second try should find the existing folder
        String folderIdSecondLookup = service.service(context);

        assertNotNull(folderIdSecondLookup);

        assertEquals(folderIdFirstLookup, folderIdSecondLookup);

    }

}
