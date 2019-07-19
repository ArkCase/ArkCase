package com.armedia.acm.plugins.admin.web.api;

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

import com.armedia.acm.quartz.scheduler.AcmJobState;
import com.armedia.acm.quartz.scheduler.AcmSchedulerService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = { "/api/latest/plugin/admin/scheduler" }, produces = MediaType.APPLICATION_JSON_VALUE)
public class AcmSchedulerAPIController
{
    private AcmSchedulerService schedulerService;

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, AcmJobState> getScheduledJobs()
    {
        return schedulerService.getAllScheduledJobDetails();
    }

    @RequestMapping(value = "/jobs/{name}/run", method = RequestMethod.PUT)
    @ResponseBody
    public void runJob(@PathVariable String name)
    {
        schedulerService.triggerJob(name);
    }

    @RequestMapping(value = "/jobs/{name}/pause", method = RequestMethod.PUT)
    @ResponseBody
    public void pauseJob(@PathVariable String name)
    {
        schedulerService.pauseJob(name);
    }

    @RequestMapping(value = "/jobs/{name}/resume", method = RequestMethod.PUT)
    @ResponseBody
    public void resumeJob(@PathVariable String name)
    {
        schedulerService.resumeJob(name);
    }

    public AcmSchedulerService getSchedulerService()
    {
        return schedulerService;
    }

    public void setSchedulerService(AcmSchedulerService schedulerService)
    {
        this.schedulerService = schedulerService;
    }
}
