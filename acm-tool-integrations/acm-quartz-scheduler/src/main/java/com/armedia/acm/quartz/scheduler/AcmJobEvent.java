package com.armedia.acm.quartz.scheduler;

/*-
 * #%L
 * ACM Tool Integrations: Quartz Scheduler
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class AcmJobEvent extends AcmEvent
{

    public AcmJobEvent(AcmJobState source, String eventType, String fireInstanceId)
    {
        super(source);
        setEventDate(new Date());
        setSucceeded(true);
        setEventType(eventType);
        setObjectType("QRTZ_JOB");
        setUserId(fireInstanceId);
        setIpAddress(getHostAddress());
        setEventDescription(String.format("%s - %s", source.getJobName(), eventType));
    }

    private String getHostAddress()
    {
        try
        {
            return InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            return "";
        }
    }

    @Override
    public AcmJobState getSource()
    {
        return (AcmJobState) super.getSource();
    }
}
