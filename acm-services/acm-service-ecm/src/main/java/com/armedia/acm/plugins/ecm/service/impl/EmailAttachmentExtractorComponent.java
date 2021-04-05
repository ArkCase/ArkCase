package com.armedia.acm.plugins.ecm.service.impl;

import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class EmailAttachmentExtractorComponent
{

    public List<EmailAttachment> extractFromMsg(MultipartFile attachment) throws IOException
    {
        InputStream inputStream = attachment.getInputStream();
        MAPIMessage message = new MAPIMessage(inputStream);
        AttachmentChunks[] attachmentFiles = message.getAttachmentFiles();
        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (AttachmentChunks attachmentChunks : attachmentFiles)
        {
            InputStream byteArrayInputStream = new ByteArrayInputStream(attachmentChunks.getEmbeddedAttachmentObject());

            String fileName = attachmentChunks.getAttachLongFileName().getValue();
            String contentType = new MimetypesFileTypeMap().getContentType(fileName);
            emailAttachments.add(new EmailAttachment(
                    fileName,
                    byteArrayInputStream,
                    contentType));
        }

        return emailAttachments;
    }

    public List<EmailAttachment> extractFromEml(MultipartFile attachment) throws Exception
    {
        Properties props = new Properties();
        Session mailSession = Session.getDefaultInstance(props, null);
        InputStream inputStream = attachment.getInputStream();
        MimeMessage message = new MimeMessage(mailSession, inputStream);

        MimeMessageParser mimeParser = new MimeMessageParser(message);
        mimeParser.parse();
        List<DataSource> dsl = mimeParser.getAttachmentList();

        List<EmailAttachment> emailAttachments = new ArrayList<>();
        for (DataSource ds : dsl)
        {
            emailAttachments.add(new EmailAttachment(
                    ds.getName(),
                    ds.getInputStream(),
                    ds.getContentType()));
        }
        return emailAttachments;
    }

    public static class EmailAttachment
    {
        private String name;
        private InputStream inputStream;
        private String contentType;

        public EmailAttachment(String name, InputStream inputStream, String contentType)
        {
            this.name = name;
            this.inputStream = inputStream;
            this.contentType = contentType;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public InputStream getInputStream()
        {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream)
        {
            this.inputStream = inputStream;
        }

        public String getContentType()
        {
            return contentType;
        }

        public void setContentType(String contentType)
        {
            this.contentType = contentType;
        }
    }
}
