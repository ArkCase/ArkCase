package com.armedia.acm.services.subscription.model;

/*-
 * #%L
 * ACM Service: Subscription
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

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

/**
 * Created by marjan.stefanoski on 11.02.2015.
 */
public class AcmSubscriptionEventPersistenceEvent extends AcmEvent
{

    public AcmSubscriptionEventPersistenceEvent(AcmSubscriptionEvent source)
    {

        super(source);
        setObjectId(source.getSubscriptionEventId());
        setEventDate(new Date());
        setUserId(source.getEventUser());
        setObjectType(SubscriptionConstants.OBJECT_TYPE_EVENT);
    }

    @Override
    public String getObjectType()
    {
        return SubscriptionConstants.OBJECT_TYPE_EVENT;
    }

}
