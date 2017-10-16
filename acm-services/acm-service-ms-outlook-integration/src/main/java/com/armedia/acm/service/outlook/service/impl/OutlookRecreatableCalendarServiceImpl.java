package com.armedia.acm.service.outlook.service.impl;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.service.OutlookRecreateableCalendarService;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
public class OutlookRecreatableCalendarServiceImpl implements OutlookRecreateableCalendarService
{

    private AcmContainerDao containerDao;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.armedia.acm.service.outlook.service.OutlookRecreateableCalendarService#clearFolderRecreatedFlag(java.lang.
     * String, java.lang.Long)
     */
    @Override
    @Transactional
    public AcmContainer clearFolderRecreatedFlag(String objectType, Long objectId) throws AcmObjectNotFoundException
    {
        AcmContainer container = containerDao.findFolderByObjectTypeAndId(objectType, objectId);
        container.setCalendarFolderRecreated(false);
        return container;
    }

    /**
     * @param containerDao
     *            the containerDao to set
     */
    public void setContainerDao(AcmContainerDao containerDao)
    {
        this.containerDao = containerDao;
    }

}
