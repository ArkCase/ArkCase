package com.armedia.acm.plugins.complaint.model;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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
 * One event raised per search result.
 */
public class ComplaintSearchResultEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.complaint.search.result";
    private static final long serialVersionUID = -7440634052215762578L;

    public ComplaintSearchResultEvent(ComplaintListView source)
    {
        super(source);
        setObjectId(source.getComplaintId());
        setEventDate(new Date());
        setObjectType("COMPLAINT");
        setSucceeded(true);
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }
}
