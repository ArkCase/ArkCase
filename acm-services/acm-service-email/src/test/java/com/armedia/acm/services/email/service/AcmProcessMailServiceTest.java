package com.armedia.acm.services.email.service;

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.easymock.Capture;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;

/**
 * Created by nebojsha.davidovikj on 1/20/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-email.xml"
})
public class AcmProcessMailServiceTest extends EasyMockSupport
{
    @Autowired
    private AcmProcessMailService acmProcessMailService;
    private AcmFolderService acmFolderServiceMock = createMock(AcmFolderService.class);
    private EcmFileService ecmFileServiceMock = createMock(EcmFileService.class);


    @Before
    public void setUp() throws Exception
    {
        ((AcmProcessMailServiceImpl) acmProcessMailService).setAcmFolderService(acmFolderServiceMock);
        ((AcmProcessMailServiceImpl) acmProcessMailService).setEcmFileService(ecmFileServiceMock);
    }

    @Test
    public void extractAttachmentsAndUpload() throws Exception
    {
        assertNotNull(acmProcessMailService);
        Message messageMock = createMock(Message.class);
        Multipart multipartMock = createMock(Multipart.class);
        Authentication authMock = createMock(Authentication.class);


        expect(messageMock.getContent()).andAnswer(() -> multipartMock);

        expect(multipartMock.getCount()).andReturn(2).anyTimes();

        expect(multipartMock.getBodyPart(0)).andAnswer(() ->
        {
            BodyPart bodyPart = new MimeBodyPart();
            return bodyPart;
        });

        ByteArrayInputStream bais = new ByteArrayInputStream("asdasd".getBytes());
        expect(multipartMock.getBodyPart(1)).andAnswer(() ->
        {

            BodyPart bodyPart = new MimeBodyPart(bais);
            bodyPart.setFileName("just bytes");
            return bodyPart;
        });

        expect(acmFolderServiceMock.getRootFolder(1L, "CASE_FILE")).andAnswer(() ->
        {
            AcmFolder folder = new AcmFolder();
            folder.setCmisFolderId("cmis_folder_id");
            folder.setId(1L);
            return folder;
        });

        Capture<InputStream> isCapture = newCapture();
        expect(ecmFileServiceMock.upload(
                eq("1_CASE_FILE.eml"), eq("mail"), eq("Document"),
                capture(isCapture), eq("message/rfc822"),
                eq("1_CASE_FILE.eml"), eq(authMock),
                eq("cmis_folder_id"), eq("CASE_FILE"), eq(1L))).andAnswer(() -> null);

        expect(ecmFileServiceMock.upload(eq("just bytes"), eq("attachment"), eq("Document"),
                capture(isCapture), eq("message/rfc822"),
                eq("just bytes"), eq(authMock),
                eq("cmis_folder_id"), eq("CASE_FILE"), eq(1L))).andAnswer(() -> null);

        Capture<OutputStream> osCapture = newCapture();
        messageMock.writeTo(capture(osCapture));
        expectLastCall();

        replayAll();

        acmProcessMailService.extractAttachmentsAndUpload(messageMock, 1L, "CASE_FILE", null, authMock);

    }

}