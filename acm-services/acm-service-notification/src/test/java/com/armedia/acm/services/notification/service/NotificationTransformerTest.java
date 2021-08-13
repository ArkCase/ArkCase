package com.armedia.acm.services.notification.service;

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATE_LCS;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.TITLE_PARSEABLE;
import static org.easymock.EasyMock.anyLong;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class NotificationTransformerTest extends EasyMockSupport
{

    private NotificationToSolrTransformer cut;
    private NotificationUtils mockNotificationsUtils;
    private UserDao mockUserDao;
    private AcmUser creator;
    private AcmUser modifier;

    @Before
    public void setUp() throws Exception
    {
        mockUserDao = createMock(UserDao.class);
        mockNotificationsUtils = createMock(NotificationUtils.class);
        cut = new NotificationToSolrTransformer();
        cut.setUserDao(mockUserDao);
        cut.setNotificationUtils(mockNotificationsUtils);

        creator = new AcmUser();
        setupCreator(creator);

        modifier = new AcmUser();
        setupModifier(modifier);
    }

    @Test
    public void createSolrAdvancedSearchDocument()
    {
        Notification in = createMockNotification();

        expect(mockUserDao.quietFindByUserId("vladimir.cherepnalkovski@armedia.com")).andReturn(creator);
        expect(mockUserDao.quietFindByUserId("NOTIFICATION-BATCH-INSERT")).andReturn(modifier);

        expect(mockNotificationsUtils.buildNotificationLink(anyString(), anyLong(), anyString(), anyLong())).andReturn(null).anyTimes();

        replayAll();

        SolrAdvancedSearchDocument result = cut.toSolrAdvancedSearch(in);

        assertEquals(in.getState(), result.getAdditionalProperties().get(STATE_LCS));
        assertEquals(in.getTitle(), result.getAdditionalProperties().get(TITLE_PARSEABLE));
        assertEquals(in.getCreator(), result.getCreator_lcs());
        assertEquals(in.getModifier(), result.getModifier_lcs());
    }

    private Notification createMockNotification()
    {
        Notification notification = new Notification();
        notification.setId(118L);
        notification.setCreator("vladimir.cherepnalkovski@armedia.com");
        notification.setCreated(new Date());
        notification.setTitle("Web Portal Registration");
        notification.setNote(
                "https://dev.foia.arkcase.com/foia/portal/login/register/f5d74344-c4b0-4ecf-84bd-f58bd4c9bb6a/vladimir.cherepnalkovski@armedia.com");
        notification.setModified(new Date());
        notification.setModifier("NOTIFICATION-BATCH-INSERT");
        notification.setUser("241.arkcase-admin@appdev.armedia.com");
        notification.setState("SENT");
        notification.setParentType("USER");
        notification.setTemplateModelName("portalRequestRegistrationLink");
        notification.setAttachFiles(false);
        notification.setEmailAddresses("vladimir.cherepnalkovski@armedia.com");
        notification.setNotificationType("AUTOMATED");
        notification.setFiles(new ArrayList<>());

        return notification;
    }

    private void setupModifier(AcmUser modifier)
    {
        modifier.setUserId("NOTIFICATION-BATCH-INSERT");
        modifier.setFirstName("SERVICE");
        modifier.setLastName("SERVICE");
    }

    private void setupCreator(AcmUser creator)
    {
        creator.setUserId("vladimir.cherepnalkovski@armedia.com");
        creator.setFirstName("Vladimir");
        creator.setLastName("Cherepnalkovski");
    }
}
