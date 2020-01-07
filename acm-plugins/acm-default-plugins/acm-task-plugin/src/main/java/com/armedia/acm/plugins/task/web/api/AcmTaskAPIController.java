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

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.model.TaskConstants;
import com.armedia.acm.plugins.task.service.AcmTaskService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class AcmTaskAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private AcmTaskService acmTaskService;

    @RequestMapping(value = "/businessProcess/{id}/futureTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipFutureTasks(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Trying to fetch the Future Tasks from Business Process {}", businessProcessId);
            return new ResponseEntity<>(getAcmTaskService().getBuckslipFutureTasks(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/pastTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipPastTasks(@PathVariable("id") String businessProcessId,
            @RequestParam(value = "readFromHistory", required = false, defaultValue = "false") boolean readFromHistory)
    {
        try
        {
            log.info("Trying to fetch the Past Tasks from Business Process {}", businessProcessId);
            return new ResponseEntity<>(getAcmTaskService().getBuckslipPastTasks(businessProcessId, readFromHistory), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/objectType/{type}/objectId/{id}/buckslipProcessesForChildren", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipProcessesForObject(@PathVariable("type") String objectType,
            @PathVariable("id") Long objectId)
    {
        try
        {
            log.info("Trying to fetch buckslip processes for {}, with ID {}", objectType, objectId);
            return new ResponseEntity<>(getAcmTaskService().getBuckslipProcessesForChildren(objectType, objectId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{objectType}/{objectId}/pastTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipPastTasksForObject(@PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @RequestParam(value = "readFromHistory", required = false, defaultValue = "false") boolean readFromHistory,
            Authentication authentication)
    {
        log.info("Trying to fetch the completed Business Processes Id for object {}, with id {}", objectType, objectId);
        Long businessProcessId = getAcmTaskService().getCompletedBuckslipProcessIdForObjectFromSolr(objectType, objectId, authentication);

        try
        {
            log.info("Trying to fetch the Past Tasks from Business Process {}", businessProcessId);
            return new ResponseEntity<>(getAcmTaskService().getBuckslipPastTasks(String.valueOf(businessProcessId), readFromHistory),
                    HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            log.debug("No history for Business Proess {}", businessProcessId);
            return new ResponseEntity<>(new ArrayList<>().toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/businessProcess/{objectType}/{objectId}/{processVariable}/businessProcessVariable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBusinessProcessVariableForObject(@PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @PathVariable("processVariable") String processVariable,
            @RequestParam(value = "readFromHistory", required = false, defaultValue = "false") boolean readFromHistory,
            Authentication authentication) throws AcmTaskException
    {

        log.info("Trying to fetch the completed Business Processes Id for object {}, with id {}", objectType, objectId);

        try
        {
            return new ResponseEntity<>(getAcmTaskService().getBusinessProcessVariableByObjectType(objectType, objectId, processVariable,
                    readFromHistory, authentication), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(new ArrayList<>().toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/initiatable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowInitiable(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow for Business Process {} initiable", businessProcessId);
            return new ResponseEntity<>(getAcmTaskService().isInitiatable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/withdrawable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowWithdrawable(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow for Business Process {} withdrawable", businessProcessId);
            return new ResponseEntity<>(getAcmTaskService().isWithdrawable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/initiate", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> initiateRoutingWorkflow(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Initiating routing workflow with Business Process {}", businessProcessId);
            getAcmTaskService().signalTask(businessProcessId, TaskConstants.INITIATE_TASK_NAME);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{taskId}/withdraw", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> withdrawRoutingWorkflow(@PathVariable("taskId") Long taskId)
    {
        try
        {
            log.info("Withdrawing routing workflow with task Id {}", taskId);
            getAcmTaskService().messageTask(taskId, "Withdraw Message");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/buckslipProcesses", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateBuckslipProcess(@RequestBody BuckslipProcess buckslipProcess)
    {
        try
        {
            log.info("Updating buckslip process with business process name {}", buckslipProcess.getBusinessProcessName());
            return new ResponseEntity<>(getAcmTaskService().updateBuckslipProcess(buckslipProcess), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
