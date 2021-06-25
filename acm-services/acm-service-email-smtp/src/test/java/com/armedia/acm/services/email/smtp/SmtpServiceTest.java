package com.armedia.acm.services.email.smtp;

/*-
 * #%L
 * ACM Service: Email SMTP
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.service.AcmEmailContentGeneratorService;
import com.armedia.acm.services.users.model.AcmUser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 5, 2017
 */
@RunWith(MockitoJUnitRunner.class)
public class SmtpServiceTest
{

    @InjectMocks
    private SmtpService service;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private AcmEmailContentGeneratorService mockAcmEmailContentGeneratorService;

    @Mock
    private AcmUser mockAcmUser;

    @Mock
    private EcmFileService mockEcmFileService;

    @Mock
    private InputStream mockInputStream;

    @Mock
    private EcmFile mockEcmFile;

    @Mock
    private ApplicationEventPublisher mockApplicationEventPublisher;

    @Mock
    private AcmMailSender mockMailSender;

    private EmailSenderConfig senderConfig;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        senderConfig = new EmailSenderConfig();
        senderConfig.setHost("host_value");
        senderConfig.setPort(0);
        senderConfig.setUsername("email_user_value");
        senderConfig.setPassword("email_password_value");
        senderConfig.setUserFrom("email_from_value");
        senderConfig.setConvertDocumentsToPdf(false);
        service.setEmailSenderConfig(senderConfig);
    }

    @Test
    public void testSendEmailWithEmbeddedLinks() throws Exception
    {
        // given
        final String email = "user_email@test.com";
        final String ccEmail = "cc_user_email@test.com";
        final String bccEmail = "bcc_user_email@test.com";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = header + "\\s*" + "<br/>" + baseUrl + fileId + "&acm_email_ticket=" + token + "<br/>" + "\\s*" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<String> ccAddresses = new ArrayList<>();
        ccAddresses.add(ccEmail);
        List<String> bccAddresses = new ArrayList<>();
        bccAddresses.add(bccEmail);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        EmailWithEmbeddedLinksDTO inputDTO = new EmailWithEmbeddedLinksDTO();
        inputDTO.setTitle(title);
        inputDTO.setHeader(header);
        inputDTO.setEmailAddresses(addresses);
        inputDTO.setBaseUrl(baseUrl);
        inputDTO.setFileIds(fileIds);
        inputDTO.setFooter(footer);
        inputDTO.setModelReferenceName("someEmail");
        inputDTO.setSubject("subject");
        inputDTO.setCcEmailAddresses(ccAddresses);
        inputDTO.setBccEmailAddresses(bccAddresses);

        senderConfig.setEncryption("off");

        when(mockAcmEmailContentGeneratorService.generateEmailBody(inputDTO, email, mockAuthentication)).thenReturn(note);

        // when
        List<EmailWithEmbeddedLinksResultDTO> results = service.sendEmailWithEmbeddedLinks(inputDTO, mockAuthentication, mockAcmUser);

        // then
        assertThat(results.size(), is(1));

        EmailWithEmbeddedLinksResultDTO resultDTO = results.get(0);
        assertThat(resultDTO.isState(), is(true));
        assertThat(resultDTO.getEmailAddress(), is(email));
        verify(mockMailSender, times(1)).sendEmail(eq(email), anyString(), eq(note), eq(null), eq(null), eq(ccEmail), eq(bccEmail));

    }

    @Test
    public void testSendEmailWithAttachments() throws Exception
    {
        // given
        final String email = "user_email@test.com";
        final String header = "header";
        final String body = "body";
        final String footer = "footer";
        final String note = header + body + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        EmailWithAttachmentsDTO inputDTO = new EmailWithAttachmentsDTO();
        inputDTO.setEmailAddresses(addresses);
        inputDTO.setHeader(header);
        inputDTO.setBody(note);
        inputDTO.setFooter(footer);
        inputDTO.setSubject("subject");

        senderConfig.setEncryption("off");

        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(999L);
        inputDTO.setAttachmentIds(attachmentIds);

        List<String> filePaths = new ArrayList<>();
        Resource resource = new ClassPathResource("temp.zip");
        filePaths.add(resource.getFile().getAbsolutePath());
        inputDTO.setFilePaths(filePaths);

        ArgumentCaptor<String> capturedNote = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ArrayList<InputStreamDataSource>> capturedAttachments = ArgumentCaptor.forClass((Class) ArrayList.class);

        ArgumentCaptor<byte[]> read = ArgumentCaptor.forClass(byte[].class);
        when(mockEcmFileService.downloadAsInputStream(attachmentIds.get(0), null)).thenReturn(mockInputStream);
        when(mockEcmFileService.findById(attachmentIds.get(0))).thenReturn(mockEcmFile);
        when(mockInputStream.read(read.capture(), eq(0), eq(16384))).thenReturn(-1);

        when(mockEcmFile.getFileName()).thenReturn("fileName");
        when(mockEcmFile.getFileActiveVersionNameExtension()).thenReturn(".extension");

        mockInputStream.close();

        when(mockAcmUser.getUserId()).thenReturn("ann-acm");
        when(mockEcmFile.getParentObjectId()).thenReturn(103L);
        when(mockEcmFile.getParentObjectType()).thenReturn("COMPLAINT");

        // expected calls to raise the file emailed event on the file itself - AFDP-3029
        when(mockEcmFile.getId()).thenReturn(attachmentIds.get(0));
        when(mockEcmFile.getObjectType()).thenReturn(EcmFileConstants.OBJECT_FILE_TYPE);

        // when
        service.sendEmailWithAttachments(inputDTO, mockAuthentication, mockAcmUser);

        // then
        verify(mockMailSender).sendMultipartEmail(eq(email), eq(null), anyString(), capturedNote.capture(),
                capturedAttachments.capture(), eq(null), eq(null));
        assertThat(note.equals(capturedNote.getValue()), is(true));
        assertThat(capturedAttachments.getValue(), notNullValue());
        assertThat(capturedAttachments.getValue().size(), is(2));
        assertThat(capturedAttachments.getValue().get(0).getName(), notNullValue());
        assertThat(capturedAttachments.getValue().get(0).getContentType(), notNullValue());
        assertThat(capturedAttachments.getValue().get(1).getName(), notNullValue());
        assertThat(capturedAttachments.getValue().get(1).getContentType(), notNullValue());
        mockApplicationEventPublisher.publishEvent(any(SmtpEventSentEvent.class));
    }

    @Test
    public void testSendEmailWithEmbeddedLinksAndAttachments() throws Exception
    {
        // given
        final String email = "user_email@test.com";
        final String header = "header";
        final String baseUrl = "base_url";
        final String title = "title";
        final String footer = "footer";
        final long fileId = 1234;
        final String token = "token";
        final String note = header + "\\s*" + "<br/>" + baseUrl + fileId + "&acm_email_ticket=" + token + "<br/>" + "\\s*" + footer;

        List<String> addresses = new ArrayList<>();
        addresses.add(email);
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(fileId);
        EmailWithAttachmentsAndLinksDTO inputDTO = new EmailWithAttachmentsAndLinksDTO();
        inputDTO.setTitle(title);
        inputDTO.setHeader(header);
        inputDTO.setEmailAddresses(addresses);
        inputDTO.setBaseUrl(baseUrl);
        inputDTO.setFileIds(fileIds);
        inputDTO.setFooter(footer);
        List<Long> attachmentIds = new ArrayList<>();
        attachmentIds.add(999L);
        inputDTO.setAttachmentIds(attachmentIds);

        List<String> filePaths = new ArrayList<>();
        Resource resource = new ClassPathResource("temp.zip");
        filePaths.add(resource.getFile().getAbsolutePath());
        inputDTO.setFilePaths(filePaths);

        senderConfig.setEncryption("off");

        ArgumentCaptor<String> capturedNote = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ArrayList<InputStreamDataSource>> capturedAttachments = ArgumentCaptor.forClass((Class) ArrayList.class);

        when(mockAcmEmailContentGeneratorService.generateEmailBody(inputDTO, email, mockAuthentication)).thenReturn(note);

        ArgumentCaptor<byte[]> read = ArgumentCaptor.forClass(byte[].class);
        when(mockEcmFileService.downloadAsInputStream(attachmentIds.get(0), null)).thenReturn(mockInputStream);
        when(mockEcmFileService.findById(attachmentIds.get(0))).thenReturn(mockEcmFile);
        when(mockEcmFileService.findById(fileId)).thenReturn(mockEcmFile);
        when(mockInputStream.read(read.capture(), eq(0), eq(16384))).thenReturn(-1);

        when(mockEcmFile.getFileName()).thenReturn("fileName");
        when(mockEcmFile.getFileActiveVersionNameExtension()).thenReturn(".extension");

        mockInputStream.close();

        when(mockAcmUser.getUserId()).thenReturn("ann-acm");
        when(mockEcmFile.getParentObjectId()).thenReturn(103L);
        when(mockEcmFile.getParentObjectType()).thenReturn("COMPLAINT");

        // expected calls to raise the file emailed event on the file itself - AFDP-3029
        when(mockEcmFile.getId()).thenReturn(attachmentIds.get(0));
        when(mockEcmFile.getObjectType()).thenReturn(EcmFileConstants.OBJECT_FILE_TYPE);

        // when
        service.sendEmailWithAttachmentsAndLinks(inputDTO, mockAuthentication, mockAcmUser);

        // then
        verify(mockMailSender).sendMultipartEmail(eq(email), eq(null), capturedNote.capture(), capturedAttachments.capture(),
                eq(null), eq(null));
        assertEquals(note, capturedNote.getValue());
        assertThat(capturedAttachments.getValue(), notNullValue());
        assertThat(capturedAttachments.getValue().size(), is(2));
        assertThat(capturedAttachments.getValue().get(0).getName(), notNullValue());
        assertThat(capturedAttachments.getValue().get(0).getContentType(), notNullValue());
        assertThat(capturedAttachments.getValue().get(1).getName(), notNullValue());
        assertThat(capturedAttachments.getValue().get(1).getContentType(), notNullValue());
        mockApplicationEventPublisher.publishEvent(any(SmtpEventSentEvent.class));
    }

    @Test
    public void testValidEmail()
    {
        List<String> validEmails = Arrays.asList("ann-acm@arkcase.org", "ann-acm@appdev.arkcase.org", "ann-acm@yahoo.com",
                "ann-acm@gmail.com", "ann-acm@1.com", "ann-acm@test.com", "ann-acm@test.net", "me@me.mk");
        validEmails.forEach(it -> assertTrue(service.isEmailValid(it)));
    }

    @Test
    public void testInvalidEmail()
    {
        List<String> validEmails = Arrays.asList("ann-acm@.arkcase.org", "ann-acm@.com", "ann-acm@arkcase", "ann-acm@.com.com",
                "ann-acm@%*.com", "ann-acm@com@com");
        validEmails.forEach(it -> assertFalse(service.isEmailValid(it)));
    }

}
