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

import com.armedia.acm.services.notification.model.NotificationConfig;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

public class NotificationFormatterTest extends EasyMockSupport
{
    private NotificationFormatter notificationFormatter;
    private NotificationConfig mockConfig;

    @Before
    public void setUp()
    {
        notificationFormatter = new NotificationFormatter();
        mockConfig = createMock(NotificationConfig.class);
        notificationFormatter.setNotificationConfig(mockConfig);
    }

    /**
     * Test when notification title is not null and contains OBJECT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceObjectTypeLabelInTitle()
    {
        String parentType = "CASE_FILE";
        String expectedTitle = "Title with label placeholder: object_label";
        String actualTitle = "Title with label placeholder: " + NotificationConstants.TYPE_LABEL;

        expect(mockConfig.getLabelForObjectType(parentType)).andReturn("object_label");

        // when
        replayAll();
        String title = notificationFormatter.replaceFormatPlaceholders(actualTitle, null, parentType, null);

        // then
        verifyAll();
        assertEquals(title, expectedTitle);
    }
}
