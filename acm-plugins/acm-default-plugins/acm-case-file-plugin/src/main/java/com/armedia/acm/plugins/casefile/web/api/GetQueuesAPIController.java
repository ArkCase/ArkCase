package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.service.AcmQueueService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/queues", "/api/latest/plugin/queues" })
public class GetQueuesAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmQueueService acmQueueService;

    @PostFilter("hasPermission(filterObject.id, 'QUEUE', 'viewQueueInQueueMenu')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmQueue> getQueues(Authentication auth) throws AcmObjectNotFoundException
    {
        return acmQueueService.listAllQueues();
    }

    public void setAcmQueueService(AcmQueueService acmQueueService)
    {
        this.acmQueueService = acmQueueService;
    }
}
