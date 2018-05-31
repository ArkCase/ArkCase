package com.armedia.acm.services.email.service;

/*-
 * #%L
 * ACM Service: Email
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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.newCapture;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.easymock.Capture;
import org.easymock.EasyMock;
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

        expect(multipartMock.getBodyPart(0)).andAnswer(() -> {
            BodyPart bodyPart = new MimeBodyPart();
            return bodyPart;
        });

        ByteArrayInputStream bais = new ByteArrayInputStream("asdasd".getBytes());
        expect(multipartMock.getBodyPart(1)).andAnswer(() -> {

            BodyPart bodyPart = new MimeBodyPart(bais);
            bodyPart.setFileName("just bytes");
            return bodyPart;
        });

        expect(acmFolderServiceMock.getRootFolder(1L, "CASE_FILE")).andAnswer(() -> {
            AcmFolder folder = new AcmFolder();
            folder.setCmisFolderId("cmis_folder_id");
            folder.setId(1L);
            return folder;
        });

        Capture<InputStream> isCapture = newCapture();
        expect(ecmFileServiceMock.upload(
                EasyMock.anyString(), eq("mail"), eq("Document"),
                capture(isCapture), eq("message/rfc822"),
                EasyMock.anyString(), eq(authMock),
                eq("cmis_folder_id"), eq("CASE_FILE"), eq(1L))).andAnswer(() -> null);

        expect(ecmFileServiceMock.upload(EasyMock.anyString(), eq("attachment"), eq("Document"),
                capture(isCapture), eq("message/rfc822"),
                EasyMock.anyString(), eq(authMock),
                eq("cmis_folder_id"), eq("CASE_FILE"), eq(1L))).andAnswer(() -> null);

        Capture<OutputStream> osCapture = newCapture();
        messageMock.writeTo(capture(osCapture));
        expectLastCall();

        replayAll();

        acmProcessMailService.extractAttachmentsAndUpload(messageMock, 1L, "CASE_FILE", null, authMock);

    }

}
