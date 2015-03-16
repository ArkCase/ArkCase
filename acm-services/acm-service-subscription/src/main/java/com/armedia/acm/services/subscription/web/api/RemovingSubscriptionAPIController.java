package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
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
@RequestMapping({"/api/v1/service/subscription", "/api/latest/service/subscription"})
public class RemovingSubscriptionAPIController {

    private SubscriptionDao subscriptionDao;
    private AcmPlugin subscriptionPlugin;
    private SubscriptionEventPublisher subscriptionEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());

    private final static String SUCCESS_MSG = "subscription.removed.successful";
    private final static String SUBSCRIPTION_NOT_FOUND_MSG="subscription.not.found";

    private final static String OBJECT_TYPE = "SUBSCRIPTION";

    private final static int NO_ROW_DELETED = 0;

    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmObjectNotFoundException, SQLException {

        if ( log.isInfoEnabled() ) {
            log.info("Removing subscription for user:"+userId+" on object['" + objectType + "]:[" + objectId + "]");
        }

        int resultFromDeleteAction = 0;

        resultFromDeleteAction = getSubscriptionDao().deleteSubscription(userId, objectId, objectType);

        if ( resultFromDeleteAction == NO_ROW_DELETED ) {
            if( log.isDebugEnabled() )
                log.debug("Subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "] not found in the DB");
            getSubscriptionEventPublisher().publishSubscriptionDeletedEvent(userId, objectId, objectType, false);
            String msg = (String)getSubscriptionPlugin().getPluginProperties().get(SUCCESS_MSG);
            return prepareJsonReturnMsg( msg, objectId );
        } else {
            log.debug("Subscription for user:"+userId+" on object['" + objectType + "]:[" + objectId + "] successfully removed");
            getSubscriptionEventPublisher().publishSubscriptionDeletedEvent(userId, objectId, objectType, true);
            String successMsg = (String)getSubscriptionPlugin().getPluginProperties().get(SUCCESS_MSG);
            return prepareJsonReturnMsg( successMsg, objectId );
        }
    }

    private String prepareJsonReturnMsg( String msg,Long objectId ) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedSubscriptionId", objectId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }

    public SubscriptionEventPublisher getSubscriptionEventPublisher() {
        return subscriptionEventPublisher;
    }

    public void setSubscriptionEventPublisher(SubscriptionEventPublisher subscriptionEventPublisher) {
        this.subscriptionEventPublisher = subscriptionEventPublisher;
    }

    public AcmPlugin getSubscriptionPlugin() {
        return subscriptionPlugin;
    }

    public void setSubscriptionPlugin(AcmPlugin subscriptionPlugin) {
        this.subscriptionPlugin = subscriptionPlugin;
    }

    public SubscriptionDao getSubscriptionDao() {
        return subscriptionDao;
    }

    public void setSubscriptionDao(SubscriptionDao subscriptionDao) {
        this.subscriptionDao = subscriptionDao;
    }
}
