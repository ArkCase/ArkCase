package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.model.NotificationConstants;

import java.util.Map;

public class NotificationSenderFactory
{
    private PropertyFileManager propertyFileManager;
    private String emailSenderPropertyFileLocation;
    private Map<String, NotificationSender> notificationSenderMap;

    public NotificationSender getNotificationSender()
    {
        String flowType = "smtp";
        try
        {
            flowType = getPropertyFileManager().load(getEmailSenderPropertyFileLocation(), NotificationConstants.EMAIL_FLOW_TYPE, "smtp");
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

    public String getEmailSenderPropertyFileLocation()
    {
        return emailSenderPropertyFileLocation;
    }

    public void setEmailSenderPropertyFileLocation(String emailSenderPropertyFileLocation)
    {
        this.emailSenderPropertyFileLocation = emailSenderPropertyFileLocation;
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
