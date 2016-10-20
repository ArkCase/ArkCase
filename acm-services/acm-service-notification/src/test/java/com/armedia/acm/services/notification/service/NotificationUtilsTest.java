package com.armedia.acm.services.notification.service;


import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

public class NotificationUtilsTest extends EasyMockSupport
{
    private static final String BASE_URL = "/arkcase";
    private List<AcmObjectType> acmObjectTypes;

    private Properties mockProperties;
    private Notification notification;
    private AcmApplication mockAcmAppConfiguration;
    private NotificationUtils notificationUtils;

    @Before
    public void setUp()
    {
        mockProperties = createMock(Properties.class);
        notification = new Notification();
        acmObjectTypes = new ArrayList<>();
        mockAcmAppConfiguration = createMock(AcmApplication.class);

        notificationUtils = new NotificationUtils();
        notificationUtils.setAcmAppConfiguration(mockAcmAppConfiguration);
        notificationUtils.setNotificationProperties(mockProperties);
    }
    /**
     * Test when object type is TOP LEVEL (parentType = CASE_FILE, COMPLAINT or TASK)
     */
    @Test
    public void createLinkWhenRelatedObjectTypeIsNull()
    {
        String parentType = "CASE_FILE";
        Long parentId = 1L;

        setNotification(parentType, parentId, null, null);

        // url pattern as defined in app-config.xml
        String expectedLink = String.format("%s/cases/%d/main", BASE_URL, parentId);
        Map<String, String> urlValues = new HashMap<>();
        urlValues.put(parentType, "/cases/%d/main");

        AcmObjectType objectType = new AcmObjectType();

        objectType.setUrl(urlValues);
        objectType.setName(parentType);

        acmObjectTypes.add(objectType);

        runTestBuildLink(expectedLink);
    }

    /**
     * Test when object type is nested in top level object = relatedObjectType (CASE_FILE, COMPLAINT or TASK)
     */
    @Test
    public void createLinkWhenRelatedObjectTypeNotNull()
    {
        String parentType = "NOTE";
        Long parentId = 1L;
        String relatedObjectType = "CASE_FILE";
        Long relatedObjectId = 2L;
        String expectedLink = String.format("%s/cases/%d/notes", BASE_URL, relatedObjectId);

        setNotification(parentType, parentId, relatedObjectType, relatedObjectId);

        Map<String, String> urlValues = new HashMap<>();
        urlValues.put("CASE_FILE", "/cases/%d/notes");

        AcmObjectType objectType = new AcmObjectType();

        objectType.setUrl(urlValues);
        objectType.setName(parentType);
        acmObjectTypes.add(objectType);

        runTestBuildLink(expectedLink);
    }

    /**
     * Test when object type is NOT found in known object types
     */
    @Test
    public void createLinkWhenObjectTypeNotFound()
    {
        objectTypeMatching(true, false);
    }

    /**
     * Test when object type does not match any of the object types
     */
    @Test
    public void createLinkWhenObjectTypeDoesNotMatch()
    {
        objectTypeMatching(false, false);
    }

    /**
     * Test when url values are not found for the given object type
     */
    @Test
    public void createLinkWhenUrlValuesNotFound()
    {
        objectTypeMatching(false, true);
    }

    void objectTypeMatching(boolean acmObjectTypesIsEmpty, boolean urlNotFound)
    {
        String parentType = "CASE_FILE";
        Long parentId = 1L;

        setNotification(parentType, parentId, null, null);
        // url pattern as defined in app-config.xml
        String expectedLink = null;

        if (!acmObjectTypesIsEmpty)
        {
            AcmObjectType objectType = new AcmObjectType();
            // different parent type
            objectType.setName("COMPLAINT");
            if (urlNotFound)
            {
                Map<String, String> urlValues = new HashMap<>();
                urlValues.put("COMPLAINT", "/complaint/%d/main");
                objectType.setUrl(urlValues);
                objectType.setName("CASE_FILE");
            }


            acmObjectTypes.add(objectType);
        }

        runTestBuildLink(expectedLink);
    }


    public void runTestBuildLink(String expectedLink)
    {
        expect(mockProperties.getProperty(NotificationConstants.BASE_URL_KEY)).andReturn(BASE_URL);
        expect(mockAcmAppConfiguration.getObjectTypes()).andReturn(acmObjectTypes);

        // when
        replayAll();
        String notificationLink = notificationUtils.buildNotificationLink(notification.getParentType(),
                notification.getParentId(), notification.getRelatedObjectType(), notification.getRelatedObjectId());
        // then
        verifyAll();
        assertEquals(expectedLink, notificationLink);
    }

    public void setNotification(String parentType, Long parentId, String relatedObjectType, Long relatedObjectId)
    {
        notification.setParentType(parentType);
        notification.setParentId(parentId);
        notification.setRelatedObjectType(relatedObjectType);
        notification.setRelatedObjectId(relatedObjectId);
    }
}
