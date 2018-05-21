package com.armedia.acm.services.timesheet.model;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.core.AcmStatefulEntity;
import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;

public class AcmTimesheetAssociatedEvent extends AcmEvent
{

    private static final long serialVersionUID = 4922835593497915522L;

    public AcmTimesheetAssociatedEvent(AcmStatefulEntity source, Long sourceId, String sourceType, String eventType, String userId,
            String ipAddress, Date eventDate, boolean succeeded)
    {
        super(source);

        setObjectId(sourceId);
        setObjectType(sourceType);
        setEventType(eventType);
        setUserId(userId);
        setIpAddress(ipAddress);
        setEventDate(eventDate);
        setSucceeded(succeeded);
    }
}
