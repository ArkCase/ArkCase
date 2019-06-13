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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by marjan.stefanoski on 21.11.2014.
 */

@Controller
@RequestMapping({ "/api/v1/plugin/search", "/api/latest/plugin/search" })
public class CaseFilesSearchByDueDateTimeIntervalAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/caseFilesSearch/byTimeInterval", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

    @ResponseBody
    public String caseFiles(@RequestParam(value = "timePeriod", required = true) String timePeriod,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "5") int maxRows, Authentication authentication,
            HttpServletResponse httpResponse) throws MuleException, UnsupportedEncodingException
    {

        if ("all".equals(timePeriod))
        {
            log.debug("User '{}' is searching for all Case Files grouped by status", authentication.getName());
        }
        log.debug("User '{}' is searching for all Case Files grouped by status with dueDate the last:'{}' ", authentication.getName(),
                timePeriod);

        String query = "object_type_s:CASE_FILE";
        String sort = "AND dueDate_tdt ASC";

        switch (TimePeriodForSearch.getTimePeriod(timePeriod))
        {
        case ALL:
            break;
        case LAST_MONTH:
            query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1MONTH TO *]", "UTF-8");
            break;
        case LAST_YEAR:
            query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-1YEAR TO *]", "UTF-8");
            break;
        case LAST_WEEK:
            query += URLEncoder.encode("AND dueDate_tdt:[NOW/DAY-7DAY TO *]", "UTF-8");
            break;
        default:
            break;
        }

        query += " AND group=true AND group.field=status_lcs AND group.limit=0";
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
