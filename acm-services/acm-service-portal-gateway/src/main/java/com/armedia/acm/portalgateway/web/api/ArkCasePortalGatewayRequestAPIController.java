package com.armedia.acm.portalgateway.web.api;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

import com.armedia.acm.portalgateway.service.CheckPortalUserAssignement;
import com.armedia.acm.portalgateway.service.PortalId;
import com.armedia.acm.portalgateway.service.PortalRequestService;
import com.armedia.acm.portalgateway.service.PortalRequestServiceException;
import com.armedia.acm.portalgateway.service.PortalServiceExceptionMapper;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.FacetedSearchService;
import com.armedia.acm.services.search.service.SearchResults;
import com.armedia.acm.services.users.dao.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/service/portalgateway", "/api/latest/service/portalgateway" })
public class ArkCasePortalGatewayRequestAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalRequestService portalRequestService;

    private ExecuteSolrQuery executeSolrQuery;

    private FacetedSearchService facetedSearchService;

    private UserDao userDao;

    @Value("${portal.serviceProvider.directory.name}")
    private String directoryName;

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalId}/requests", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalResponse submitRequest(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @RequestBody PortalRequest request) throws PortalRequestServiceException
    {
        log.debug("Submitting request from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, auth.getName(),
                request.getRequestType());
        return portalRequestService.submitRequest(portalId, auth.getName(), request);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalId}/requests/{portalUserId}/{requestType:.+}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public List<PortalResponse> listRequests(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "portalUserId") String portalUserId, @PathVariable(value = "requestType") String requestType)
            throws PortalRequestServiceException
    {
        log.debug("Listing requests from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                requestType);
        return portalRequestService.listRequests(portalId, portalUserId, requestType);
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalId}/requests/{portalUserId}/{requestType}/{requestId}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public PortalResponse getRequestStatus(Authentication auth, @PortalId @PathVariable(value = "portalId") String portalId,
            @PathVariable(value = "portalUserId") String portalUserId, @PathVariable(value = "requestType") String requestType,
            @PathVariable(value = "requestId") String requestId)
            throws PortalRequestServiceException
    {
        return portalRequestService.getRequestStatus(portalId, portalUserId, requestType, requestId);
    }

    @RequestMapping(value = "/inquiry", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE,
            MediaType.TEXT_PLAIN_VALUE })
    @ResponseBody
    public String submitInquiry(Authentication auth, @RequestBody String inquiry)
    {
        log.debug("Submitting request from portal for portal user with [{}] ID of [{}] type.", auth.getName(),
                inquiry);
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            PortalRequest portalRequest = mapper.readValue(inquiry, PortalRequest.class);
            portalRequestService.submitInquiry(portalRequest);
            return "{\"success\":true}";
        }
        catch (PortalRequestServiceException | IOException e)
        {
            return "{\"success\":false}";
        }
    }

    @CheckPortalUserAssignement
    @RequestMapping(value = "/{portalId}/requests/suggestRequests", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<String> suggestRequests(@PortalId @PathVariable(value = "portalId") String portalId,
            @RequestParam(value = "q") String query,
            @RequestParam(value = "emailAddress") String emailAddress,
            Authentication authentication) throws SolrException
    {
        String filterQueries = "";
        String[] filter = new String[] { "object_type_s:CASE_FILE", "requester_email_s:" + emailAddress };
        filterQueries = Arrays.asList(filter).stream().map(f -> getFacetedSearchService().buildSolrQuery(f))
                .collect(Collectors.joining(""));
        filterQueries += filterQueries.trim().length() > 0 ? "&fq=hidden_b:false" : "fq=hidden_b:false";

        query = String.format("name:%s*", query);
        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, 0,
                10, "",
                filterQueries);

        List<String> caseNames = new ArrayList<>();

        SearchResults searchResults = new SearchResults();
        JSONArray docFiles = searchResults.getDocuments(results);

        for (int i = 0; i < docFiles.length(); i++)
        {
            JSONObject docFile = docFiles.getJSONObject(i);
            caseNames.add(docFile.getString("name"));
        }
        return caseNames;
    }

    @ExceptionHandler(PortalRequestServiceException.class)
    @ResponseBody
    public ResponseEntity<?> handleRequestException(PortalRequestServiceException se)
    {
        log.warn("Handling exception of [{}] type.", se.getClass().getName());
        PortalServiceExceptionMapper exceptionMapper = portalRequestService.getExceptionMapper(se);
        Object errorDetails = exceptionMapper.mapException();
        return ResponseEntity.status(exceptionMapper.getStatusCode()).body(errorDetails);
    }

    /**
     * @param portalRequestService
     *            the portalRequestService to set
     */
    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public FacetedSearchService getFacetedSearchService()
    {
        return facetedSearchService;
    }

    public void setFacetedSearchService(FacetedSearchService facetedSearchService)
    {
        this.facetedSearchService = facetedSearchService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }
}
