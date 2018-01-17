package com.armedia.acm.data;

import com.armedia.acm.core.AcmNotifiableEntity;

public interface AcmNotificationDao
{
    AcmNotifiableEntity findEntity(Long id);

    String getSupportedNotifiableObjectType();
}