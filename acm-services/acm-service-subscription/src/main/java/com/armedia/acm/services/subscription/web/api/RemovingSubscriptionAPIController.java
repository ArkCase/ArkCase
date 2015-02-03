package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
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
import java.sql.SQLTimeoutException;

/**
 * Created by marjan.stefanoski on 03.02.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/subscription", "/api/latest/service/subscription"})
public class RemovingSubscriptionAPIController {

    private SubscriptionDao subscriptionDao;
    private AcmPlugin subscriptionPlugin;
    private Logger log = LoggerFactory.getLogger(getClass());

    private final static String SUCCESS_MSG = "subscription.removed.successful";
    private final static String FAIL_MSG = "subscription.removed.failed";
    private final static String SUBSCRIPTION_NOT_FOUND_MSG="subscription.not.found";

    private final static int NO_ROW_DELETED = 0;

    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication,
            HttpSession httpSession
    ) {

        if ( log.isInfoEnabled() ) {
            log.info("Removing subscription for user:"+userId+" on object['" + objectType + "]:[" + objectId + "]");
        }

        int resultFromDeleteAction = 0;

        try {
            resultFromDeleteAction = getSubscriptionDao().deleteSubscription(userId, objectId, objectType);
        } catch ( SQLTimeoutException e ) {
            if(log.isErrorEnabled())
                log.error("Exception occurred while removing subscription on object['" + objectType + "]:[" + objectId + "] by user: "+userId,e);
            return (String)getSubscriptionPlugin().getPluginProperties().get(FAIL_MSG);
        } catch ( SQLException e ) {
            if(log.isErrorEnabled())
                log.error("Exception occurred while removing subscription on object['" + objectType + "]:[" + objectId + "] by user: "+userId,e);
            return (String)getSubscriptionPlugin().getPluginProperties().get(FAIL_MSG);
        }
        if ( resultFromDeleteAction == NO_ROW_DELETED ) {
            if(log.isDebugEnabled())
                log.debug("Subscription for user:" + userId + " on object['" + objectType + "]:[" + objectId + "] not found in the DB");
            return (String)getSubscriptionPlugin().getPluginProperties().get(SUBSCRIPTION_NOT_FOUND_MSG);
        } else {
            log.debug("Subscription for user:"+userId+" on object['" + objectType + "]:[" + objectId + "] successfully removed");
            return (String)getSubscriptionPlugin().getPluginProperties().get(SUCCESS_MSG);
        }
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
