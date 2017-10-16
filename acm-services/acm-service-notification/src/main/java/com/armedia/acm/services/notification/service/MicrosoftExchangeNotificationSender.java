package com.armedia.acm.services.notification.service;

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

public class MicrosoftExchangeNotificationSender extends NotificationSender
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
        in.setTemplate(notificationTemplate);
        getEmailSenderService().sendEmailWithAttachments(in, authentication, user);
    }

    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO in, Authentication authentication, AcmUser user)
            throws Exception
    {
        in.setTemplate(notificationTemplate);
        getEmailSenderService().sendEmailWithAttachmentsAndLinks(in, authentication, user);
    }

    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO in, Authentication authentication,
            AcmUser user) throws Exception
    {
        in.setTemplate(notificationTemplate);
        return getEmailSenderService().sendEmailWithEmbeddedLinks(in, authentication, user);
    }

}
