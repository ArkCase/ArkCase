package com.armedia.acm.services.notification.service;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;
import com.armedia.acm.services.email.sender.service.EmailSenderConfigurationService;

import org.springframework.context.ApplicationListener;

import java.util.Map;

public class NotificationSenderFactory implements ApplicationListener<AbstractConfigurationFileEvent>
{

    private Map<String, NotificationSender> notificationSenderMap;
    private EmailSenderConfigurationService emailSenderConfigurationService;
    String flowType = "smtp";

    public NotificationSender getNotificationSender()
    {
        return getNotificationSenderMap().get(flowType);
    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {

        if (event instanceof ConfigurationFileChangedEvent && event.getConfigFile().getName().equals("acmEmailSender.properties"))
        {
            EmailSenderConfiguration senderConfigurationUpdated = getEmailSenderConfigurationService().readConfiguration();
            flowType = senderConfigurationUpdated.getType();
        }

    }

    /**
     * @return the emailSenderConfigurationService
     */
    public EmailSenderConfigurationService getEmailSenderConfigurationService()
    {
        return emailSenderConfigurationService;
    }

    /**
     * @param emailSenderConfigurationService
     *            the emailSenderConfigurationService to set
     */
    public void setEmailSenderConfigurationService(EmailSenderConfigurationService emailSenderConfigurationService)
    {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

    public Map<String, NotificationSender> getNotificationSenderMap()
    {
        return notificationSenderMap;
    }

    public void setNotificationSenderMap(Map<String, NotificationSender> notificationSenderMap)
    {
        this.notificationSenderMap = notificationSenderMap;
    }
}
