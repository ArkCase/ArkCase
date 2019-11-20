package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

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

public class NotificationUtilsTest extends EasyMockSupport
{
    private List<AcmObjectType> acmObjectTypes;
    private Notification notification;
    private AcmApplication mockAcmAppConfiguration;
    private NotificationUtils notificationUtils;

    @Before
    public void setUp()
    {
        notification = new Notification();
        acmObjectTypes = new ArrayList<>();
        mockAcmAppConfiguration = createMock(AcmApplication.class);

        notificationUtils = new NotificationUtils();
        notificationUtils.setAcmAppConfiguration(mockAcmAppConfiguration);
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
        String expectedLink = String.format("/cases/%d/main", parentId);
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
        String expectedLink = String.format("/cases/%d/notes", relatedObjectId);

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
