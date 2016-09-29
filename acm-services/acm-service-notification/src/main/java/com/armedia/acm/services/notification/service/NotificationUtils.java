package com.armedia.acm.services.notification.service;


import com.armedia.acm.core.AcmApplication;
import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.core.AcmObjectType;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.NotificationConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Properties;

public class NotificationUtils
{
    private Properties notificationProperties;

    private AcmApplication acmAppConfiguration;

    private AcmDataService acmDataService;

    public String buildNotificationLink(String parentType, Long parentId, String relatedObjectType, Long relatedObjectId)
    {
        String baseUrl = getNotificationProperties().getProperty(NotificationConstants.BASE_URL_KEY);
        // If relatedObjectType is null, the parent object is TOP LEVEL (Case File or Complaint)
        // else parent object is nested in top level object
        String linkedObjectType = StringUtils.isEmpty(relatedObjectType) ? parentType : relatedObjectType;
        Long linkedObjectId = StringUtils.isEmpty(relatedObjectType) ? parentId : relatedObjectId;

        // find the object type from the ACM application configuration, and get the URL from the object type
        for (AcmObjectType objectType : getAcmAppConfiguration().getObjectTypes())
        {
            Map<String, String> urlValues = objectType.getUrl();
            if (objectType.getName().equals(parentType) && StringUtils.isNotEmpty(linkedObjectType))
            {
                String objectUrl = urlValues.get(linkedObjectType);
                if (StringUtils.isNotEmpty(objectUrl))
                {
                    objectUrl = String.format(objectUrl, linkedObjectId);
                    return String.format("%s%s", baseUrl, objectUrl);
                }
            }
        }

        return null;
    }


    public String getNotificationParentOrRelatedObjectNumber(String objectType, Long objectId)
    {
        if (objectType != null && objectId != null)
        {
            AcmNotificationDao dao = getAcmDataService().getNotificationDaoByObjectType(objectType);
            if (dao != null)
            {
                AcmNotifiableEntity entity = dao.findEntity(objectId);
                if (entity != null)
                {
                    return entity.getNotifiableEntityTitle();
                }
            }
        }
        return null;
    }

    public Properties getNotificationProperties()
    {
        return notificationProperties;
    }

    public void setNotificationProperties(Properties notificationProperties)
    {
        this.notificationProperties = notificationProperties;
    }

    public AcmApplication getAcmAppConfiguration()
    {
        return acmAppConfiguration;
    }

    public void setAcmAppConfiguration(AcmApplication acmAppConfiguration)
    {
        this.acmAppConfiguration = acmAppConfiguration;
    }

    public AcmDataService getAcmDataService()
    {
        return acmDataService;
    }

    public void setAcmDataService(AcmDataService acmDataService)
    {
        this.acmDataService = acmDataService;
    }
}
