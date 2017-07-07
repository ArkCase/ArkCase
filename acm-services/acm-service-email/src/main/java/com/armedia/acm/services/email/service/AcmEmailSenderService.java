package com.armedia.acm.services.email.service;

import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.users.model.AcmUser;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 20, 2017
 *
 */
public interface AcmEmailSenderService
{

    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void sendEmailWithAttachments(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user)
            throws Exception;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void sendEmail(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user) throws Exception;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO, Authentication authentication,
            AcmUser user) throws Exception;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    void sendEmail(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO, Authentication authentication, AcmUser user)
            throws Exception;

    @Retryable(maxAttempts = 3, value = Exception.class, backoff = @Backoff(delay = 500))
    List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO emailDTO, Authentication authentication,
            AcmUser user) throws Exception;

}
