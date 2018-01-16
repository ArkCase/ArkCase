package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.service.SubscriptionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/subscription", "/api/latest/service/subscription" })
public class GetUserSubscriptionAPIController
{

    private SubscriptionService subscriptionService;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSubscription> getSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        log.info("Find subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "]");

        return getSubscriptionService().getSubscriptionsByUserObjectIdAndType(userId, objectId, objectType);
    }

    public SubscriptionService getSubscriptionService()
    {
        return subscriptionService;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService)
    {
        this.subscriptionService = subscriptionService;
    }
}
