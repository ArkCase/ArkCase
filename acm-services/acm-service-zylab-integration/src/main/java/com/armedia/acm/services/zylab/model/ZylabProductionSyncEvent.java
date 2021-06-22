package com.armedia.acm.services.zylab.model;

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

import java.util.Date;

import org.springframework.security.core.Authentication;

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.model.AcmEvent;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on March, 2021
 */
public class ZylabProductionSyncEvent extends AcmEvent
{
    private static final String EVENT_TYPE = "com.armedia.acm.zylab.production.sync";

    private ZylabProductionSyncDTO zylabProductionSyncDTO;

    public ZylabProductionSyncEvent(Object source, Long objectId, String objectType, ZylabProductionSyncDTO zylabProductionSyncDTO,
            boolean success, Authentication auth)
    {
        super(source);

        setObjectId(objectId);
        setObjectType(objectType);
        setEventDate(new Date());
        setEventType(EVENT_TYPE);
        setSucceeded(success);
        setUserId(auth.getName());
        setIpAddress(AuthenticationUtils.getUserIpAddress());
        setEventDescription(String.format("Sync production %s for matter %d", zylabProductionSyncDTO.getProductionKey(),
                zylabProductionSyncDTO.getMatterId()));
        setZylabProductionSyncDTO(zylabProductionSyncDTO);
    }

    public ZylabProductionSyncDTO getZylabProductionSyncDTO()
    {
        return zylabProductionSyncDTO;
    }

    public void setZylabProductionSyncDTO(ZylabProductionSyncDTO zylabProductionSyncDTO)
    {
        this.zylabProductionSyncDTO = zylabProductionSyncDTO;
    }

    @Override
    public String getEventType()
    {
        return EVENT_TYPE;
    }

}
