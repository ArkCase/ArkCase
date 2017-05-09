package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class NotificationSenderFactory
{
    private final Logger log = LoggerFactory.getLogger(getClass());
    private PropertyFileManager propertyFileManager;
    private String notificationPropertyFileLocation;
    private Map<String, NotificationSender> notificationSenderMap;

    public NotificationSender getNotificationSender()
    {
        String flowType = NotificationConstants.SMTP;
        try
        {
            flowType = getPropertyFileManager().load(getNotificationPropertyFileLocation(), NotificationConstants.EMAIL_FLOW_TYPE, NotificationConstants.SMTP);
        } catch (AcmEncryptionException e)
        {
            log.error("Encryption error: {}", e.getMessage());
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

    public Map<String, NotificationSender> getNotificationSenderMap()
    {
        return notificationSenderMap;
    }

    public void setNotificationSenderMap(Map<String, NotificationSender> notificationSenderMap)
    {
        this.notificationSenderMap = notificationSenderMap;
    }
}
