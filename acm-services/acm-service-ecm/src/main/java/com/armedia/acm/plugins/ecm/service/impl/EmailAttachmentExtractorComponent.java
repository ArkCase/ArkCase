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
