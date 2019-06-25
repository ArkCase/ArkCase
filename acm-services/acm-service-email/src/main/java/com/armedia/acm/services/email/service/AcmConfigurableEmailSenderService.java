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

import com.armedia.acm.email.model.EmailSenderConfig;
import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.services.email.model.EmailBodyBuilder;
import com.armedia.acm.services.email.model.EmailBuilder;
import com.armedia.acm.services.email.model.EmailWithAttachmentsAndLinksDTO;
import com.armedia.acm.services.email.model.EmailWithAttachmentsDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksDTO;
import com.armedia.acm.services.email.model.EmailWithEmbeddedLinksResultDTO;
import com.armedia.acm.services.email.sender.service.EmailSenderConfigurationServiceImpl;
import com.armedia.acm.services.users.model.AcmUser;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jul 3, 2017
 *
 */
public class AcmConfigurableEmailSenderService
        implements ApplicationListener<AbstractConfigurationFileEvent>, InitializingBean, AcmEmailSenderService
{

    private Map<String, AcmEmailSenderService> emailSenderMap;

    private EmailSenderConfigurationServiceImpl emailSenderConfigurationService;

    private String senderType;

    private Logger logger = LogManager.getLogger(getClass());

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {
        if (event instanceof ConfigurationFileChangedEvent && event.getConfigFile().getName().equals("acmEmailSender.properties"))
        {
            readSenderType();

        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
    {
        readSenderType();
    }

    /**
     *
     */
    private void readSenderType()
    {
        EmailSenderConfig senderConfigurationUpdated = emailSenderConfigurationService.readConfiguration();
        senderType = senderConfigurationUpdated.getType();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendPlainEmail(java.util.stream.Stream,
     * com.armedia.acm.services.email.model.EmailBuilder, com.armedia.acm.services.email.model.EmailBodyBuilder)
     */
    @Override
    public <T> void sendPlainEmail(Stream<T> emailsDataStream, EmailBuilder<T> emailBuilder, EmailBodyBuilder<T> emailBodyBuilder)
            throws Exception
    {
        AcmEmailSenderService service = getSender();
        service.sendPlainEmail(emailsDataStream, emailBuilder, emailBodyBuilder);

    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachments(com.armedia.acm.services.
     * email.model.EmailWithAttachmentsDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachments(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user)
            throws Exception
    {
        AcmEmailSenderService service = getSender();
        service.sendEmailWithAttachments(emailWithAttachmentsDTO, authentication, user);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmail(com.armedia.acm.services.email.model.
     * EmailWithAttachmentsDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmail(EmailWithAttachmentsDTO emailWithAttachmentsDTO, Authentication authentication, AcmUser user) throws Exception
    {
        AcmEmailSenderService service = getSender();
        service.sendEmail(emailWithAttachmentsDTO, authentication, user);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithAttachmentsAndLinks(com.armedia.acm.
     * services.email.model.EmailWithAttachmentsAndLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmailWithAttachmentsAndLinks(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO,
            Authentication authentication, AcmUser user) throws Exception
    {
        AcmEmailSenderService service = getSender();
        service.sendEmailWithAttachmentsAndLinks(emailWithAttachmentsAndLinksDTO, authentication, user);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmail(com.armedia.acm.services.email.model.
     * EmailWithAttachmentsAndLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public void sendEmail(EmailWithAttachmentsAndLinksDTO emailWithAttachmentsAndLinksDTO, Authentication authentication, AcmUser user)
            throws Exception
    {
        AcmEmailSenderService service = getSender();
        service.sendEmail(emailWithAttachmentsAndLinksDTO, authentication, user);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.email.service.AcmEmailSenderService#sendEmailWithEmbeddedLinks(com.armedia.acm.services.
     * email.model.EmailWithEmbeddedLinksDTO, org.springframework.security.core.Authentication,
     * com.armedia.acm.services.users.model.AcmUser)
     */
    @Override
    public List<EmailWithEmbeddedLinksResultDTO> sendEmailWithEmbeddedLinks(EmailWithEmbeddedLinksDTO emailDTO,
            Authentication authentication, AcmUser user) throws Exception
    {
        AcmEmailSenderService service = getSender();
        return service.sendEmailWithEmbeddedLinks(emailDTO, authentication, user);
    }

    /**
     * @return
     * @throws AcmEmailServiceException
     */
    private AcmEmailSenderService getSender() throws AcmEmailServiceException
    {
        AcmEmailSenderService service = Optional.ofNullable(emailSenderMap.get(senderType))
                .orElseThrow(() -> new AcmEmailServiceException(String.format("No email sender configured for %s type.", senderType)));
        return service;
    }

    /**
     * @param emailSenderMap
     *            the notificationSenderMap to set
     */
    public void setEmailSenderMap(Map<String, AcmEmailSenderService> emailSenderMap)
    {
        this.emailSenderMap = emailSenderMap;
    }

    /**
     * @param emailSenderConfigurationService
     *            the emailSenderConfigurationService to set
     */
    public void setEmailSenderConfigurationService(EmailSenderConfigurationServiceImpl emailSenderConfigurationService)
    {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

}
