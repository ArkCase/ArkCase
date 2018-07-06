package com.armedia.acm.services.billing.model;

/*-
 * #%L
 * ACM Service: Billing
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

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingItemEvent extends AcmEvent
{

    public BillingItemEvent(BillingItem source, String billingItemEvent, boolean succeeded, String ipAddress)
    {
        super(source);

        setObjectId(source.getId());
        setEventDate(source.getCreated());
        setUserId(source.getCreator());
        setEventType(String.format("%s.%s", BillingItemConstants.EVENT_TYPE, billingItemEvent));
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
        setObjectType(BillingItemConstants.OBJECT_TYPE);
        setParentObjectId(source.getParentObjectId());
        setParentObjectType(source.getParentObjectType());
    }
}
