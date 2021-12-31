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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.subscription.model.AcmSubscription;
import com.armedia.acm.services.subscription.model.SubscriptionConfig;
import com.armedia.acm.services.subscription.service.SubscriptionEventPublisher;
import com.armedia.acm.services.subscription.service.SubscriptionService;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 02.02.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/subscription", "/api/latest/service/subscription" })
public class CreateSubscriptionAPIController
{
    private final static String QUERY_PLACEHOLDER_CHARACTER = "?";
    private final static int FIRST_ROW = 0;
    private final static int MAX_ROWS = 1;
    private final static String SORT = "";

    private final static String SOLR_RESPONSE_BODY = "response";
    private final static String SOLR_RESPONSE_DOCS = "docs";
    private final static String SOLR_OBJECT_FIELD_NAME = "name";
    private final static String SOLR_OBJECT_FILED_TITLE = "title_parseable";

    private final static int ZERO = 0;

    private SubscriptionService subscriptionService;
    private SubscriptionConfig subscriptionConfig;
    private ExecuteSolrQuery executeSolrQuery;
    private SubscriptionEventPublisher subscriptionEventPublisher;

    private Logger log = LogManager.getLogger(getClass());

    @PreAuthorize("hasPermission(#objectId, #objectType, 'subscribe')")
    @RequestMapping(value = "/{userId}/{objType}/{objId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSubscription createSubscription(
            @PathVariable("userId") String userId,
            @PathVariable("objType") String objectType,
            @PathVariable("objId") Long objectId,
            Authentication authentication) throws AcmCreateObjectFailedException, AcmObjectNotFoundException
    {

        log.info("Creating subscription for user: {} on object [{}]:[{}]", userId, objectType, objectId);

        AcmSubscription subscription = prepareSubscription(userId, objectType, objectId, authentication);
        try
        {
            AcmSubscription addedSubscription = getSubscriptionService().saveSubscription(subscription);
            getSubscriptionEventPublisher().publishSubscriptionCreatedEvent(addedSubscription, authentication, true);
            subscription = addedSubscription;
        }
        catch (Exception e)
        {
            Throwable t = ExceptionUtils.getRootCause(e);
            if (t instanceof SQLIntegrityConstraintViolationException)
            {
                log.debug("Subscription on object [{}]:[{}] by user: {} already exists. {}",
                        objectType, objectId, userId, e.getMessage());

                List<AcmSubscription> subscriptionList = getSubscriptionService().getSubscriptionsByUserObjectIdAndType(userId, objectId,
                        objectType);
                if (subscriptionList.isEmpty())
                {
                    log.error("Creating subscription for object [{}]:[{}] for user: {} failed. {}", objectType,
                            objectId, userId, e.getMessage());
                    throw new AcmCreateObjectFailedException(objectType, "Subscription for user: " + userId + " on object [" + objectType
                            + "]:[" + objectId + "] was not inserted into the DB", e);
                }
                else
                {
                    subscription = subscriptionList.get(ZERO);
                }
            }
            else
            {
                log.error("Creating subscription for object [{}]:[{}] for user: {} failed. {}", objectType,
                        objectId, userId, e.getMessage());

                getSubscriptionEventPublisher().publishSubscriptionCreatedEvent(subscription, authentication, false);

                throw new AcmCreateObjectFailedException(objectType, "Subscription for user: " + userId + " on object [" + objectType
                        + "]:[" + objectId + "] was not inserted into the DB", e);
            }
        }
        return subscription;
    }

    private AcmSubscription prepareSubscription(String userId, String objectType, Long objectId, Authentication auth)
            throws AcmObjectNotFoundException
    {
        JSONObject solrResponse = findSolrObjectForSubscription(objectType, objectId, auth);
        return prepareSubscriptionObject(userId, objectId, objectType, solrResponse);
    }

    private JSONObject findSolrObjectForSubscription(String objectType, Long objectId, Authentication auth)
            throws AcmObjectNotFoundException
    {
        String predefinedQuery = subscriptionConfig.getGetObjectByIdQuery();

        String id = objectId + "-" + objectType;

        String query = predefinedQuery.replace(QUERY_PLACEHOLDER_CHARACTER, id);

        String solrResponseJsonString;
        try
        {
            solrResponseJsonString = getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH,
                    query, FIRST_ROW, MAX_ROWS, SORT);
        }
        catch (SolrException e)
        {
            log.error("Solr exception occurred while performing advanced search for object: [{}]. {}", id, e.getMessage());
            throw new AcmObjectNotFoundException(objectType, objectId, "Exception occurred while performing advanced search for object:" + id,
                    e);
        }
        return new JSONObject(solrResponseJsonString);
    }

    private AcmSubscription prepareSubscriptionObject(String userId, Long objectId, String objectType, JSONObject solrResponse)
            throws AcmObjectNotFoundException
    {
        JSONObject responseBody = solrResponse.getJSONObject(SOLR_RESPONSE_BODY);
        JSONArray docsList = responseBody.getJSONArray(SOLR_RESPONSE_DOCS);

        if (docsList.length() == 0)
        {
            log.error("No such object to subscribe to: [{}-{}]", objectId, objectType);
            throw new AcmObjectNotFoundException(objectType, objectId, "no such object to subscribe to", null);
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

    public SubscriptionEventPublisher getSubscriptionEventPublisher()
    {
        return subscriptionEventPublisher;
    }

    public void setSubscriptionEventPublisher(SubscriptionEventPublisher subscriptionEventPublisher)
    {
        this.subscriptionEventPublisher = subscriptionEventPublisher;
    }

    public SubscriptionConfig getSubscriptionConfig()
    {
        return subscriptionConfig;
    }

    public void setSubscriptionConfig(SubscriptionConfig subscriptionConfig)
    {
        this.subscriptionConfig = subscriptionConfig;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
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
