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

import com.armedia.acm.services.labels.service.ObjectLabelConfig;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

public class NotificationFormatterTest extends EasyMockSupport
{
    private NotificationFormatter notificationFormatter;
    private ObjectLabelConfig mockConfig;
    private TranslationService mockTranslationService;

    @Before
    public void setUp()
    {
        notificationFormatter = new NotificationFormatter();
        mockConfig = createMock(ObjectLabelConfig.class);
        mockTranslationService = createMock(TranslationService.class);
        notificationFormatter.setObjectLabelConfig(mockConfig);
        notificationFormatter.setTranslationService(mockTranslationService);
    }

    /**
     * Test when notification title is not null and contains OBJECT TYPE LABEL PLACEHOLDER
     */
    @Test
    public void replaceObjectTypeLabelInTitle()
    {
        String parentType = "CASE_FILE";
        String expectedTitle = "Title with label placeholder: Case File";
        String inputTitle = "Title with label placeholder: " + NotificationConstants.TYPE_LABEL;

        expect(mockConfig.getTypeLabel()).andReturn(Collections.singletonMap(parentType, "object.label.casefile"));
        expect(mockTranslationService.translate("object.label.casefile")).andReturn("Case File");

        // when
        replayAll();
        String title = notificationFormatter.replaceFormatPlaceholders(inputTitle, null, parentType, null);

        // then
        verifyAll();
        assertEquals(expectedTitle, title);

    }
}
