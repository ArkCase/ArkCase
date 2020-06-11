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

    <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
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
