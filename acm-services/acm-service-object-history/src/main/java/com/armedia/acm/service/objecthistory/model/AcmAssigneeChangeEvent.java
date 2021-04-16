/**
 *
 */
package com.armedia.acm.service.objecthistory.model;

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

import com.armedia.acm.core.model.AcmEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author riste.tutureski
 */
public class AcmAssigneeChangeEvent extends AcmEvent
{

    public static final String EVENT_TYPE = "com.armedia.acm.object.assignee.change";
    private static final long serialVersionUID = -969356637766220472L;

    public AcmAssigneeChangeEvent(AcmAssignment source, String userId)
    {
        super(source);

        setObjectId(source.getId());
        setObjectType("ASSIGNMENT");
        setParentObjectId(source.getObjectId());
        setParentObjectType(source.getObjectType());
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setUserId(userId);

        if (source.getNewAssignee() == null || source.getNewAssignee().isEmpty()) // For unclaiming case
            source.setNewAssignee("None");
        if (source.getOldAssignee() == null || source.getOldAssignee().isEmpty()) // For claiming case
            setEventDescription(" to " + source.getNewAssignee());
        else // For changing from one Assignee to another
            setEventDescription(" from " + source.getOldAssignee() + " to " + source.getNewAssignee());

        Map<String, Object> eventProperties = new HashMap<>();

        eventProperties.put("objectTitle", source.getObjectTitle());
        eventProperties.put("objectName", source.getObjectName());
        eventProperties.put("newAssignee", source.getNewAssignee());
        eventProperties.put("oldAssignee", source.getOldAssignee());

        setEventProperties(eventProperties);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
