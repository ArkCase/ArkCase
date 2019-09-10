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

import com.armedia.acm.services.search.model.ReportGenerator;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.armedia.acm.services.search.service.FacetedSearchService;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 17.12.2014.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/search" })
public class FacetedSearchAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());

    private SpringContextHolder springContextHolder;

    private ExecuteSolrQuery executeSolrQuery;
    private FacetedSearchService facetedSearchService;

    // For EXPORT, set parameter export =(eg. 'csv') & fields = [fields that should be exported]!
    @RequestMapping(value = "/facetedSearch", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String mainNotFilteredFacetedSearch(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "500") int maxRows,
            @RequestParam(value = "filters", required = false, defaultValue = "") String[] filters,
            @RequestParam(value = "join", required = false, defaultValue = "") String joinQuery,
            @RequestParam(value = "s", required = false, defaultValue = "create_date_tdt DESC") String sortSpec,
            @RequestParam(value = "fields", required = false, defaultValue = "parent_number_lcs, parent_type_s, modified_date_tdt") String[] exportFields,
            @RequestParam(value = "export", required = false) String export,
            @RequestParam(value = "reportName", required = false, defaultValue = "report") String reportName,
            @RequestParam(value = "titles", required = false) String[] exportTitles,
            // Part of the query to NOT ESCAPE
            @RequestParam(value = "unescapedQuery", required = false, defaultValue = "") String unescapedQuery,
            HttpServletResponse response,
            Authentication authentication) throws MuleException, UnsupportedEncodingException
    {
        log.debug("User '" + authentication.getName() + "' is performing facet search for the query: '" + q + "' ");

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

        if (StringUtils.isNotEmpty(q))
        {
            q = URLDecoder.decode(q, SearchConstants.FACETED_SEARCH_ENCODING);
            q = getFacetedSearchService().escapeQueryChars(q);
        }

        // Needed workaround for BACTES, since there needs to be exceptions the special characters that get escaped
        if (StringUtils.isNotEmpty(unescapedQuery))
        {
            unescapedQuery = URLDecoder.decode(unescapedQuery, SearchConstants.FACETED_SEARCH_ENCODING);
            q = (q == null ? unescapedQuery : q + unescapedQuery);
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
                export(generator, content, response, reportName);
            }
            catch (NoSuchBeanDefinitionException e)
            {
                log.error(String.format("Bean of type: %sReportGenerator is not defined", export.toLowerCase()));
                throw new IllegalStateException(String.format("Can not export to %s!", export));
            }

            // The output stream is already closed as report is exported
            return "";
        }
        return res;
    }

    public void export(ReportGenerator generator, String content, HttpServletResponse response, String reportName)
    {
        try (PrintWriter writer = response.getWriter())
        {
            response.setContentType(generator.getReportContentType());
            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=\"%s\"", generator.generateReportName(reportName)));
            response.setContentLength(content.length());
            writer.write(URLEncoder.encode(content, "UTF-8"));
        }
        catch (IOException e)
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
