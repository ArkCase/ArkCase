package com.armedia.acm.services.search.web.api;

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.FacetedSearchService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 * refactored by nebojsha.davidovikj on 02.24.2018
 */
@Controller
@RequestMapping({ "/api/v2/plugin/search", "/api/latest/plugin/search" })
public class FacetedSearchAPIControllerV2
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private SpringContextHolder springContextHolder;

    private ExecuteSolrQuery executeSolrQuery;
    private FacetedSearchService facetedSearchService;

    // For EXPORT, set parameter export =(eg. 'csv') & fields = [fields that should be exported]!
    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> mainNotFilteredFacetedSearch(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue = "") String[] filters,
            @RequestParam(value = "join", required = false, defaultValue = "") String joinQuery,
            @RequestParam(value = "s", required = false, defaultValue = "score DESC") String sortSpec,
            @RequestParam(value = "fields", required = false, defaultValue = "parent_number_lcs, parent_type_s, modified_date_tdt") String[] exportFields,
            @RequestParam(value = "export", required = false) String export,
            @RequestParam(value = "reportName", required = false, defaultValue = "report") String reportName,
            @RequestParam(value = "titles", required = false) String[] exportTitles,
            Authentication authentication) throws MuleException, UnsupportedEncodingException
    {
        log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");
        MultiValueMap<String, String> headers = new HttpHeaders();

        // decode query
        q = StringUtils.isNotEmpty(q) ? URLDecoder.decode(q, SearchConstants.FACETED_SEARCH_ENCODING) : q;
        String facetKeys = getFacetedSearchService().getFacetKeys();
        // true if the filter should check for parent object access
        boolean isParentRef = true;
        boolean isSubscriptionSearch = false;
        String filterQueries = "";
        if (filters != null)
        {
            filterQueries = Arrays.asList(filters).stream().map(f -> getFacetedSearchService().buildSolrQuery(f))
                    .collect(Collectors.joining("&"));
            for (String filter : filters)
            {
                String decodedFilter = URLDecoder.decode(filter, SearchConstants.FACETED_SEARCH_ENCODING);
                decodedFilter = decodedFilter.replaceAll("\\s+", "");

                isSubscriptionSearch = decodedFilter.contains("\"ObjectType\":SUBSCRIPTION_EVENT");

                // do not check for parent-object access for NOTIFICATION and SUBSCRIPTION_EVENT object types
                isParentRef = isParentRef && !decodedFilter.contains("\"ObjectType\":NOTIFICATION");
                isParentRef = isParentRef && !isSubscriptionSearch;
            }
        }

        String rowQueryParameters = facetKeys + filterQueries;
        String sort = sortSpec == null ? "" : sortSpec.trim();

        String query = getFacetedSearchService().escapeTermsInQuery(q);

        query = getFacetedSearchService().updateQueryWithExcludedObjects(query, rowQueryParameters);
        query += getFacetedSearchService().buildHiddenDocumentsFilter();
        query = URLEncoder.encode(query, SearchConstants.FACETED_SEARCH_ENCODING);

        rowQueryParameters += SearchConstants.SOLR_FILTER_QUERY_ATTRIBUTE_NAME + joinQuery;

        if (StringUtils.isNotEmpty(export))
        {
            startRow = 0;
            maxRows = SearchConstants.MAX_RESULT_ROWS;
        }

        String results = getExecuteSolrQuery().getResultsByPredefinedQuery(authentication, SolrCore.ADVANCED_SEARCH,
                query, startRow, maxRows, sort, true, rowQueryParameters, isParentRef, isSubscriptionSearch, SearchConstants.DEFAULT_FIELD);
        String res = getFacetedSearchService().replaceEventTypeName(results);

        if (StringUtils.isNotEmpty(export))
        {
            if (null != exportTitles)
            {
                for (int i = 0; i < exportTitles.length; i++)
                {
                    exportTitles[i] = new String(exportTitles[i].getBytes("ISO-8859-1"), "UTF-8");
                }
            }
            else
            {
                exportTitles = exportFields;
            }

            try
            {
                // Get the appropriate generator for the requested file type
                ReportGenerator generator = springContextHolder.getBeanByName(String.format("%sReportGenerator",
                        export.toLowerCase()), ReportGenerator.class);
                String content = generator.generateReport(exportFields, exportTitles, res);
                headers.add("Content-Type", generator.getReportContentType());
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", generator.generateReportName(reportName)));

                res = content;
            }
            catch (NoSuchBeanDefinitionException e)
            {
                log.error(String.format("Bean of type: %sReportGenerator is not defined", export.toLowerCase()));
                throw new IllegalStateException(String.format("Can not export to %s!", export));
            }
        }

        return new ResponseEntity<>(res, headers, HttpStatus.OK);
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

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
