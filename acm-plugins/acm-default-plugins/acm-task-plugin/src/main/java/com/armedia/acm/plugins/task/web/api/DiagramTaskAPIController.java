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

import com.armedia.acm.core.model.DiagramResponse;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.service.AcmTaskService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by riste.tutureski on 5/12/2017.
 */
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class DiagramTaskAPIController
{
    private AcmTaskService acmTaskService;

    @RequestMapping(value = "/diagram/{taskId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DiagramResponse diagram(@PathVariable("taskId") Long taskId) throws AcmTaskException
    {
        byte[] data = getAcmTaskService().getDiagram(taskId);
        DiagramResponse response = new DiagramResponse(data);

        return response;
    }

    @RequestMapping(value = "/diagram/process/{processId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public DiagramResponse diagramByProcessId(@PathVariable("processId") String processId) throws AcmTaskException
    {
        byte[] data = getAcmTaskService().getDiagram(processId);
        DiagramResponse response = new DiagramResponse(data);

        return response;
    }

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }
}
