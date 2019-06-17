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

import com.armedia.acm.services.subscription.model.SubscriptionConfig;
import com.armedia.acm.services.subscription.model.SubscriptionConstants;
import com.armedia.acm.services.subscription.service.SubscriptionService;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 03.02.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/subscription", "/api/latest/service/subscription" })
public class RemovingSubscriptionAPIController
{

    private SubscriptionConfig subscriptionConfig;
    private SubscriptionService subscriptionService;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId) throws SQLException
    {
        log.info("Removing subscription for user: {} on object [{}]:[{}]", userId, objectType, objectId);
        int resultFromDeleteAction = getSubscriptionService().deleteSubscriptionForGivenObject(userId, objectId, objectType);

        if (resultFromDeleteAction == SubscriptionConstants.NO_ROW_DELETED)
        {
            log.debug("Subscription for user: {} on object [{}]:[{}] not found in the DB",userId, objectType,objectId);
            return prepareJsonReturnMsg("Subscription Removed Successfully", objectId);
        }
        else
        {
            log.debug("Subscription for user: {} on object [{}]:[{}] successfully removed", userId, objectType, objectId);
            getSubscriptionService().deleteSubscriptionEventsForGivenObject(userId, objectId, objectType);
            log.debug("Deleted all subscription events related to object '{}' with id '{}' for user '{}'", objectType, objectId, userId);

            return prepareJsonReturnMsg("Subscription Removed Successfully", objectId);
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

    public SubscriptionConfig getSubscriptionConfig()
    {
        return subscriptionConfig;
    }

    public void setSubscriptionConfig(SubscriptionConfig subscriptionConfig)
    {
        this.subscriptionConfig = subscriptionConfig;
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
