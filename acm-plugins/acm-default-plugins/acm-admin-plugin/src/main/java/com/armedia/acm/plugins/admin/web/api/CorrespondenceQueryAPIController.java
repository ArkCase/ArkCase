package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.correspondence.model.CorrespondenceQuery;
import com.armedia.acm.correspondence.model.QueryType;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.admin.exception.CorrespondenceQueryNotFoundException;
import com.armedia.acm.plugins.admin.model.CorrespondenceQueryResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jan 25, 2017
 */
@Controller
@RequestMapping({ "/api/v1/plugin/admin", "/api/latest/plugin/admin" })
public class CorrespondenceQueryAPIController
{

    private CorrespondenceService correspondenceService;

    @RequestMapping(value = "/queries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceQueryResponse> getAllQueries()
    {
        return generateResponse(correspondenceService.getAllQueries());
    }

    @RequestMapping(value = "/queries/{queryType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CorrespondenceQueryResponse> getQueriesByType(@PathVariable(value = "queryType") QueryType queryType)
    {
        return generateResponse(correspondenceService.getQueriesByType(queryType));
    }

    @RequestMapping(value = "/queries/query/{queryBeanId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CorrespondenceQueryResponse getQueryByBeanId(@PathVariable(value = "queryBeanId") String queryBeanId)
    {
        return new CorrespondenceQueryResponse(queryBeanId,
                correspondenceService.getQueryByBeanId(queryBeanId).orElseThrow(CorrespondenceQueryNotFoundException::new));
    }

    @ExceptionHandler(CorrespondenceQueryNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Error while retreiving correspondence query.")
    public void handleException()
    {
    }

    /**
     * @param queries
     * @return
     */
    private List<CorrespondenceQueryResponse> generateResponse(Map<String, CorrespondenceQuery> queries)
    {
        return queries.entrySet().stream().map(entry -> new CorrespondenceQueryResponse(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * @param correspondenceService
     *            the correspondenceService to set
     */
    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

}
