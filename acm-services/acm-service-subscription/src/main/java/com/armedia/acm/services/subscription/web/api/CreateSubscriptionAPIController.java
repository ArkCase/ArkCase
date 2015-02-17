package com.armedia.acm.services.subscription.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.subscription.dao.SubscriptionDao;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.EntityExistsException;
import javax.persistence.RollbackException;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/subscription", "/api/latest/service/subscription"})
public class CreateSubscriptionAPIController {

    private final static String QUERY_KEY = "subscription.get.object.byId";
    private final static String QUERY_PLACEHOLDER_CHARACTER = "?";
    private final static String FIRST_ROW = "0";
    private final static String MAX_ROWS = "1";
    private final static String SORT = "";

    private final static String SOLR_RESPONSE_BODY = "response";
    private final static String SOLR_RESPONSE_DOCS = "docs";
    private final static String SOLR_OBJECT_FIELD_NAME = "name";
    private final static String SOLR_OBJECT_FILED_TITLE = "title_parseable";

    private final static int ZERO = 0;

    private SubscriptionDao subscriptionDao;
    private AcmPlugin subscriptionPlugin;
    private ExecuteSolrQuery executeSolrQuery;
    private SubscriptionEventPublisher subscriptionEventPublisher;

    private Logger log = LoggerFactory.getLogger(getClass());


    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSubscription createSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {

        if ( log.isInfoEnabled() ) {
            log.info("Creating subscription for user:"+userId+" on object['" + objectType + "]:[" + objectId + "]");
        }

        AcmSubscription subscription = prepareSubscription(userId, objectType, objectId, authentication);
        try {
           AcmSubscription addedSubscription = getSubscriptionDao().save(subscription);
            getSubscriptionEventPublisher().publishSubscriptionCreatedEvent(addedSubscription,authentication,true);
        } catch ( Exception e ) {
               Throwable t =  ExceptionUtils.getRootCause(e);
               if ( t instanceof  SQLIntegrityConstraintViolationException ) {
                   if (log.isDebugEnabled())
                       log.debug("Subscription on object['" + objectType + "]:[" + objectId + "] by user: " + userId + " already exists", e);

                   List<AcmSubscription> subscriptionList = getSubscriptionDao().getSubscriptionByUserObjectIdAndType(userId, objectId, objectType);
                   if(subscriptionList.isEmpty()){
                       if(log.isErrorEnabled())
                           log.error("Constraint Violation Exception occurred while trying to create subscription on object[" + objectType + "]:[" + objectId + "] for user: " + userId,e);
                       throw new AcmCreateObjectFailedException(objectType,"Subscription for user: "+userId+" on object [" + objectType + "]:[" + objectId + "] was not inserted into the DB",e);
                   } else {
                       subscription = subscriptionList.get(ZERO);
                   }
               } else {
                   if(log.isErrorEnabled())
                       log.error("Exception occurred while trying to create subscription on object[" + objectType + "]:[" + objectId + "] for user: " + userId,e);

                   getSubscriptionEventPublisher().publishSubscriptionCreatedEvent(subscription,authentication,false);

                   throw new AcmCreateObjectFailedException(objectType,"Subscription for user: "+userId+" on object [" + objectType + "]:[" + objectId + "] was not inserted into the DB",e);
               }
        }
        return subscription;
    }

    private AcmSubscription prepareSubscription(String userId, String objectType, Long objectId , Authentication auth) throws AcmObjectNotFoundException {
        JSONObject solrResponse = findSolrObjectForSubscription(objectType, objectId, auth);
        return prepareSubscriptionObject(userId,objectId,objectType,solrResponse);
    }

    private JSONObject findSolrObjectForSubscription(String objectType, Long objectId, Authentication auth) throws AcmObjectNotFoundException {

        Map<String, Object> properties =  getSubscriptionPlugin().getPluginProperties();
        String predefinedQuery = (String)properties.get(QUERY_KEY);
        String id = objectId + "-" + objectType;

        String query = predefinedQuery.replace(QUERY_PLACEHOLDER_CHARACTER,id);

        String solrResponseJsonString;
        try {
            solrResponseJsonString = getExecuteSolrQuery().getResultsByPredefinedQuery(query, FIRST_ROW, MAX_ROWS, SORT, auth);
        } catch ( MuleException e ) {
            if(log.isErrorEnabled()){
                log.error("Mule exception occurred while performing quick search for object:"+id,e);
            }
            throw new AcmObjectNotFoundException(objectType,objectId,"Exception occurred while performing quick search for object:"+id,e);
        }
        return new JSONObject(solrResponseJsonString);
    }

    private AcmSubscription prepareSubscriptionObject( String userId, Long objectId, String objectType, JSONObject solrResponse ) throws AcmObjectNotFoundException {

        JSONObject responseBody = solrResponse.getJSONObject(SOLR_RESPONSE_BODY);
        JSONArray docsList  = responseBody.getJSONArray(SOLR_RESPONSE_DOCS);

        if(docsList.length()==0) {
            if(log.isErrorEnabled()){
                log.error("no such object to subscribe to:"+objectId  +"-" + objectType);
            }
            throw new AcmObjectNotFoundException(objectType,objectId,"no such object to subscribe to",null);
        }

        String objectName = docsList.getJSONObject(ZERO).getString(SOLR_OBJECT_FIELD_NAME);
        String objectTitle = docsList.getJSONObject(ZERO).getString(SOLR_OBJECT_FILED_TITLE);

        AcmSubscription subscription = new AcmSubscription();
        subscription.setUserId(userId);
        subscription.setObjectId(objectId);
        subscription.setSubscriptionObjectType(objectType);
        subscription.setObjectName(objectName);
        subscription.setObjectTitle(objectTitle);

        return subscription;
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

    public ExecuteSolrQuery getExecuteSolrQuery() {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery) {
        this.executeSolrQuery = executeSolrQuery;
    }
}
