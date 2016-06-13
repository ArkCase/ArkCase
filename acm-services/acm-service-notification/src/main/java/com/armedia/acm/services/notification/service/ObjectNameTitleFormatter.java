package com.armedia.acm.services.notification.service;

import com.armedia.acm.core.AcmNotifiableEntity;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

/**
 * Created by ncuculova
 */
public class ObjectNameTitleFormatter implements CustomTitleFormatter
{
    private AcmDataService acmDataService;

    @Override
    public String format(Notification notification)
    {
        String parentObjectType = notification.getRelatedObjectType() != null ?
                notification.getRelatedObjectType() : notification.getParentType();
        Long parentObjectId = notification.getRelatedObjectId() != null ?
                notification.getRelatedObjectId() : notification.getParentId();
        String title = notification.getTitle();

        if (title != null)
        {
            AcmNotificationDao dao = getAcmDataService().getNotificationDaoByObjectType(parentObjectType);
            if (dao != null)
            {
                AcmNotifiableEntity entity = dao.findEntity(parentObjectId);
                if (entity != null)
                {
                    title = replacePlaceholder(entity.getNotifiableEntityTitle(), title, NotificationConstants.NAME_LABEL);
                }
            }
        }
        return title;
    }

    private String replacePlaceholder(String objectName, String titlePlaceholder, String placeholder)
    {
        return titlePlaceholder.replace(placeholder, objectName);
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
