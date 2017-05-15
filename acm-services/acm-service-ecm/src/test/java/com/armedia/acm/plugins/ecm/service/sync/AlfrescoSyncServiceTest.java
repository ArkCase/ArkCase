package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoAuditApplicationRestClient;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceCreateAuditResponseReader;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoSyncService;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.easymock.EasyMock.*;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncServiceTest
{
    private AlfrescoSyncService unit;

    private PropertyFileManager propertyFileManager = EasyMock.createMock(PropertyFileManager.class);

    private AlfrescoAuditApplicationRestClient auditApplicationRestClient = EasyMock.createMock(AlfrescoAuditApplicationRestClient.class);

    private ApplicationEventPublisher applicationEventPublisher = EasyMock.createMock(ApplicationEventPublisher.class);

    private EcmAuditResponseReader ecmAuditResponseReader = new AlfrescoFileFolderServiceCreateAuditResponseReader();

    private String auditApplicationLastAuditIdProperties = "/home/arkuser/path/to/lastAuditId.properties";

    private JSONObject auditResponse;

    @Before
    public void setUp() throws Exception
    {
        unit = new AlfrescoSyncService();

        unit.setPropertyFileManager(propertyFileManager);
        unit.setAuditApplicationRestClient(auditApplicationRestClient);
        unit.setAuditApplicationLastAuditIdsFilename(auditApplicationLastAuditIdProperties);
        unit.setApplicationEventPublisher(applicationEventPublisher);

        Map<String, EcmAuditResponseReader> applications = new HashMap<>();
        applications.put("applicationOne", ecmAuditResponseReader);
        applications.put("applicationTwo", ecmAuditResponseReader);
        unit.setAuditApplications(applications);

        final Resource fileFolderServiceCreateAuditResponseResource = new ClassPathResource("json/SampleAlfrescoFileFolderServiceCreateAuditResponse.json");
        String fileFolderServiceCreateAuditResponseString = FileUtils.readFileToString(fileFolderServiceCreateAuditResponseResource.getFile());
        auditResponse = new JSONObject(fileFolderServiceCreateAuditResponseString);
    }

    @Test
    public void queryAlfrescoAuditApplications() throws Exception
    {

        for (Map.Entry<String, EcmAuditResponseReader> app : unit.getAuditApplications().entrySet())
        {
            Long randomId = new Random().nextLong();
            expect(propertyFileManager.load(auditApplicationLastAuditIdProperties, app.getKey() + ".lastAuditId", "0"))
                    .andReturn(String.valueOf(randomId));

            expect(auditApplicationRestClient.service(app.getKey(), randomId)).andReturn(auditResponse);

            // the last audit ID in our sample audit response is 91
            propertyFileManager.store(app.getKey() + ".lastAuditId", "91", auditApplicationLastAuditIdProperties);

            // our sample response has 2 events
            applicationEventPublisher.publishEvent(anyObject(EcmEvent.class));
            expectLastCall().times(2);
        }

        replay(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);

        unit.queryAlfrescoAuditApplications();

        verify(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);


    }


}
