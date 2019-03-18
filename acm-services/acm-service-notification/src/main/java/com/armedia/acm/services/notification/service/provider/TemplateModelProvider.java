package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.services.notification.model.Notification;

public interface TemplateModelProvider
{
    Object getModel(Notification notification);
}
