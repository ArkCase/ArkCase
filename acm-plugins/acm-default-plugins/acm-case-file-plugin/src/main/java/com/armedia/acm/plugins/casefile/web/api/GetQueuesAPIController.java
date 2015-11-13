package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.model.AcmQueue;
import com.armedia.acm.plugins.casefile.service.AcmQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping({"/api/v1/plugin/queues", "/api/latest/plugin/queues"})
public class GetQueuesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmQueueService acmQueueService;


    // FIXME: this method should return only the queues the user is allowed to see
    // TODO: rename method
    // FIXME: no order id available
//    @PreAuthorize("hasPermission(#orderId, 'CASE_FILE', 'viewBillingQueueInQueueMenu') or " +
//            "hasPermission(#orderId, 'CASE_FILE', 'viewDistributionQueueInQueueMenu') or " +
//            "hasPermission(#orderId, 'CASE_FILE', 'viewFulfillQueueInQueueMenu') or " +
//            "hasPermission(#orderId, 'CASE_FILE', 'viewPendingResolutionQueueInQueueMenu') or " +
//            "hasPermission(#orderId, 'CASE_FILE', 'viewQualityControlQueueInQueueMenu') or " +
//            "hasPermission(#orderId, 'CASE_FILE', 'viewTranscribeQueueInQueueMenu')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    List<AcmQueue> findCaseById(Authentication auth
    ) throws AcmObjectNotFoundException
    {
        return acmQueueService.listAllQueues();
    }

    public void setAcmQueueService(AcmQueueService acmQueueService)
    {
        this.acmQueueService = acmQueueService;
    }
}
