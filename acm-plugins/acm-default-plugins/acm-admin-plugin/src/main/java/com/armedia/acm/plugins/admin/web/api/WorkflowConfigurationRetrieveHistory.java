package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.activiti.model.AcmProcessDefinition;
import com.armedia.acm.plugins.admin.exception.AcmWorkflowConfigurationException;
import com.armedia.acm.plugins.admin.service.WorkflowConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by sergey.kolomiets on 6/9/15.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class WorkflowConfigurationRetrieveHistory
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private WorkflowConfigurationService workflowConfigurationService;

    @RequestMapping(value = "/workflowconfiguration/workflows/{key}/versions/{version}/history", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE
    })
    @ResponseBody
    public List<AcmProcessDefinition> retrieveHistory(
            @PathVariable("key") String key,
            @PathVariable("version") int version) throws IOException, AcmWorkflowConfigurationException
    {

        try
        {
            List<AcmProcessDefinition> processDefinitions = workflowConfigurationService.retrieveHistory(key, version);

            return processDefinitions;
        }
        catch (Exception e)
        {
            log.error("Can't get workflows history", e);
            throw new AcmWorkflowConfigurationException("Can't get workflows history", e);
        }
    }

    public void setWorkflowConfigurationService(WorkflowConfigurationService workflowConfigurationService)
    {
        this.workflowConfigurationService = workflowConfigurationService;
    }
}
