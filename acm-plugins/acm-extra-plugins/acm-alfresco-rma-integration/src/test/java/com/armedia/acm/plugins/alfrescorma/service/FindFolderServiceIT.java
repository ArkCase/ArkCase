package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.web.api.MDCConstants;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
public class FindFolderServiceIT
{
    @Autowired
    @Qualifier("findFolderService")
    private AlfrescoService<Folder> service;

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    @Test
    public void findCategoryFolder() throws Exception
    {
        assertNotNull(service);

        Map<String, Object> context = new HashMap<>();

        context.put("folderPath", "Complaints");

        CmisObject cmisObject = service.service(context);

        assertNotNull(cmisObject);
    }
}
