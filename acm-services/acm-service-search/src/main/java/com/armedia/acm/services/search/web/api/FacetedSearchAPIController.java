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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping({"/api/v1/plugin/search", "/api/latest/plugin/search"})
public class FacetedSearchAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private SpringContextHolder springContextHolder;

    private ExecuteSolrQuery executeSolrQuery;
    private FacetedSearchService facetedSearchService;

    // For EXPORT, set parameter export =(eg. 'csv') & fields = [fields that should be exported]!
    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSearch(
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue = "") String[] filters,
            @RequestParam(value = "join", required = false, defaultValue = "") String joinQuery,
            @RequestParam(value = "s", required = false, defaultValue = "create_date_tdt DESC") String sortSpec,
            @RequestParam(value = "fields", required = false,
                    defaultValue = "parent_number_lcs, parent_type_s, modified_date_tdt") String[] exportFields,
            @RequestParam(value = "export", required = false) String export,
            @RequestParam(value = "reportName", required = false, defaultValue = "report") String reportName,
            HttpServletResponse response,
            Authentication authentication
    ) throws MuleException, UnsupportedEncodingException
    {
        log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");

        String facetKeys = getFacetedSearchService().getFacetKeys();

        // true if the filter should check for parent object access
        boolean isParentRef = true;
        boolean isSubscriptionSearch = false;
        String filterQueries = "";
        if (filters != null)
        {
            filterQueries = Arrays.asList(filters).stream().map(f -> getFacetedSearchService().buildSolrQuery(f)).collect(Collectors.joining("&"));
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

        // if the query ends in a *, it has to be quoted, or Solr will not find anything somehow.
        if (q.endsWith("*"))
        {
            q = "\"" + q + "\"";
        }

        String query = SearchConstants.CATCH_ALL_QUERY + q;

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
                query, startRow, maxRows, sort, rowQueryParameters, isParentRef, isSubscriptionSearch);
        String res = getFacetedSearchService().replaceEventTypeName(results);

        if (StringUtils.isNotEmpty(export))
        {
            try
            {
                // Get the appropriate generator for the requested file type
                ReportGenerator generator = (ReportGenerator) springContextHolder.getBeanByName(String.format("%sReportGenerator",
                        export.toLowerCase()), ReportGenerator.class);
                byte[] output = generator.generateReport(exportFields, res);
                export(generator, output, response, reportName);
            } catch (NoSuchBeanDefinitionException e)
            {
                log.error(String.format("Bean of type: %sReportGenerator is not defined", export.toLowerCase()));
                throw new IllegalStateException(String.format("Can not export to %s!", export));
            }

            // The output stream is already closed as report is exported
            return "";
        }
        return res;
    }

    public void export(ReportGenerator generator, byte[] bytes, HttpServletResponse response, String reportName)
    {
        try (OutputStream outputStream = response.getOutputStream())
        {
            response.setContentType(generator.getReportContentType());
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", generator.generateReportName(reportName)));
            response.setContentLength(bytes.length);
            outputStream.write(bytes);
        } catch (IOException e)
        {
            log.error("Unable to generate report document. Exception msg: '{}'", e.getMessage());
        }
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
