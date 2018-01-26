package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;
import com.armedia.acm.services.subscription.service.SubscriptionService;

import org.activiti.engine.impl.util.json.JSONObject;
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

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 03.02.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/subscription", "/api/latest/service/subscription" })
public class RemovingSubscriptionAPIController
{

    private AcmPlugin subscriptionPlugin;
    private SubscriptionService subscriptionService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication,
            HttpSession httpSession) throws AcmObjectNotFoundException, SQLException
    {
        log.info("Removing subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "]");
        int resultFromDeleteAction = getSubscriptionService().deleteSubscriptionForGivenObject(userId, objectId, objectType);

        if (resultFromDeleteAction == SubscriptionConstants.NO_ROW_DELETED)
        {
            log.debug("Subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "] not found in the DB");
            String msg = (String) getSubscriptionPlugin().getPluginProperties().get(SubscriptionConstants.SUCCESS_MSG);
            return prepareJsonReturnMsg(msg, objectId);
        }
        else
        {
            log.debug("Subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "] successfully removed");
            getSubscriptionService().deleteSubscriptionEventsForGivenObject(userId, objectId, objectType);
            log.debug("Deleted all subscription events related to object '{}' with id '{}' for user '{}'", objectType, objectId, userId);

            String successMsg = (String) getSubscriptionPlugin().getPluginProperties().get(SubscriptionConstants.SUCCESS_MSG);
            return prepareJsonReturnMsg(successMsg, objectId);
        }
    }

    private String prepareJsonReturnMsg(String msg, Long objectId)
    {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedSubscriptionId", objectId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
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
