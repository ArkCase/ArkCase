package com.armedia.acm.plugins.ecm.service.impl;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.xml.bind.DatatypeConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

public class EmailAttachmentExtractorComponentTest
{
    private EmailAttachmentExtractorComponent emailAttachmentExtractorComponent;

    private File exampleWithAttachmentMsg;

    private static String toHex(byte[] bytes)
    {
        return DatatypeConverter.printHexBinary(bytes);
    }

    @Before
    public void setUp()
    {
        this.emailAttachmentExtractorComponent = new EmailAttachmentExtractorComponent();
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("email/msg/example.msg");
        exampleWithAttachmentMsg = new File(Objects.requireNonNull(resource).getFile());
    }

    @Test
    public void shouldExtractAttachmentFromMsgFile() throws IOException, ChunkNotFoundException {
        EmailAttachmentExtractorComponent.EmailContent emailContent = emailAttachmentExtractorComponent
                .extractFromMsg(new MockMultipartFile(
                        exampleWithAttachmentMsg.getName(),
                        new FileInputStream(exampleWithAttachmentMsg)));

        assertEquals(1, emailContent.getEmailAttachments().size());
        EmailAttachmentExtractorComponent.EmailAttachment emailAttachment = emailContent.getEmailAttachments().get(0);

        String expectedFileName = "Practice+Worksheet+Present+Simple+vs+Present+Continuous.pdf";
        assertEquals(expectedFileName, emailAttachment.getName());

        String expectedContentType = new MimetypesFileTypeMap().getContentType(expectedFileName);
        assertEquals(expectedContentType, emailAttachment.getContentType());

        File extractedEmbeddedAttachment = File.createTempFile("email", "attachment");
        FileUtils.copyInputStreamToFile(emailAttachment.getInputStream(), extractedEmbeddedAttachment);

        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("email/msg/Practice+Worksheet+Present+Simple+vs+Present+Continuous.pdf");
        File expectedEmbeddedAttachmentFile = new File(Objects.requireNonNull(resource).getFile());

        assertEquals(toHex(checksum(expectedEmbeddedAttachmentFile)), toHex(checksum(extractedEmbeddedAttachment)));
    }

    @Test
    public void shouldExtractAttachmentsFromEmlFile() throws Exception
    {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("email/eml/example.eml");
        File emlFile = new File(Objects.requireNonNull(resource).getFile());

        EmailAttachmentExtractorComponent.EmailContent emailContent = emailAttachmentExtractorComponent
                .extractFromEml(new MockMultipartFile(
                        emlFile.getName(),
                        new FileInputStream(emlFile)));

        assertEquals(1, emailContent.getEmailAttachments().size());
        EmailAttachmentExtractorComponent.EmailAttachment emailAttachment = emailContent.getEmailAttachments().get(0);

        String expectedFileName = "ENVIRONMENTS.docx";
        assertEquals(expectedFileName, emailAttachment.getName());

        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", emailAttachment.getContentType());

        File extractedEmbeddedAttachment = File.createTempFile("email", "attachment");
        FileUtils.copyInputStreamToFile(emailAttachment.getInputStream(), extractedEmbeddedAttachment);

        URL embededAttachmentResource = classLoader.getResource("email/eml/ENVIRONMENTS.docx");
        File expectedEmbeddedAttachmentFile = new File(Objects.requireNonNull(embededAttachmentResource).getFile());

        assertEquals(toHex(checksum(expectedEmbeddedAttachmentFile)), toHex(checksum(extractedEmbeddedAttachment)));
    }

    private byte[] checksum(File input)
    {
        try (InputStream in = new FileInputStream(input))
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] block = new byte[4096];
            int length;
            while ((length = in.read(block)) > 0)
            {
                digest.update(block, 0, length);
            }
            return digest.digest();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}