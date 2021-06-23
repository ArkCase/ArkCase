package com.armedia.acm.plugins.consultation.web.api;

/*-
 * #%L
 * ACM Default Plugin: Consultation
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.consultation.model.ConsultationSummaryByStatusAndTimePeriodDto;
import com.armedia.acm.plugins.consultation.model.ConsultationsByStatusAndTimePeriod;
import com.armedia.acm.plugins.consultation.model.ConsultationsByStatusDto;
import com.armedia.acm.plugins.consultation.model.TimePeriod;
import com.armedia.acm.plugins.consultation.service.ConsultationService;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on May, 2020
 */
@Controller
@RequestMapping({ "/api/v1/plugin/consultation/bystatus", "/api/latest/plugin/consultation/bystatus" })
public class GetConsultationByStatusAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private ConsultationService consultationService;
    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(value = "/summary", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<ConsultationSummaryByStatusAndTimePeriodDto> getConsultationsSummaryByStatusAndTimePeriod(
            Authentication authentication) throws AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Getting consultations grouped by status in a different time periods");
        }
        List<ConsultationSummaryByStatusAndTimePeriodDto> retval = getConsultationSummary();
        return retval;
    }

    /**
     * REST api for retrieving consultations by status.
     *
     * @param authentication
     * @return
     */
    @RequestMapping(value = "/{timePeriod}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public List<ConsultationsByStatusDto> getConsultationsByStatus(
            @PathVariable("timePeriod") String timePeriod,
            Authentication authentication) throws AcmListObjectsFailedException
    {
        log.info("Getting consultations grouped by status in a time period");

        List<Object> facet = retrieveFacetValues(authentication, ConsultationsByStatusAndTimePeriod.getTimePeriod(timePeriod));

        List<ConsultationsByStatusDto> consultationsByStatusDtos = new ArrayList<>();
        Iterator<Object> iterator = facet.iterator();
        // we are sure we have (status, count) pairs in a list so it's safe to do 2x iterator.next() in a single cycle
        while (iterator.hasNext())
        {
            ConsultationsByStatusDto consultationsByStatusDto = new ConsultationsByStatusDto();
            consultationsByStatusDto.setStatus((String) iterator.next());
            consultationsByStatusDto.setCount((Integer) iterator.next());
            consultationsByStatusDtos.add(consultationsByStatusDto);
        }

        return consultationsByStatusDtos;
    }

    private List<ConsultationSummaryByStatusAndTimePeriodDto> getConsultationSummary()
    {
        List<ConsultationSummaryByStatusAndTimePeriodDto> consultationSummaryByStatusAndTimePeriodDtos = new ArrayList<>();
        ConsultationSummaryByStatusAndTimePeriodDto consultationSummaryByStatusAndTimePeriodDto = new ConsultationSummaryByStatusAndTimePeriodDto();
        for (TimePeriod tp : TimePeriod.values())
        {
            consultationSummaryByStatusAndTimePeriodDto.setTimePeriod(tp.getnDays());
            consultationSummaryByStatusAndTimePeriodDto
                    .setConsultationsByStatusDtos(getConsultationService().getConsultationsByStatusAndByTimePeriod(tp));

            consultationSummaryByStatusAndTimePeriodDtos.add(consultationSummaryByStatusAndTimePeriodDto);
            consultationSummaryByStatusAndTimePeriodDto = new ConsultationSummaryByStatusAndTimePeriodDto();
        }

        return consultationSummaryByStatusAndTimePeriodDtos;
    }

    /**
     * This method will return facet results in the list. The result is taken from the Solr
     *
     * @param authentication
     *            - authentication object
     * @return - list of facet results
     */
    private List<Object> retrieveFacetValues(Authentication authentication,
            ConsultationsByStatusAndTimePeriod consultationsByStatusAndTimePeriod)
    {
        final List<Object> facetValues = new ArrayList<>();

        // Take response from solr
        String solrFacetResponse = getSolrFacetResponse(authentication, consultationsByStatusAndTimePeriod);
        if (solrFacetResponse != null)
        {
            // Get facet search values
            SearchResults searchResults = new SearchResults();
            JSONObject facetFields = searchResults.getFacetFields(solrFacetResponse);
            if (facetFields != null)
            {
                // Add all results in the list
                facetValues.addAll(searchResults.extractObjectList(facetFields, SearchConstants.PROPERTY_STATUS));
            }
        }

        return facetValues;
    }

    /**
     * This method will return Solr response as String for facet search
     *
     * @param authentication
     *            - authentication object
     * @return - Solr response in string representation
     */
    private String getSolrFacetResponse(Authentication authentication,
            ConsultationsByStatusAndTimePeriod consultationsByStatusAndTimePeriod)
    {
        String solrResponse = null;
        String facetQuery = "object_type_s:CONSULTATION";

        // filter by modified date
        switch (consultationsByStatusAndTimePeriod)
        {
        case LAST_WEEK:
            facetQuery += "+AND+modified_date_tdt:[NOW-7DAYS TO *]";
            break;
        case LAST_MONTH:
            facetQuery += "+AND+modified_date_tdt:[NOW-1MONTH TO *]";
            break;
        case LAST_YEAR:
            facetQuery += "+AND+modified_date_tdt:[NOW-1YEAR TO *]";
            break;
        case ALL:
            // no filtering by modified date
            break;
        }

        facetQuery += "&rows=0&fl=id&wt=json&indent=true&facet=true&facet.mincount=1&facet.field=" + SearchConstants.PROPERTY_STATUS;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH, facetQuery, 0, 1, "");
        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", facetQuery, e);
        }

        return solrResponse;
    }

    public ConsultationService getConsultationService()
    {
        return consultationService;
    }

    public void setConsultationService(ConsultationService consultationService)
    {
        this.consultationService = consultationService;
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
