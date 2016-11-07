package com.armedia.acm.plugins.alfrescorma.service;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

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
public class AlfrescoFindCategoryFolderIT
{
    @Autowired
    @Qualifier("alfrescoFindCategoryFolderService")
    private AlfrescoService<CmisObject> service;

    @Test
    public void findCategoryFolder() throws Exception
    {
        assertNotNull(service);

        Map<String, Object> context = new HashMap<>();

        // J1 only works in JSAP extension, when forward-porting to ArkCase use a different path here
        context.put("categoryFolderPath", "J1");

        CmisObject cmisObject = service.service(context);

        assertNotNull(cmisObject);

        System.out.println("id: " + cmisObject.getId());

    }
}
