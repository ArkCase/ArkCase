package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.casefile.model.CaseByStatusDto;
import com.armedia.acm.plugins.casefile.model.CasesByStatusAndTimePeriod;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.SearchResults;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by marjan.stefanoski on 9/3/2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/casebystatus", "/api/latest/plugin/casebystatus"})
public class GetCasesByStatusAPIController
{

    /**
     * Logger instance.
     */
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Solr query execution utility.
     */
    private ExecuteSolrQuery executeSolrQuery;

    /**
     * REST api for retrieving active case files by status
     *
     * @param authentication
     * @return
     */
    @RequestMapping(
            value = "/{timePeriod}",
            method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public List<CaseByStatusDto> getCasesByStatus(
            @PathVariable("timePeriod") String timePeriod,
            Authentication authentication
    ) throws AcmListObjectsFailedException
    {
        log.info("Getting cases grouped by status in a time period");

        List<Object> facet = retrieveFacetValues(authentication, CasesByStatusAndTimePeriod.getTimePeriod(timePeriod));

        List<CaseByStatusDto> casesByStatus = new ArrayList<>();
        Iterator<Object> iterator = facet.iterator();
        // we are sure we have (status, count) pairs in a list so it's safe to do 2x iterator.next() in a single cycle
        while (iterator.hasNext())
        {
            CaseByStatusDto caseByStatusDto = new CaseByStatusDto();
            caseByStatusDto.setStatus((String) iterator.next());
            caseByStatusDto.setCount((Integer) iterator.next());
            casesByStatus.add(caseByStatusDto);
        }

        return casesByStatus;
    }

    /**
     * This method will return facet results in the list. The result is taken from the Solr
     *
     * @param authentication - authentication object
     * @return - list of facet results
     */
    private List<Object> retrieveFacetValues(Authentication authentication, CasesByStatusAndTimePeriod casesByStatusAndTimePeriod)
    {
        final List<Object> facetValues = new ArrayList<>();

        // Take response from solr
        String solrFacetResponse = getSolrFacetResponse(authentication, casesByStatusAndTimePeriod);
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
     * @param authentication - authentication object
     * @return - Solr response in string representation
     */
    private String getSolrFacetResponse(Authentication authentication, CasesByStatusAndTimePeriod casesByStatusAndTimePeriod)
    {
        String solrResponse = null;
        String facetQuery = "object_type_s:CASE_FILE";

        // filter by modified date
        switch (casesByStatusAndTimePeriod)
        {
            case LAST_WEEK:
                facetQuery += "+AND+last_modified_tdt:[NOW-7DAYS TO *]";
                break;
            case LAST_MONTH:
                facetQuery += "+AND+last_modified_tdt:[NOW-1MONTH TO *]";
                break;
            case LAST_YEAR:
                facetQuery += "+AND+last_modified_tdt:[NOW-1YEAR TO *]";
                break;
            case ALL:
                // no filtering by modified date
                break;
        }

        facetQuery += "&rows=0&fl=id&wt=json&indent=true&facet=true&facet.mincount=1&facet.field=" + SearchConstants.PROPERTY_STATUS;

        try
        {
            solrResponse = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.QUICK_SEARCH, facetQuery, 0, 1, "");
        } catch (MuleException e)
        {
            log.error("Error while executing Solr query: {}", facetQuery, e);
        }

        return solrResponse;
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
