package com.armedia.acm.service.outlook.service.impl;

/*-
 * #%L
 * ACM Service: MS Outlook integration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
