package com.armedia.acm.plugins.task.web.api;

import com.armedia.acm.data.BuckslipFutureTask;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.BuckslipProcess;
import com.armedia.acm.plugins.task.model.TaskConstants;
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

    @RequestMapping(value = "/businessProcess/{id}/futureTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipFutureTasks(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Trying to fetch the Future Tasks from Business Process {}", businessProcessId);
            return new ResponseEntity<List<BuckslipFutureTask>>(getAcmTaskService().getBuckslipFutureTasks(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/pastTasks", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipPastTasks(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Trying to fetch the Past Tasks from Business Process {}", businessProcessId);
            return new ResponseEntity<String>(getAcmTaskService().getBuckslipPastTasks(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/objectType/{type}/objectId/{id}/buckslipProcessesForChildren", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> getBuckslipProcessesForObject(@PathVariable("type") String objectType, @PathVariable("id") Long objectId)
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

    @RequestMapping(value = "/businessProcess/{id}/initiatable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowInitiable(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow for Business Process {} initiable", businessProcessId);
            return new ResponseEntity<Boolean>(getAcmTaskService().isInitiatable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/businessProcess/{id}/withdrawable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<?> isWorkflowWithdrawable(@PathVariable("id") String businessProcessId)
    {
        try
        {
            log.info("Checking is the routing workflow for Business Process {] withdrawable", businessProcessId);
            return new ResponseEntity<Boolean>(getAcmTaskService().isWithdrawable(businessProcessId), HttpStatus.OK);
        }
        catch (AcmTaskException e)
        {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/buckslipProcesses", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
