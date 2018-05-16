package com.armedia.acm.plugins.dashboard.site.model;

/*-
 * #%L
 * ACM Default Plugin: Dashboard
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
 * Created by joseph.mcgrady on 4/26/2017.
 */
public class SiteEvent extends AcmEvent
{
    private static final long serialVersionUID = 38795920545L;
    private static final String EVENT_TYPE_BASE = "com.armedia.acm.site";

    public SiteEvent(Site site, String eventType, boolean succeeded, String ipAddress)
    {
        super(site);

        setObjectId(site.getId());
        setObjectType(site.getObjectType());
        setEventDate(site.getModified());
        setUserId(site.getModifier());
        setEventType(EVENT_TYPE_BASE + "." + eventType);
        setSucceeded(succeeded);
        setIpAddress(ipAddress);
    }
}
