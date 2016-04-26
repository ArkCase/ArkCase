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

public class NotificationFormatterTest extends EasyMockSupport
{
    private Properties mockProperties;

    private AcmApplication mockAcmAppConfiguration;

    private Notification notification;

    private NotificationFormatter notificationFormatter;

    private List<AcmObjectType> acmObjectTypes;

    private static final String BASE_URL = "/arkcase";

    @Before
    public void setUp()
    {
        mockProperties = createMock(Properties.class);
        mockAcmAppConfiguration = createMock(AcmApplication.class);
        notification = new Notification();
        notificationFormatter = new NotificationFormatter();
        notificationFormatter.setAcmAppConfiguration(mockAcmAppConfiguration);
        notificationFormatter.setNotificationProperties(mockProperties);
        acmObjectTypes = new ArrayList<>();
    }

    /**
     * Test when object type is TOP LEVEL (parentType = CASE_FILE, COMPLAINT or TASK)
     */
    @Test
    public void replaceAnchorWhenRelatedObjectTypeIsNull()
    {
        String parentType = "CASE_FILE";
        Long parentId = 1L;

        setNotification(parentType, parentId, null, null);

        // url pattern as defined in app-config.xml
        String expectedNote = String.format("Link: %s/cases/%d/main", BASE_URL, parentId);
        Map<String, String> urlValues = new HashMap<>();
        urlValues.put(parentType, "/cases/%d/main");

        AcmObjectType objectType = new AcmObjectType();

        objectType.setUrl(urlValues);
        objectType.setName(parentType);

        acmObjectTypes.add(objectType);

        runTestReplaceAnchor(expectedNote);
    }

    /**
     * Test when object type is nested in top level object = relatedObjectType (CASE_FILE, COMPLAINT or TASK)
     */
    @Test
    public void replaceAnchorWhenRelatedObjectTypeNotNull()
    {
        String parentType = "NOTE";
        Long parentId = 1L;
        String relatedObjectType = "CASE_FILE";
        Long relatedObjectId = 2L;
        String expectedNote = String.format("Link: %s/cases/%d/notes", BASE_URL, relatedObjectId);

        setNotification(parentType, parentId, relatedObjectType, relatedObjectId);

        Map<String, String> urlValues = new HashMap<>();
        urlValues.put("CASE_FILE", "/cases/%d/notes");

        AcmObjectType objectType = new AcmObjectType();

        objectType.setUrl(urlValues);
        objectType.setName(parentType);
        acmObjectTypes.add(objectType);

        runTestReplaceAnchor(expectedNote);
    }

    /**
     * Test when object type is NOT found in known object types
     */
    @Test
    public void replaceAnchorWhenObjectTypeNotFound()
    {
        objectTypeMatching(true, false);
    }

    /**
     * Test when object type does not match any of the object types
     */
    @Test
    public void replaceAnchorWhenObjectTypeDoesNotMatch()
    {
        objectTypeMatching(false, false);
    }

    /**
     * Test when url values are not found for the given object type
     */
    @Test
    public void replaceAnchorWhenUrlValuesNotFound()
    {
        objectTypeMatching(false, true);
    }

    void objectTypeMatching(boolean acmObjectTypesIsEmpty, boolean urlNotFound)
    {
        String parentType = "CASE_FILE";
        Long parentId = 1L;

        setNotification(parentType, parentId, null, null);
        // url pattern as defined in app-config.xml
        String expectedNote = String.format("Link: %s", NotificationConstants.ANCHOR_PLACEHOLDER);

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

        runTestReplaceAnchor(expectedNote);
    }

    /**
     * Test when notification note is null
     */
    @Test
    public void replaceObjectTypeLabelInTitle()
    {
        String parentType = "CASE_FILE";
        String keyLabel = parentType + ".label";
        String expectedTitle = "Title with label placeholder: object_label";
        notification.setNote(null);
        notification.setTitle("Title with label placeholder: " + NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER);
        notification.setParentType(parentType);

        expect(mockProperties.getProperty(keyLabel)).andReturn("object_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getTitle(), expectedTitle);
    }

    /**
     * Test when notification title is null and note contains OBJECT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceObjectTypeLabelInNote()
    {
        String parentType = "CASE_FILE";
        String keyLabel = parentType + ".label";
        String expectedNote = "Note with label placeholder: object_label";
        notification.setTitle(null);
        notification.setNote("Note with label placeholder: " + NotificationConstants.OBJECT_TYPE_LABEL_PLACEHOLDER);
        notification.setParentType(parentType);

        expect(mockProperties.getProperty(keyLabel)).andReturn("object_label");

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(updatedNotification.getNote(), expectedNote);
    }


    public void runTestReplaceAnchor(String expectedNote)
    {
        expect(mockProperties.getProperty(NotificationConstants.BASE_URL_KEY)).andReturn(BASE_URL);
        expect(mockAcmAppConfiguration.getObjectTypes()).andReturn(acmObjectTypes);

        // when
        replayAll();
        Notification updatedNotification = notificationFormatter.replaceFormatPlaceholders(notification);

        // then
        verifyAll();
        assertEquals(expectedNote, updatedNotification.getNote());
    }

    public void setNotification(String parentType, Long parentId, String relatedObjectType, Long relatedObjectId)
    {
        String note = String.format("Link: %s", NotificationConstants.ANCHOR_PLACEHOLDER);
        notification.setNote(note);
        notification.setParentType(parentType);
        notification.setParentId(parentId);
        notification.setRelatedObjectType(relatedObjectType);
        notification.setRelatedObjectId(relatedObjectId);
    }
}
