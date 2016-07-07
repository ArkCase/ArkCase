package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.service.AcmQueueService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger log = LoggerFactory.getLogger(getClass());

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
