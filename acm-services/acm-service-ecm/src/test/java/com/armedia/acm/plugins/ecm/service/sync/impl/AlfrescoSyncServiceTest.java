package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import org.apache.commons.io.FileUtils;
import org.easymock.Capture;
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
import static org.junit.Assert.assertEquals;
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

    private EcmAuditResponseReader firstEcmAuditResponseReader = new AlfrescoNodeServiceCreateNodeAuditResponseReader();
    private EcmAuditResponseReader secondEcmAuditResponseReader = new AlfrescoFileFolderServiceCreateAuditResponseReader();

    private String auditApplicationLastAuditIdProperties = "/home/arkuser/path/to/lastAuditId.properties";

    private JSONObject firstAuditResponse;
    private JSONObject secondAuditResponse;

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
        applications.put("applicationOne", firstEcmAuditResponseReader);
        applications.put("applicationTwo", secondEcmAuditResponseReader);
        unit.setAuditApplications(applications);

        final Resource firstAuditResponseResource = new ClassPathResource("json/SampleAlfrescoNodeServiceCreateNodeAuditResponse.json");
        String firstAuditResponseString = FileUtils.readFileToString(firstAuditResponseResource.getFile());
        firstAuditResponse = new JSONObject(firstAuditResponseString);

        final Resource secondAuditResponseResource = new ClassPathResource("json/SampleAlfrescoFileFolderServiceCreateAuditResponse.json");
        String secondAuditResponseString = FileUtils.readFileToString(secondAuditResponseResource.getFile());
        secondAuditResponse = new JSONObject(secondAuditResponseString);

        final Resource emptyAuditResponseResource = new ClassPathResource("json/SampleAlfrescoEmptyAuditResponse.json");
        String emptyAuditResponseString = FileUtils.readFileToString(emptyAuditResponseResource.getFile());
        emptyAuditResponse = new JSONObject(emptyAuditResponseString);
    }

    @Test
    public void queryAlfrescoAuditApplications() throws Exception
    {


        // generate random audit id from 10 to 10,000
        Long firstAppAuditId = Integer.valueOf(new Random().nextInt(10000 - 10) + 10).longValue();
        Long secondAppAuditId = Integer.valueOf(new Random().nextInt(10000 - 10) + 10).longValue();

        expect(propertyFileManager.load(auditApplicationLastAuditIdProperties, "applicationOne.lastAuditId", "0"))
                .andReturn(String.valueOf(firstAppAuditId));
        expect(propertyFileManager.load(auditApplicationLastAuditIdProperties, "applicationTwo.lastAuditId", "0"))
                .andReturn(String.valueOf(secondAppAuditId));

        // we want to start from the next audit record... so we don't retrieve the last one we got before
        expect(auditApplicationRestClient.service("applicationOne", firstAppAuditId + 1)).andReturn(firstAuditResponse);
        expect(auditApplicationRestClient.service("applicationTwo", secondAppAuditId + 1)).andReturn(secondAuditResponse);

        // the last audit ID in the first audit response is 61
        propertyFileManager.storeMultiple(
                Collections.singletonMap("applicationOne.lastAuditId", "61"),
                auditApplicationLastAuditIdProperties,
                false);
        // the last audit ID in the second audit response is 91
        propertyFileManager.storeMultiple(
                Collections.singletonMap("applicationTwo.lastAuditId", "91"),
                auditApplicationLastAuditIdProperties,
                false);

        // 3 events from the first audit app, and 2 events from the second.  The events should be published
        // by order of the audit id in each event: 42, 52, 55, 57, 91
        Capture<EcmEvent> first = Capture.newInstance();
        Capture<EcmEvent> second = Capture.newInstance();
        Capture<EcmEvent> third = Capture.newInstance();
        Capture<EcmEvent> fourth = Capture.newInstance();
        Capture<EcmEvent> fifth = Capture.newInstance();
        applicationEventPublisher.publishEvent(capture(first));
        applicationEventPublisher.publishEvent(capture(second));
        applicationEventPublisher.publishEvent(capture(third));
        applicationEventPublisher.publishEvent(capture(fourth));
        applicationEventPublisher.publishEvent(capture(fifth));


        replay(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);

        unit.queryAlfrescoAuditApplications();

        verify(propertyFileManager, auditApplicationRestClient, applicationEventPublisher);

        assertEquals(42, first.getValue().getAuditId());
        assertEquals(52, second.getValue().getAuditId());
        assertEquals(55, third.getValue().getAuditId());
        assertEquals(57, fourth.getValue().getAuditId());
        assertEquals(91, fifth.getValue().getAuditId());

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
