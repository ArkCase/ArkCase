package com.armedia.acm.data.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmNotificationDao;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class AcmDataServiceImpl implements AcmDataService
{

    private SpringContextHolder springContextHolder;

    @SuppressWarnings("unchecked")
    @Override
    public AcmAbstractDao<AcmObject> getDaoByObjectType(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmObject> dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public AcmNotificationDao getNotificationDaoByObjectType(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmNotificationDao> daos = getSpringContextHolder().getAllBeansOfType(AcmNotificationDao.class);

            if (daos != null)
            {
                for (AcmNotificationDao dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedNotifiableObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }
        return null;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
