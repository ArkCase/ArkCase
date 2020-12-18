package com.armedia.acm.calendar.service.integration.exchange.web.api;

/*-
 * #%L
 * ACM Service: Exchange Integration Calendar Service
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.service.outlook.service.OutlookRecreateableCalendarService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 31, 2017
 *
 */
@Controller
@RequestMapping({ "/api/v1/service/calendar/exchange", "/api/latest/service/calendar/exchange" })
public class AcmExchangeCalendarAPIController
{

    private OutlookRecreateableCalendarService recreatableCalendarService;

    @RequestMapping(path = "/folders/recreated", method = RequestMethod.PUT)
    public AcmContainer clearFolderRecreatedFlag(@RequestBody RecreatedEntity recreatedEntity)
            throws AcmObjectNotFoundException
    {
        AcmContainer container = recreatableCalendarService.clearFolderRecreatedFlag(recreatedEntity.getObjectType(),
                recreatedEntity.getObjectId());
        return container;
    }

    /**
     * @param recreatableCalendarService
     *            the recreatableCalendarService to set
     */
    public void setRecreatableCalendarService(OutlookRecreateableCalendarService recreatableCalendarService)
    {
        this.recreatableCalendarService = recreatableCalendarService;
    }

}
