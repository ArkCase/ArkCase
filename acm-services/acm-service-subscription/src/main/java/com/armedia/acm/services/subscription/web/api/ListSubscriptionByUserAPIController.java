package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.exception.AcmSubscriptionException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 03.02.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/subscription", "/api/latest/service/subscription" })
public class ListSubscriptionByUserAPIController
{

    private SubscriptionService subscriptionService;
    private AcmPlugin subscriptionPlugin;
    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSubscription> listSubscriptionsByUser(@PathVariable("userId") String userId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "-1") int maxRows, Authentication authentication,
            HttpSession httpSession) throws AcmSubscriptionException
    {
        log.info("Listing subscriptions for user [{}]", userId);
        List<AcmSubscription> subscriptionList = null;
        try
        {
            subscriptionList = getSubscriptionService().getSubscriptionsByUser(userId, startRow, maxRows);
        } catch (AcmObjectNotFoundException e)
        {
            log.debug("No Subscriptions Found for user [{}], {}", userId, e.getMessage());
            return new ArrayList<>();
        }
        return subscriptionList;
    }

    public AcmPlugin getSubscriptionPlugin()
    {
        return subscriptionPlugin;
    }

    public void setSubscriptionPlugin(AcmPlugin subscriptionPlugin)
    {
        this.subscriptionPlugin = subscriptionPlugin;
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
