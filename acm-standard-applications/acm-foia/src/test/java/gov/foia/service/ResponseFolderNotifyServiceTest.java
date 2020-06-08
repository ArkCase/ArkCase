package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;
import gov.foia.model.FOIAConstants;
import gov.foia.model.FOIARequest;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

/**
 * Created by teng.wang on 3/31/2017.
 */
public class ResponseFolderNotifyServiceTest extends EasyMockSupport

{
    static final String objectType = "CASE_FILE";
    static final long requestId = 101l;
    static final String emailType = "email";
    static final String emailAddress = "teng.wang@armedia.com";
    static final String caseNumber = "20170331-101";
    static final String userId = "***REMOVED***";
    static final String filePath = "C:\\apache-tomcat-8.0.36\\temp\\temp.zip";
    static final String requestType = "New Request";
    static final String baseUrl = "https://acm-arkcase/arkcase";
    private ResponseFolderNotifyService responseFolderNotifyService;
    private CaseFileDao mockedCaseFileDao;
    private UserDao mockedUserDao;
    private FOIARequest mockedRequest;
    private List<ContactMethod> mockedContactMethods;
    private ContactMethod mockedContactMethod;
    private Person mockedPerson;
    private PersonAssociation mockedPersonAssociation;
    private NotificationSender mockedNotificationSender;
    private AcmFolder mockedAcmFolder;
    private FolderCompressor mockedCompressor;
    private ResponseFolderService mockedResponseFolderService;
    private AcmApplication mockedAcmApplication;
    private NotificationDao mockNotificationDao;

    @Before
    public void setUp()
    {
        responseFolderNotifyService = new ResponseFolderNotifyService();
        mockedRequest = createMock(FOIARequest.class);
        mockedCaseFileDao = createMock(CaseFileDao.class);
        mockedUserDao = createMock(UserDao.class);
        mockedPerson = createMock(Person.class);
        mockedContactMethod = createMock(ContactMethod.class);
        mockedPersonAssociation = createMock(PersonAssociation.class);
        mockedNotificationSender = createMock(NotificationSender.class);
        mockedAcmFolder = createMock(AcmFolder.class);
        mockedCompressor = createMock(FolderCompressor.class);
        mockedResponseFolderService = createMock(ResponseFolderService.class);
        mockedAcmApplication = createMock(AcmApplication.class);
        mockNotificationDao = createMock(NotificationDao.class);
        
        responseFolderNotifyService.setCaseFileDao(mockedCaseFileDao);
        responseFolderNotifyService.setUserDao(mockedUserDao);
        responseFolderNotifyService.setResponseFolderService(mockedResponseFolderService);
        responseFolderNotifyService.setCompressor(mockedCompressor);
        responseFolderNotifyService.setNotificationSender(mockedNotificationSender);
        responseFolderNotifyService.setAcmAppConfiguration(mockedAcmApplication);
        responseFolderNotifyService.setNotificationDao(mockNotificationDao);

        mockedContactMethods = Arrays.asList(mockedContactMethod);
    }

    @Test
    public void testSendEmailNotification()
    {
        expect(mockedCaseFileDao.find(requestId)).andReturn(mockedRequest);

        expect(mockedRequest.getOriginator()).andReturn(mockedPersonAssociation);
        expect(mockedPersonAssociation.getPerson()).andReturn(mockedPerson);
        expect(mockedPerson.getContactMethods()).andReturn(mockedContactMethods);
        expect(mockedContactMethod.getType()).andReturn(emailType);
        expect(mockedContactMethod.getValue()).andReturn(emailAddress);

        expect(mockedRequest.getRequestType()).andReturn(requestType).anyTimes();
        expect(mockedRequest.getCaseNumber()).andReturn(caseNumber).anyTimes();
        expect(mockedRequest.getObjectType()).andReturn(objectType).anyTimes();
        expect(mockedRequest.getId()).andReturn(requestId).anyTimes();

        
        Capture<Notification> captureReleaseNotifier = Capture.newInstance();
        expect(mockNotificationDao.save(capture(captureReleaseNotifier))).andReturn(new Notification());

        replayAll();

        responseFolderNotifyService.sendEmailNotification(requestId);

        verifyAll();
        assertEquals(String.format("%s %s", FOIAConstants.EMAIL_RELEASE_SUBJECT, caseNumber),
                captureReleaseNotifier.getValue().getTitle());
        assertEquals(emailAddress, captureReleaseNotifier.getValue().getEmailAddresses());
    }

    @Test
    public void testSendEmailResponseCompressNotification()
    {
        expect(mockedCaseFileDao.find(requestId)).andReturn(mockedRequest);

        expect(mockedRequest.getOriginator()).andReturn(mockedPersonAssociation);
        expect(mockedPersonAssociation.getPerson()).andReturn(mockedPerson);
        expect(mockedPerson.getContactMethods()).andReturn(mockedContactMethods);
        expect(mockedContactMethod.getType()).andReturn(emailType);
        expect(mockedContactMethod.getValue()).andReturn(emailAddress);

        expect(mockedRequest.getRequestType()).andReturn(requestType).anyTimes();
        expect(mockedRequest.getCaseNumber()).andReturn(caseNumber).anyTimes();
        expect(mockedRequest.getObjectType()).andReturn(objectType).anyTimes();
        expect(mockedRequest.getId()).andReturn(requestId).anyTimes();

        
        Capture<Notification> captureReleaseNotifier = Capture.newInstance();
        expect(mockNotificationDao.save(capture(captureReleaseNotifier))).andReturn(new Notification());
        replayAll();

        responseFolderNotifyService.sendEmailResponseCompressNotification(requestId);

        verifyAll();
        assertEquals(String.format("%s %s", FOIAConstants.EMAIL_RESPONSE_FOLDER_ZIP, caseNumber),
                captureReleaseNotifier.getValue().getTitle());
        assertEquals(emailAddress, captureReleaseNotifier.getValue().getEmailAddresses());
    }
}
