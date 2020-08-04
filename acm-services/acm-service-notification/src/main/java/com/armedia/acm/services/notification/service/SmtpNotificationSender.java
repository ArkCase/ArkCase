package com.armedia.acm.services.notification.service;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Stream;

public class SmtpNotificationSender extends NotificationSender
{

    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception
    {
        getEmailSenderService().sendPlainEmail(emailsDataStream, emailBuilder, emailBodyBuilder);
    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, String userId) throws Exception
    {
        // Sending as system user to create AcmOutlookUser, ignoring userId
        AcmUser user = getUserDao().findByUserId(userId);
        sendEmailWithAttachments(in, authentication, user);
    }

    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO in, Authentication authentication, AcmUser user) throws Exception
    {
        getEmailSenderService().sendEmailWithAttachments(in, authentication, user);
    }

    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception
    {
        if (in.getTemplate() == null)
        {
            in.setTemplate(notificationTemplate);
        }
        getEmailSenderService().sendEmailWithAttachmentsAndLinks(in, authentication, user);
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        if (in.getTemplate() == null)
        {
            in.setTemplate(notificationTemplate);
        }
        return getEmailSenderService().sendEmailWithEmbeddedLinks(in, authentication, user);
    }

}
