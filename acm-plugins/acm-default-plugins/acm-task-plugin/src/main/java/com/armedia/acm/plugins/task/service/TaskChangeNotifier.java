package com.armedia.acm.plugins.task.service;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.data.AcmObjectChangedNotifier;
import com.armedia.acm.data.AcmObjectEventConstants;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

/**
 * Created by dmiller on 6/15/16.
 */
public class TaskChangeNotifier implements ApplicationListener<AcmApplicationTaskEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private AcmObjectChangedNotifier acmObjectChangedNotifier;

    @Override
    public void onApplicationEvent(AcmApplicationTaskEvent acmApplicationTaskEvent)
    {
        log.debug("event type: {}", acmApplicationTaskEvent.getEventType());
        if (acmApplicationTaskEvent.getEventType().endsWith(".create"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_INSERT, acmApplicationTaskEvent.getAcmTask());
        }
        else if (acmApplicationTaskEvent.getEventType().endsWith(".delete"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_DELETE, acmApplicationTaskEvent.getAcmTask());
        }
        else if (acmApplicationTaskEvent.getEventType().endsWith(".changed"))
        {
            getAcmObjectChangedNotifier().notifyChange(AcmObjectEventConstants.ACTION_UPDATE, acmApplicationTaskEvent.getAcmTask());
        }
    }

    public AcmObjectChangedNotifier getAcmObjectChangedNotifier()
    {
        return acmObjectChangedNotifier;
    }

    public void setAcmObjectChangedNotifier(AcmObjectChangedNotifier acmObjectChangedNotifier)
    {
        this.acmObjectChangedNotifier = acmObjectChangedNotifier;
    }
}
