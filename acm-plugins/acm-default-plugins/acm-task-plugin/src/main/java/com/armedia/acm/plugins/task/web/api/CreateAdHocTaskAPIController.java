package com.armedia.acm.plugins.task.web.api;

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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.impl.CreateAdHocTaskService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class CreateAdHocTaskAPIController
{
    private CreateAdHocTaskService createAdHocTaskService;

    @RequestMapping(value = "/adHocTask", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTask createAdHocTask(@RequestBody AcmTask in, Authentication authentication, HttpSession httpSession) throws AcmAppErrorJsonMsg, AcmCreateObjectFailedException {

        String ipAddress = (String) httpSession.getAttribute("acm_ip_address");
        return createAdHocTaskService.createAdHocTask(in, authentication, ipAddress);
    }

    public CreateAdHocTaskService getCreateAdHocTaskService()
    {
        return createAdHocTaskService;
    }

    public void setCreateAdHocTaskService(CreateAdHocTaskService createAdHocTaskService)
    {
        this.createAdHocTaskService = createAdHocTaskService;
    }
}
