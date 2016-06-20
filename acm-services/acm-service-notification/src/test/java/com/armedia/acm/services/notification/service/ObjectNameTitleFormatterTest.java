package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ObjectNameTitleFormatterTest extends EasyMockSupport
{
    static final String TITLE = String.format("Document deleted in %s", NotificationConstants.NAME_LABEL);
    static final String PARENT_OBJECT_TYPE = "FILE";
    static final Long PARENT_OBJECT_ID = 12L;
    static final String RELATED_OBJECT_TYPE = "COMPLAINT";
    static final Long RELATED_OBJECT_ID = 1L;
    static final String COMPLAINT_TITLE = "COMPLAINT_1";
    private Notification notification;
    private AcmDataService mockDataService;
    private AcmNotificationDao mockNotificationDao;
    private AcmNotifiableEntity mockNotifiableEntity;
    private ObjectNameTitleFormatter titleFormatter;

    @Before
    public void setUp()
    {
        notification = new Notification();
        notification.setTitle(TITLE);
        notification.setNote(null);
        notification.setRelatedObjectType(RELATED_OBJECT_TYPE);
        notification.setRelatedObjectId(RELATED_OBJECT_ID);
        notification.setParentType(PARENT_OBJECT_TYPE);
        notification.setParentId(PARENT_OBJECT_ID);
        mockDataService = createMock(AcmDataService.class);
        mockNotificationDao = createMock(AcmNotificationDao.class);
        mockNotifiableEntity = createMock(AcmNotifiableEntity.class);
        titleFormatter = new ObjectNameTitleFormatter();

        titleFormatter.setAcmDataService(mockDataService);
    }

    @Test
    public void testFormat()
    {
        expect(mockDataService.getNotificationDaoByObjectType(RELATED_OBJECT_TYPE)).andReturn(mockNotificationDao);
        expect(mockNotificationDao.findEntity(RELATED_OBJECT_ID)).andReturn(mockNotifiableEntity);
        expect(mockNotifiableEntity.getNotifiableEntityTitle()).andReturn(COMPLAINT_TITLE);

        replayAll();
        String expectedTitle = titleFormatter.format(notification);

        verifyAll();
        assertEquals(expectedTitle, TITLE.replace(NotificationConstants.NAME_LABEL, COMPLAINT_TITLE));
    }

    @Test
    public void testTitleIsNull()
    {
        notification.setTitle(null);

        replayAll();
        titleFormatter.format(notification);

        verifyAll();
        assertNull(notification.getTitle());
    }

}
