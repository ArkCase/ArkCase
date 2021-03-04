package com.armedia.acm.services.email.handler;

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
