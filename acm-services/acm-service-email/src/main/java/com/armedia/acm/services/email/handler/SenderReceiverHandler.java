package com.armedia.acm.services.email.handler;

/*-
 * #%L
 * ACM Service: Email
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

import com.armedia.acm.email.model.EmailReceiverConfig;
import com.armedia.acm.services.email.filter.AcmObjectPatternMailFilter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import javax.mail.Message;
import javax.mail.MessagingException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by ana.serafimoska
 */
public class SenderReceiverHandler implements ApplicationEventPublisherAware, ApplicationContextAware
{
    private EmailReceiverConfig emailReceiverConfig;
    private ApplicationEventPublisher eventPublisher;
    private ApplicationContext applicationContext;

    public void executeSenderReceiverHandlers(Message message) throws IOException, MessagingException
    {
        for (Map.Entry<String, String> handler : emailReceiverConfig.getReplyHandlers().entrySet())
        {
            AcmObjectPatternMailFilter acmObjectPatternMailFilter = (AcmObjectPatternMailFilter) applicationContext
                    .getBean(handler.getKey());
            if (acmObjectPatternMailFilter.accept(message))
            {
                AcmObjectMailHandler acmObjectMailHandler = (AcmObjectMailHandler) applicationContext.getBean(handler.getValue());
                acmObjectMailHandler.handle(message);
                break;
            }
        }
    }

    public EmailReceiverConfig getEmailReceiverConfig()
    {
        return emailReceiverConfig;
    }

    public void setEmailReceiverConfig(EmailReceiverConfig emailReceiverConfig)
    {
        this.emailReceiverConfig = emailReceiverConfig;
    }

    public ApplicationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        eventPublisher = applicationEventPublisher;
    }
}
