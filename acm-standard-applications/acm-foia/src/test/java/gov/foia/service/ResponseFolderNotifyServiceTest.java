package gov.foia.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.compressfolder.FolderCompressor;
import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import gov.foia.model.FOIARequest;

/**
 * Created by teng.wang on 3/31/2017.
 */
public class ResponseFolderNotifyServiceTest extends EasyMockSupport

{
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

        responseFolderNotifyService.setCaseFileDao(mockedCaseFileDao);
        responseFolderNotifyService.setUserDao(mockedUserDao);
        responseFolderNotifyService.setResponseFolderService(mockedResponseFolderService);
        responseFolderNotifyService.setCompressor(mockedCompressor);
        responseFolderNotifyService.setNotificationSender(mockedNotificationSender);
        responseFolderNotifyService.setAcmAppConfiguration(mockedAcmApplication);

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
        expect(mockedAcmApplication.getBaseUrl()).andReturn(baseUrl);

        Capture<Notification> captureReleaseNotifier = Capture.newInstance();
        expect(mockedNotificationSender.send(capture(captureReleaseNotifier))).andReturn(new Notification());

        replayAll();

        responseFolderNotifyService.sendEmailNotification(requestId);

        verifyAll();
        assertEquals(String.format("%s %s", "FOIA Request Complete", caseNumber), captureReleaseNotifier.getValue().getTitle());
        assertEquals(emailAddress, captureReleaseNotifier.getValue().getUserEmail());
    }
}
