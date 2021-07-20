package com.armedia.acm.plugins.ecm.service.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.poi.hsmf.MAPIMessage;
import org.apache.poi.hsmf.datatypes.AttachmentChunks;
import org.apache.poi.hsmf.exceptions.ChunkNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Address;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class EmailAttachmentExtractorComponent
{
    public EmailContent extractFromMsg(MultipartFile attachment) throws ChunkNotFoundException, IOException {
        List<EmailAttachment> emailAttachments = new ArrayList<>();
        String sender = "";
        String subject;
        try(InputStream inputStream = attachment.getInputStream())
        {
            MAPIMessage message = new MAPIMessage(inputStream);
            message.setReturnNullOnMissingChunk(true);
            AttachmentChunks[] attachmentFiles = message.getAttachmentFiles();

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
            subject = message.getSubject();
            if (message.getHeaders() != null) {
                Optional<String> from = Arrays.stream(message.getHeaders()).filter(m -> m.startsWith("From:")).findFirst();
                if (from.isPresent()) {
                    if (from.toString().contains("<") && from.toString().contains(">")) {
                        sender = StringUtils.substringBetween(from.get(), "<", ">");
                    }
                    else if (from.get().length() > 6) {
                        sender = from.get().substring(6);
                    }
                }
            }
            else{
                String senderName = message.getMainChunks().getDisplayFromChunk().getValue();
                String textBodyChunk = message.getMainChunks().getTextBodyChunk().toString();
                sender = StringUtils.substringBetween(textBodyChunk,senderName+" <", ">");
                if(sender == null){
                    sender = senderName;
                }
            }
        }
        return new EmailContent(emailAttachments, subject, sender);
    }

    public EmailContent extractFromEml(MultipartFile attachment) throws Exception {
        List<EmailAttachment> emailAttachments = new ArrayList<>();
        String subject;
        String sender;
        Properties props = new Properties();
        Session mailSession = Session.getDefaultInstance(props, null);

        try(InputStream inputStream = attachment.getInputStream())
        {
            MimeMessage message = new MimeMessage(mailSession, inputStream);
            MimeMessageParser mimeParser = new MimeMessageParser(message);
            mimeParser.parse();
            List<DataSource> dsl = mimeParser.getAttachmentList();


            for (DataSource ds : dsl)
            {
                emailAttachments.add(new EmailAttachment(
                        ds.getName(),
                        ds.getInputStream(),
                        ds.getContentType()));
            }
            subject = message.getSubject();
            Address[] froms = message.getFrom();
            sender = froms == null ? "" : ((InternetAddress) froms[0]).getAddress();
        }


        return new EmailContent(emailAttachments, subject, sender);
    }
    public boolean isEmail(MultipartFile file)
    {
        return file.getOriginalFilename().endsWith(EcmFileConstants.FILE_EXTENSION_MSG) || file.getOriginalFilename().endsWith(EcmFileConstants.FILE_EXTENSION_EML);
    }

    public EmailContent extract(MultipartFile attachment) throws Exception {
        String emailFileType = attachment.getOriginalFilename();

        if (emailFileType != null && emailFileType.endsWith(EcmFileConstants.FILE_EXTENSION_MSG))
        {
            return extractFromMsg(attachment);
        }
        else if(emailFileType != null && emailFileType.endsWith(EcmFileConstants.FILE_EXTENSION_EML))
        {
            return extractFromEml(attachment);
        }
        else
        {
            throw new UnsupportedOperationException("Not supported type: " + emailFileType);
        }
    }


    public static class EmailContent
    {
        private List<EmailAttachment> emailAttachments;
        private String subject;
        private String sender;

        public EmailContent(List<EmailAttachment> emailAttachments, String subject, String sender)
        {
            this.emailAttachments = emailAttachments;
            this.subject = subject;
            this.sender = sender;
        }

        public List<EmailAttachment> getEmailAttachments()
        {
            return emailAttachments;
        }

        public void setEmailAttachments(List<EmailAttachment> emailAttachments)
        {
            this.emailAttachments = emailAttachments;
        }

        public String getSubject()
        {
            return subject;
        }

        public void setSubject(String subject)
        {
            this.subject = subject;
        }

        public String getSender()
        {
            return sender;
        }

        public void setSender(String sender)
        {
            this.sender = sender;
        }
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
