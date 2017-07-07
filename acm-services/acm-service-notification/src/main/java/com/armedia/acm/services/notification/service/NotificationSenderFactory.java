package com.armedia.acm.services.notification.service;

import com.armedia.acm.files.AbstractConfigurationFileEvent;
import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.services.email.sender.model.EmailSenderConfiguration;
import com.armedia.acm.services.email.sender.service.EmailSenderConfigurationServiceImpl;

import org.springframework.context.ApplicationListener;

import java.util.Map;

public class NotificationSenderFactory implements ApplicationListener<AbstractConfigurationFileEvent>
{
    private Map<String, NotificationSender> notificationSenderMap;
    private EmailSenderConfigurationServiceImpl emailSenderConfigurationService;
    String flowType = "smtp";

    public NotificationSender getNotificationSender()
    {
        return notificationSenderMap.get(flowType);
    }

    @Override
    public void onApplicationEvent(AbstractConfigurationFileEvent event)
    {

        if (event instanceof ConfigurationFileChangedEvent && event.getConfigFile().getName().equals("acmEmailSender.properties"))
        {
            EmailSenderConfiguration senderConfigurationUpdated = emailSenderConfigurationService.readConfiguration();
            flowType = senderConfigurationUpdated.getType();
        }

    }

    /**
     * @param emailSenderConfigurationService the emailSenderConfigurationService to set
     */
    public void setEmailSenderConfigurationService(EmailSenderConfigurationServiceImpl emailSenderConfigurationService)
    {
        this.emailSenderConfigurationService = emailSenderConfigurationService;
    }

    public void setNotificationSenderMap(Map<String, NotificationSender> notificationSenderMap)
    {
        this.notificationSenderMap = notificationSenderMap;
    }
}
