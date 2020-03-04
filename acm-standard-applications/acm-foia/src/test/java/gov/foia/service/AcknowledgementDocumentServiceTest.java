/**
 *
 */
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
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.NotificationSender;
import com.armedia.acm.services.users.dao.UserDao;

import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIADocumentDescriptor;
import gov.foia.model.FOIAObject;
import gov.foia.model.FOIARequest;
import gov.foia.model.FoiaConfiguration;

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
    static final String userId = "ann-acm@armedia.com";
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
    private FoiaConfigurationService mockedFoiaConfigurationService;
    private FoiaConfiguration mockedFoiaConfiguration;
    private Notification mockedNotification;
    private EcmFileVersion mockedEcmFileVersion;
    private NotificationDao mockedNotificationDao;

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
        mockedFoiaConfigurationService = createMock(FoiaConfigurationService.class);
        mockedFoiaConfiguration = createMock(FoiaConfiguration.class);
        mockedNotification = createMock(Notification.class);
        mockedEcmFileVersion = createMock(EcmFileVersion.class);
        mockedNotificationDao = createMock(NotificationDao.class);

        acknowledgementService.setRequestDao(mockedFOIARequestFileDao);
        acknowledgementService.setEcmFileDao(mockedFileDao);
        acknowledgementService.setUserDao(mockedUserDao);
        acknowledgementService.setNotificationSender(mockedNotificationSender);
        acknowledgementService.setDocumentGeneratorService(mockDocumentGeneratorService);
        acknowledgementService.setFoiaConfigurationService(mockedFoiaConfigurationService);
        acknowledgementService.setNotificationDao(mockedNotificationDao);

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
    public void testAcknowledgementEmail() throws Exception
    {
        expect(mockedFoiaConfiguration.getReceivedDateEnabled()).andReturn(false);
        expect(mockedFoiaConfigurationService.readConfiguration()).andReturn(mockedFoiaConfiguration);
        expect(mockedFOIARequestFileDao.find(requestId)).andReturn(mockedRequest);
        expect(mockedRequest.getPreviousQueue()).andReturn(null);
        expect(mockedRequest.getOriginator()).andReturn(mockedPersonAssociation);
        expect(mockedRequest.getCreator()).andReturn(userId);
        expect(mockedRequest.getObjectType()).andReturn("CASE_FILE");
        expect(mockedPersonAssociation.getPerson()).andReturn(mockedPerson);
        expect(mockedPerson.getContactMethods()).andReturn(mockedContactMethods);
        expect(mockedContactMethod.getType()).andReturn(emailType);
        expect(mockedContactMethod.getValue()).andReturn(emailAddress);
        FOIADocumentDescriptor documentDescriptor = new FOIADocumentDescriptor();
        documentDescriptor.setDoctype(documentType);
        expect(mockDocumentGeneratorService.getDocumentDescriptor(anyObject(FOIAObject.class), anyObject(String.class)))
                .andReturn(documentDescriptor);
        expect(mockedContainer.getId()).andReturn(containerId).anyTimes();
        expect(mockedContainer.getAttachmentFolder()).andReturn(mockedFolder);
        expect(mockedFolder.getId()).andReturn(folderId).anyTimes();
        expect(mockedFileDao.findForContainerAttachmentFolderAndFileType(containerId, folderId, documentType)).andReturn(mockedFile);
        expect(mockedFile.getVersions()).andReturn(Arrays.asList(mockedEcmFileVersion));
        expect(mockedEcmFileVersion.getVersionTag()).andReturn("versionTag");
        expect(mockedFile.getActiveVersionTag()).andReturn("versionTag");
        expect(mockedRequest.getContainer()).andReturn(mockedContainer).anyTimes();
        expect(mockedRequest.getCaseNumber()).andReturn(caseNumber);
        expect(mockedRequest.getRequestType()).andReturn(requestType).anyTimes();
        Capture<Notification> captureNotification = Capture.newInstance();
        expect(mockedNotificationDao.save(capture(captureNotification))).andReturn(mockedNotification);

        replayAll();

        acknowledgementService.emailAcknowledgement(requestId);

        verifyAll();
        assertEquals(emailAddress, captureNotification.getValue().getEmailAddresses());
        assertEquals(String.format("%s %s", "New Request", caseNumber), captureNotification.getValue().getTitle());
    }
}
