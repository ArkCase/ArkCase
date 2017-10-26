package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.BuckslipFutureTask;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.service.AcmTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({"/api/v1/plugin/task", "/api/latest/plugin/task"})
public class AcmTaskAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private AcmTaskService acmTaskService;

    @RequestMapping(value = "/getBuckslipFutureTasks/businessProcessId/{businessProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipFutureTasks(@PathVariable("businessProcessId") String businessProcessId)
    {
        try
        {
            log.info("Trying to fetch the Buckslip Future Tasks");
            return new ResponseEntity<List<BuckslipFutureTask>>(getAcmTaskService().getBuckslipFutureTasks(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getBuckslipPastTasks/businessProcessId/{businessProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipPastTasks(@PathVariable("businessProcessId") String businessProcessId)
    {
        try
        {
            log.info("Trying to fetch the Buckslip Past Tasks");
            return new ResponseEntity<String>(getAcmTaskService().getBuckslipPastTasks(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/getBuckslipProcessesForChildren/objectType/{objectType}/objectId/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipProcessesForObject(@PathVariable("objectType") String objectType, @PathVariable("objectId") Long objectId)
    {
        try
        {
            log.info("Trying to fetch buckslip processes for {}, with ID {}", objectType, objectId);
            return new ResponseEntity<List<BuckslipProcess>>(getAcmTaskService().getBuckslipProcessesForChildren(objectType, objectId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/isWorkflowInitiable/businessProcessId/{businessProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowInitiable(@PathVariable("businessProcessId") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow initiable");
            return new ResponseEntity<Boolean>(getAcmTaskService().isInitiatable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/isWorkflowWithdrawable/businessProcessId/{businessProcessId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowWithdrawable(@PathVariable("businessProcessId") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow withdrawable");
            return new ResponseEntity<Boolean>(getAcmTaskService().isWithdrawable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/initiateRoutingWorkflow/businessProcessId/{businessProcessId}/receiveTaskId/{receiveTaskId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> initiateRoutingWorkflow(@PathVariable("businessProcessId") String businessProcessId, @PathVariable("receiveTaskId") String receiveTaskId)
    {
        try
        {
            log.info("Initiating routing workflow with busines process Id {}", businessProcessId);
            getAcmTaskService().signalTask(businessProcessId, receiveTaskId);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/withdrawRoutingWorkflow/taskId/{taskId}/messageName/{messageName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> withdrawRoutingWorkflow(@PathVariable("taskId") Long taskId, @PathVariable("messageName") String messageName)
    {
        try
        {
            log.info("Withdrawing routing workflow with task Id {}", taskId);
            getAcmTaskService().messageTask(taskId, messageName);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/updateBuckslipProcess", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateBuckslipProcess(@RequestBody BuckslipProcess buckslipProcess)
    {
        try
        {
            log.info("Updating buckslip process with business process name {}", buckslipProcess.getBusinessProcessName());
            return new ResponseEntity<BuckslipProcess>(getAcmTaskService().updateBuckslipProcess(buckslipProcess), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
