/**
 * 
 */
package com.armedia.acm.service.objecthistory.service;

/*-
 * #%L
 * ACM Service: Object History Service
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

import com.armedia.acm.objectonverter.AcmMarshaller;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.service.objecthistory.dao.AcmObjectHistoryDao;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author riste.tutureski
 *
 */
public class AcmObjectHistoryServiceImpl implements AcmObjectHistoryService
{
    private final Logger LOG = LogManager.getLogger(getClass());

    private AcmObjectHistoryDao acmObjectHistoryDao;
    private AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher;
    private ObjectConverter objectConverter;

    @Override
    public AcmObjectHistory save(AcmObjectHistory acmObjectHistory, String ipAddress)
    {
        // Save Object History to the database
        acmObjectHistory = getAcmObjectHistoryDao().save(acmObjectHistory);

        // Raise Object History created event
        getAcmObjectHistoryEventPublisher().publishCreatedEvent(acmObjectHistory, ipAddress);

        return acmObjectHistory;
    }

    @Override
    public AcmObjectHistory save(String userId, String type, Object obj, Long objectId, String objectType, Date date, String ipAddress,
            Boolean succeeded)
    {
        AcmObjectHistory acmObjectHistory = null;

        if (succeeded && StringUtils.hasText(userId) && objectId != null)
        {
            // Create object history
            acmObjectHistory = new AcmObjectHistory();

            // Set username from the user who perform the action
            acmObjectHistory.setUserId(userId);
            acmObjectHistory.setType(type);

            // Set object id and type
            acmObjectHistory.setObjectId(objectId);
            acmObjectHistory.setObjectType(objectType);

            // Convert Object to JSON string
            AcmMarshaller converter = getObjectConverter().getJsonMarshaller();
            String json = converter.marshal(obj);

            // Set JSON representation of the Object
            acmObjectHistory.setObjectString(json);

            // Set date
            acmObjectHistory.setDate(date);

            // Save object history
            acmObjectHistory = save(acmObjectHistory, ipAddress);

            return acmObjectHistory;
        }

        return acmObjectHistory;

    }

    @Override
    public AcmObjectHistory getAcmObjectHistory(Long objectId, String objectType)
    {
        return getAcmObjectHistoryDao().safeFindLastInsertedByObjectIdAndObjectType(objectId, objectType);
    }

    public AcmObjectHistoryDao getAcmObjectHistoryDao()
    {
        return acmObjectHistoryDao;
    }

    public void setAcmObjectHistoryDao(AcmObjectHistoryDao acmObjectHistoryDao)
    {
        this.acmObjectHistoryDao = acmObjectHistoryDao;
    }

    public AcmObjectHistoryEventPublisher getAcmObjectHistoryEventPublisher()
    {
        return acmObjectHistoryEventPublisher;
    }

    public void setAcmObjectHistoryEventPublisher(AcmObjectHistoryEventPublisher acmObjectHistoryEventPublisher)
    {
        this.acmObjectHistoryEventPublisher = acmObjectHistoryEventPublisher;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
