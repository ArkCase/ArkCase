package com.armedia.acm.services.zylab.service;

/*-
 * #%L
 * ACM Service: Arkcase ZyLAB Integration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

import com.armedia.acm.services.zylab.jms.ZylabProductionSyncStatusToJmsSender;
import com.armedia.acm.services.zylab.model.ZylabProductionSyncEvent;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionSyncListener implements ApplicationListener<ZylabProductionSyncEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private ZylabProductionSyncStatusToJmsSender zylabProductionSyncStatusToJmsSender;

    @Override
    public void onApplicationEvent(ZylabProductionSyncEvent zylabProductionSyncEvent)
    {
        zylabProductionSyncStatusToJmsSender.sendProductionSyncStatus(zylabProductionSyncEvent.getZylabProductionSyncDTO());
    }

    public ZylabProductionSyncStatusToJmsSender getZylabProductionSyncStatusToJmsSender()
    {
        return zylabProductionSyncStatusToJmsSender;
    }

    public void setZylabProductionSyncStatusToJmsSender(ZylabProductionSyncStatusToJmsSender zylabProductionSyncStatusToJmsSender)
    {
        this.zylabProductionSyncStatusToJmsSender = zylabProductionSyncStatusToJmsSender;
    }
}
