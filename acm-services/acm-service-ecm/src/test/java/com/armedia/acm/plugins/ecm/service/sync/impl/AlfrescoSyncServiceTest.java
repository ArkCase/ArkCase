package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

/**
 * Created by dmiller on 5/15/17.
 */
public class AlfrescoSyncServiceTest
{
    private AlfrescoSyncService unit;

    private PropertyFileManager propertyFileManager = EasyMock.createMock(PropertyFileManager.class);

    private AlfrescoAuditApplicationRestClient auditApplicationRestClient = EasyMock.createMock(AlfrescoAuditApplicationRestClient.class);

    private ApplicationEventPublisher applicationEventPublisher = EasyMock.createMock(ApplicationEventPublisher.class);

    private EcmAuditResponseReader ecmAuditResponseReader = new AlfrescoNodeServiceCreateNodeAuditResponseReader();

    private String auditApplicationLastAuditIdProperties = "/home/arkuser/path/to/lastAuditId.properties";

    private JSONObject auditResponse;

    private JSONObject emptyAuditResponse;

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

        final Resource auditResponseResource = new ClassPathResource("json/SampleAlfrescoNodeServiceCreateNodeAuditResponse.json");
        String auditResponseString = FileUtils.readFileToString(auditResponseResource.getFile());
        auditResponse = new JSONObject(auditResponseString);

        final Resource emptyAuditResponseResource = new ClassPathResource("json/SampleAlfrescoEmptyAuditResponse.json");
        String emptyAuditResponseString = FileUtils.readFileToString(emptyAuditResponseResource.getFile());
        emptyAuditResponse = new JSONObject(emptyAuditResponseString);
    }

    @Test
    public void queryAlfrescoAuditApplications() throws Exception
    {

        for (Map.Entry<String, EcmAuditResponseReader> app : unit.getAuditApplications().entrySet())
        {
            // generate random audit id from 10 to 10,000
            Long randomAuditId = Integer.valueOf(new Random().nextInt(10000 - 10) + 10).longValue();

            expect(propertyFileManager.load(auditApplicationLastAuditIdProperties, app.getKey() + ".lastAuditId", "0"))
                    .andReturn(String.valueOf(randomAuditId));

            // we want to start from the next audit record... so we don't retrieve the last one we got before
            expect(auditApplicationRestClient.service(app.getKey(), randomAuditId + 1)).andReturn(auditResponse);

            // the last audit ID in our sample audit response is 61
            propertyFileManager.storeMultiple(
                    Collections.singletonMap(app.getKey() + ".lastAuditId", "61"),
                    auditApplicationLastAuditIdProperties,
                    false);

            // our sample response has 3 events
            applicationEventPublisher.publishEvent(anyObject(EcmEvent.class));
            expectLastCall().times(3);
        }

        replay(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);

        unit.queryAlfrescoAuditApplications();

        verify(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);


    }

    @Test
    public void updatePropertiesWithLastAuditId_noAuditsFound() throws Exception
    {

        replay(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);

        try
        {
            unit.updatePropertiesWithLastAuditId("lastAuditIdKey", emptyAuditResponse);
        } catch (JSONException e)
        {
            System.out.println("in catch block");
            fail("Shouldn't have JSON exceptions for an empty audit response - " + e.getMessage());
        }

        verify(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);


    }


}
