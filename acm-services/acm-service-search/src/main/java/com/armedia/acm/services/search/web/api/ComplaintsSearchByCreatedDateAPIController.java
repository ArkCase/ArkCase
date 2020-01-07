package com.armedia.acm.services.search.web.api;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.model.TimePeriodForSearch;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by marjan.stefanoski on 24.11.2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class ComplaintsSearchByCreatedDateAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/complaintsSearch/byTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public String complaints(@RequestParam(value = "timePeriod", required = false, defaultValue = "all") String timePeriod,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows, Authentication authentication,
            HttpServletResponse httpResponse) throws MuleException
    {

        if ("all".equals(timePeriod))
        {
            log.debug("User '{}' is searching for all Case Files grouped by status", authentication.getName());
        }
        log.debug("User '{}' is searching for all Case Files grouped by status with dueDate the last:'{}' ", authentication.getName(),
                timePeriod);

        String query = "object_type_s:COMPLAINT";// AND creator_lcs:" + URLEncoder.encode(userId);
        String sort = "create_date_tdt ASC";

        switch (TimePeriodForSearch.getTimePeriod(timePeriod))
        {
        case ALL:
            break;
        case LAST_MONTH:
            query += "AND create_date_tdt:[NOW/DAY-1MONTH TO *]";
            break;
        case LAST_YEAR:
            query += "AND create_date_tdt:[NOW/DAY-1YEAR TO *]";
            break;
        case LAST_WEEK:
            query += "AND create_date_tdt:[NOW/DAY-7DAY TO *]";
            break;
        default:
            break;
        }

        query = query.replaceAll(" ", "+");
        sort = sort.replaceAll(" ", "+");

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, query, startRow,
                maxRows, sort, false);

        httpResponse.addHeader("X-JSON", results);

        return results;

    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
