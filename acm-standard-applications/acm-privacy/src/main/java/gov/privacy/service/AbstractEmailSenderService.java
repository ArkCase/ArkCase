package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.service.AcmEmailSenderService;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

// All emails should be send as notifications.
@Deprecated
public abstract class AbstractEmailSenderService
{
    private transient final Logger log = LogManager.getLogger(this.getClass());
    private AcmEmailSenderService emailSenderService;
    private String emailTemplate;

    public void sendEmailWithAttachment(List<String> emailAddresses,
            String subject,
            Map<String, Object> templateDataModel,
            List<Long> attachmentIds,
            AcmUser user,
            Authentication authentication) throws Exception
    {
        EmailWithAttachmentsDTO emailWithAttachmentsDTO = new EmailWithAttachmentsDTO();
        emailWithAttachmentsDTO.setSubject(subject);
        emailWithAttachmentsDTO.setHeader("");
        emailWithAttachmentsDTO.setFooter("");
        emailWithAttachmentsDTO.setBody(buildEmailBodyFromTemplate(templateDataModel));
        emailWithAttachmentsDTO.setAttachmentIds(attachmentIds);
        emailWithAttachmentsDTO.setEmailAddresses(emailAddresses);

        log.info(String.format("Sending an email with subject [%s]", subject));
        getEmailSenderService().sendEmailWithAttachments(emailWithAttachmentsDTO, authentication, user);
    }

    private String buildEmailBodyFromTemplate(Map<String, Object> model)
    {
        String emailTemplateUpdated = new String(getEmailTemplate());
        for (Map.Entry entry : model.entrySet())
        {
            emailTemplateUpdated = emailTemplateUpdated.replace("${model." + entry.getKey() + "}",
                    entry.getValue() != null ? entry.getValue().toString() : "");
        }
        return emailTemplateUpdated;
    }

    public AcmEmailSenderService getEmailSenderService()
    {
        return emailSenderService;
    }

    public void setEmailSenderService(AcmEmailSenderService emailSenderService)
    {
        this.emailSenderService = emailSenderService;
    }

    public String getEmailTemplate()
    {
        return emailTemplate;
    }

    public void setEmailTemplate(Resource emailTemplate) throws IOException
    {
        try (DataInputStream resourceStream = new DataInputStream(emailTemplate.getInputStream()))
        {
            byte[] bytes = new byte[resourceStream.available()];
            resourceStream.readFully(bytes);
            this.emailTemplate = new String(bytes, Charset.forName("UTF-8"));
        }
    }
}
