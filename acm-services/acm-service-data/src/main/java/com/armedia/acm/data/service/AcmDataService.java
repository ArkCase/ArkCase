package com.armedia.acm.data.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNotificationDao;

/**
 * @author riste.tutureski
 *
 */
public interface AcmDataService
{
    AcmAbstractDao<AcmObject> getDaoByObjectType(String objectType);

    AcmNotificationDao getNotificationDaoByObjectType(String objectType);
}
