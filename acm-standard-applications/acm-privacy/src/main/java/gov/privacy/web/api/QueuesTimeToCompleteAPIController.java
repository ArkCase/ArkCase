package gov.privacy.web.api;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.privacy.model.QueueTimeToComplete;
import gov.privacy.service.QueuesTimeToCompleteService;

@Controller
@RequestMapping({ "/api/v1/service/queues/time-to-complete", "/api/latest/service/queues/time-to-complete" })
public class QueuesTimeToCompleteAPIController
{

    private QueuesTimeToCompleteService queuesTimeToCompleteService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void saveTimeToComplete(@RequestBody QueueTimeToComplete queueTimeToComplete)
    {

        queuesTimeToCompleteService.saveTimeToComplete(queueTimeToComplete);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public QueueTimeToComplete getTimeToComplete()
    {
        return queuesTimeToCompleteService.getTimeToComplete();
    }

    public QueuesTimeToCompleteService getQueuesTimeToCompleteService()
    {
        return queuesTimeToCompleteService;
    }

    public void setQueuesTimeToCompleteService(QueuesTimeToCompleteService queuesTimeToCompleteService)
    {
        this.queuesTimeToCompleteService = queuesTimeToCompleteService;
    }
}
