package com.armedia.acm.services.subscription.web.api;

/*-
 * #%L
 * ACM Service: Subscription
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
