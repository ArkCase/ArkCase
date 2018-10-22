/**
 *
 */
package gov.foia.service;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.pdf.service.PdfServiceImpl;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;
import gov.foia.model.FOIARequest;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 17, 2016
 */

public class AcknowledgementDocumentServiceTest extends EasyMockSupport
{

    static final long requestId = 101l;
    static final String requestType = "New Request";
    static final long folderId = 222l;
    static final long containerId = 333l;
    static final Long fileId = 111L;
    static final String documentType = "request_acknowledgement_document";
    static final String emailType = "email";
    static final String emailAddress = "teng.wang@armedia.com";
    static final String caseNumber = "20170425-101";
    static final String userId = "***REMOVED***";
    private PdfService pdfService;
    private File acknowledgementDocumentStylesheet;
    private File requestDocumentStylesheet;
    private AcknowledgementDocumentService acknowledgementService;
    private FOIARequestDao mockedFOIARequestFileDao;
    private UserDao mockedUserDao;
    private FOIARequest mockedRequest;
    private List<ContactMethod> mockedContactMethods;
    private ContactMethod mockedContactMethod;
    private Person mockedPerson;
    private PersonAssociation mockedPersonAssociation;
    private AcmContainer mockedContainer;
    private EcmFile mockedFile;
    private EcmFileDao mockedFileDao;
    private AcmFolder mockedFolder;
    private NotificationSender mockedNotificationSender;
    private FOIADocumentGeneratorService mockDocumentGeneratorService;

    @Before
    public void setUp()
    {
        pdfService = new PdfServiceImpl();

        ClassLoader classLoader = getClass().getClassLoader();
        acknowledgementDocumentStylesheet = new File(classLoader.getResource("request-acknowledgement-document.xsl").getFile());
        requestDocumentStylesheet = new File(classLoader.getResource("request-document.xsl").getFile());
        // System.out.println(acknowledgementDocumentStylesheet);

        acknowledgementService = new AcknowledgementDocumentService();
        mockedRequest = createMock(FOIARequest.class);
        mockedFOIARequestFileDao = createMock(FOIARequestDao.class);
        mockedUserDao = createMock(UserDao.class);
        mockedPerson = createMock(Person.class);
        mockedContactMethod = createMock(ContactMethod.class);
        mockedPersonAssociation = createMock(PersonAssociation.class);
        mockedFile = createMock(EcmFile.class);
        mockedFileDao = createMock(EcmFileDao.class);
        mockedFolder = createMock(AcmFolder.class);
        mockedContainer = createMock(AcmContainer.class);
        mockedNotificationSender = createMock(NotificationSender.class);
        mockDocumentGeneratorService = createMock(FOIADocumentGeneratorService.class);

        acknowledgementService.setRequestDao(mockedFOIARequestFileDao);
        acknowledgementService.setEcmFileDao(mockedFileDao);
        acknowledgementService.setUserDao(mockedUserDao);
        acknowledgementService.setNotificationSender(mockedNotificationSender);
        acknowledgementService.setDocumentGeneratorService(mockDocumentGeneratorService);

        mockedContactMethods = Arrays.asList(mockedContactMethod);
    }

    @Ignore
    @Test
    public void testAcknowledgement() throws Exception
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("requestID", "101");
        parameters.put("topic", "topic"); // where do we extract the topic from?
        parameters.put("requestorName", "Lazo Lazarev");
        parameters.put("requestorAddress", "Skopje");
        parameters.put("requestorEmailAddress", "lazo.lazarev@armedia.com");
        parameters.put("requestorOrganization", "Armedia");
        parameters.put("requestorOrganizationAddress", "USA");
        parameters.put("receivedDate", "10-12-2016");
        parameters.put("currentDate", "10-12-2016");

        String filePath = pdfService.generatePdf(acknowledgementDocumentStylesheet, parameters);
        System.out.println(filePath);
    }

    @Ignore
    @Test
    public void testRequest() throws Exception
    {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("requestID", "101");
        parameters.put("topic", "topic"); // where do we extract the topic from?
        parameters.put("requestorSource", "Drupal");
        parameters.put("requestorName", "Lazo Lazarev");
        parameters.put("requestorAddress", "Skopje");
        parameters.put("requestorEmailAddress", "lazo.lazarev@armedia.com");
        parameters.put("requestorOrganization", "Armedia");
        parameters.put("requestorOrganizationAddress", "USA");
        parameters.put("receivedDate", "10-12-2016");
        parameters.put("receivedDate", "10-12-2016");
        parameters.put("requestType", "Type");
        parameters.put("requestSubType", "SubType");
        parameters.put("category", "Category");
        parameters.put("description", "Description");
        parameters.put("currentDate", "10-12-2016");

        String filePath = pdfService.generatePdf(requestDocumentStylesheet, parameters);
        System.out.println(filePath);
    }

    @Test
    public void testAcknowledgementEmailExternal() throws Exception
    {
        testEmailExpect();
        expect(mockedRequest.getCreator()).andReturn(userId);
        expect(mockedRequest.isExternal()).andReturn(true);

        Capture<EmailWithAttachmentsDTO> captureEmailWithAttachmentsDTO = Capture.newInstance();
        Capture<Authentication> capturedAuthentication = Capture.newInstance();
        Capture<String> capturedUserId = Capture.newInstance();

        mockedNotificationSender.sendEmailWithAttachments(capture(captureEmailWithAttachmentsDTO), capture(capturedAuthentication),
                capture(capturedUserId));

        replayAll();

        acknowledgementService.emailAcknowledgement(requestId);

        verifyAll();
        assertEquals(String.format("%s %s", "New Request", caseNumber), captureEmailWithAttachmentsDTO.getValue().getSubject());
        assertEquals(emailAddress, captureEmailWithAttachmentsDTO.getValue().getEmailAddresses().get(0));
        assertEquals(fileId, captureEmailWithAttachmentsDTO.getValue().getAttachmentIds().get(0));
        assertEquals(userId, capturedUserId.getValue());
    }

    @Test
    public void testAcknowledgementEmailInternal() throws Exception
    {
        testEmailExpect();
        expect(mockedRequest.getCreator()).andReturn(userId);
        expect(mockedRequest.isExternal()).andReturn(false);
        AcmUser internalUser = new AcmUser();
        internalUser.setUserId(userId);
        expect(mockedUserDao.findByUserId(userId)).andReturn(internalUser);

        Capture<EmailWithAttachmentsDTO> capturedEmailWithAttachmentsDTO = Capture.newInstance();
        Capture<Authentication> capturedAuthentication = Capture.newInstance();
        Capture<AcmUser> capturedAcmUser = Capture.newInstance();

        mockedNotificationSender.sendEmailWithAttachments(capture(capturedEmailWithAttachmentsDTO), capture(capturedAuthentication),
                capture(capturedAcmUser));

        replayAll();

        acknowledgementService.emailAcknowledgement(requestId);

        verifyAll();
        assertEquals(String.format("%s %s", "New Request", caseNumber), capturedEmailWithAttachmentsDTO.getValue().getSubject());
        assertEquals(emailAddress, capturedEmailWithAttachmentsDTO.getValue().getEmailAddresses().get(0));
        assertEquals(fileId, capturedEmailWithAttachmentsDTO.getValue().getAttachmentIds().get(0));
        assertEquals(userId, capturedAcmUser.getValue().getUserId());
    }

    public void testEmailExpect()
    {
        expect(mockedFOIARequestFileDao.find(requestId)).andReturn(mockedRequest);
        expect(mockedRequest.getOriginator()).andReturn(mockedPersonAssociation);
        expect(mockedPersonAssociation.getPerson()).andReturn(mockedPerson);
        expect(mockedPerson.getContactMethods()).andReturn(mockedContactMethods);
        expect(mockedContactMethod.getType()).andReturn(emailType);
        expect(mockedContactMethod.getValue()).andReturn(emailAddress);
        expect(mockedRequest.getCaseNumber()).andReturn(caseNumber);
        expect(mockedRequest.getContainer()).andReturn(mockedContainer).anyTimes();
        expect(mockedContainer.getId()).andReturn(containerId).anyTimes();
        expect(mockedContainer.getAttachmentFolder()).andReturn(mockedFolder);
        expect(mockedFolder.getId()).andReturn(folderId).anyTimes();
        expect(mockedRequest.getRequestType()).andReturn(requestType).anyTimes();
        expect(mockedFile.getFileId()).andReturn(fileId);
        expect(mockedFileDao.findForContainerAttachmentFolderAndFileType(containerId, folderId, documentType)).andReturn(mockedFile);

        FOIADocumentDescriptor documentDescriptor = new FOIADocumentDescriptor();
        documentDescriptor.setDoctype(documentType);
        expect(mockDocumentGeneratorService.getDocumentDescriptor(anyObject(FOIAObject.class), anyObject(String.class)))
                .andReturn(documentDescriptor);
    }

}
