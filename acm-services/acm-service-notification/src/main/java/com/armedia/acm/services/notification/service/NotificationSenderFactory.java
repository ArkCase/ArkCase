package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.model.NotificationConstants;

public class NotificationSenderFactory
{
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private NotificationSender smtpNotificationSender;
    private NotificationSender microsoftExchangeNotificationSender;

    public NotificationSender getNotificationSender()
    {
        String flowType = "smtp";
        try
        {
            flowType = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FLOW_TYPE, "smtp");
        } catch (AcmEncryptionException e)
        {
        }
        if (("outlook").equals(flowType))
        {
            return getMicrosoftExchangeNotificationSender();
        } else
        {
            return getSmtpNotificationSender();
        }
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
}
