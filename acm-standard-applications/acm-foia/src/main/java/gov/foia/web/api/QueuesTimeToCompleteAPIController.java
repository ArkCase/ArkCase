package gov.foia.web.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.foia.model.QueueTimeToComplete;
import gov.foia.service.QueuesTimeToCompleteService;

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
