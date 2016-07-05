package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.model.NotificationConstants;

import java.util.Map;

public class NotificationSenderFactory
{
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private NotificationSender smtpNotificationSender;
    private NotificationSender microsoftExchangeNotificationSender;
    private Map<String, NotificationSender> notificationSenderMap;

    public NotificationSender getNotificationSender()
    {
        String flowType = "smtp";
        try
        {
            flowType = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FLOW_TYPE, "smtp");
        } catch (AcmEncryptionException e)
        {
        }
        return getNotificationSenderMap().get(flowType);
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getNotificationPropertyFileLocation()
    {
        return notificationPropertyFileLocation;
    }

    public void setNotificationPropertyFileLocation(String notificationPropertyFileLocation)
    {
        this.notificationPropertyFileLocation = notificationPropertyFileLocation;
    }

    public NotificationSender getSmtpNotificationSender()
    {
        return smtpNotificationSender;
    }

    public void setSmtpNotificationSender(SmtpNotificationSender smtpNotificationSender)
    {
        this.smtpNotificationSender = smtpNotificationSender;
    }

    public NotificationSender getMicrosoftExchangeNotificationSender()
    {
        return microsoftExchangeNotificationSender;
    }

    public void setMicrosoftExchangeNotificationSender(MicrosoftExchangeNotificationSender microsoftExchangeNotificationSender)
    {
        this.microsoftExchangeNotificationSender = microsoftExchangeNotificationSender;
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
