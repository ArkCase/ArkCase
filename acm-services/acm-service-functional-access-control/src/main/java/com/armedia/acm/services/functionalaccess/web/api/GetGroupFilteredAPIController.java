package com.armedia.acm.services.functionalaccess.web.api;

/*-
 * #%L
 * ACM Service: Functional Access Control
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

import com.armedia.acm.services.functionalaccess.service.FunctionalAccessService;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by maksud.sharif on 11/17/2016.
 */
@Controller
@RequestMapping({ "/api/v1/service/functionalaccess/groups", "/api/latest/service/functionalaccess/groups" })
public class GetGroupFilteredAPIController
{
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private ExecuteSolrQuery executeSolrQuery;
    private FunctionalAccessService functionalAccessService;

    @RequestMapping(value = "/toplevel", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getTopLevelGroups(@RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "s", required = false, defaultValue = "") String sort,
            @RequestParam(value = "groupSubtype", required = false, defaultValue = "") List<String> groupSubtype,
            @RequestParam(value = "roleFilters", required = false, defaultValue = "") List<String> roleFilters,
            Authentication auth) throws Exception
    {
        LOG.info("Taking all top level groups from Solr.");

        String query = "object_type_s:GROUP AND -parent_id_s:* AND -status_lcs:COMPLETE AND -status_lcs:DELETE AND -status_lcs:INACTIVE AND -status_lcs:CLOSED";

        String rowQueryParameters = "fq=hidden_b:false";
        if (groupSubtype != null && !groupSubtype.isEmpty())
        {
            rowQueryParameters += "&fq=object_sub_type_s:(" + String.join(" OR ", groupSubtype) + ")";
        }

        if (roleFilters != null && !roleFilters.isEmpty())
        {
            Map<String, List<String>> roleToGroups = getFunctionalAccessService().getApplicationRolesToGroups();
            List<String> roleFilterGroups = roleFilters.stream()
                    .filter(roleToGroups::containsKey)
                    .filter(role -> !roleToGroups.get(role).isEmpty())
                    .map(roleToGroups::get)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            if (!roleFilterGroups.isEmpty())
            {
                rowQueryParameters += "&fq=object_id_s:(" + String.join(" OR ", roleFilterGroups) + ")";
            }
        }

        LOG.debug("User [{}] is searching for [{}]", auth.getName(), query);

        return getExecuteSolrQuery().getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, startRow, maxRows, sort,
                rowQueryParameters);
    }

    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

    public FunctionalAccessService getFunctionalAccessService()
    {
        return functionalAccessService;
    }

    public void setFunctionalAccessService(FunctionalAccessService functionalAccessService)
    {
        this.functionalAccessService = functionalAccessService;
    }
}
