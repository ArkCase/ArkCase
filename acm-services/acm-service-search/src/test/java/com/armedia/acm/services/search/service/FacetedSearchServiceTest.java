package com.armedia.acm.services.search.service;

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

import static org.junit.Assert.assertEquals;

import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.services.search.model.solr.SearchConfig;

import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;

/**
 * Created by dmiller on 2/23/16.
 */
public class FacetedSearchServiceTest
{
    private FacetedSearchService unit;
    private SearchConfig searchConfig;

    @Before
    public void setUp() throws Exception
    {
        unit = new FacetedSearchService();
        searchConfig = new SearchConfig();
        searchConfig.setTimePeriod("[{\"desc\": \"Previous Week\", \"value\":\"[NOW/DAY-7DAY TO *]\"}]");
        searchConfig.setObjectsToExclude("BAND,AUTHOR");
        unit.setSearchConfig(searchConfig);
    }

    @Test
    public void updateQueryWithExcludedObjects_excludeByDefault()
    {
        String query = "ann.*";
        String updatedQuery = unit.updateQueryWithExcludedObjects(query, "filter=fq=myfield:myvalue");

        assertEquals("ann.* AND -object_type_s:BAND AND -object_type_s:AUTHOR", updatedQuery);
    }

    @Test
    public void updateQueryWithExcludedObjects_includeIfSpecificallyRequested() throws Exception
    {
        String query = "ann.*";
        String updatedQuery = unit.updateQueryWithExcludedObjects(query,
                URLEncoder.encode("{!field f=object_type_facet}BAND", SearchConstants.FACETED_SEARCH_ENCODING));

        assertEquals("ann.* AND -object_type_s:AUTHOR", updatedQuery);

        query = "ann.*";
        updatedQuery = unit.updateQueryWithExcludedObjects(query,
                URLEncoder.encode("{!field f=object_type_s}BAND", SearchConstants.FACETED_SEARCH_ENCODING));

        assertEquals("ann.* AND -object_type_s:AUTHOR", updatedQuery);
    }

    @Test
    public void buildSolrQuery_nonFacetFilterShouldBecomeFqFilter() throws Exception
    {
        String filter = "object_id_s:1401";
        String query = "&fq=" + filter;

        String found = unit.buildSolrQuery(filter);

        assertEquals(query, found);
    }

    @Test
    public void buildSolrQuery_facetFilterShouldHaveFieldNameFromProperties() throws Exception
    {
        searchConfig.getFacets().put("my_facet_lcs", "Example Facet");
        String filter = "\"Example Facet\":1401";
        String query = "&fq=" + URLEncoder.encode("{!field f=my_facet_lcs}1401", SearchConstants.FACETED_SEARCH_ENCODING);

        String found = unit.buildSolrQuery(filter);

        assertEquals(query, found);
    }

    @Test
    public void getFacetKeys() throws Exception
    {
        searchConfig.getFacets().put("first_name_s", "First Name");
        searchConfig.getFacets().put("last_name_s", "Last Name");
        searchConfig.getFacets().put("date.birth_date_tdt", "Birth Date");
        String expected = "facet.field=" +
                URLEncoder.encode("{!key='First Name'}first_name_s", SearchConstants.FACETED_SEARCH_ENCODING) +
                "&facet.query=" +
                URLEncoder.encode("{!key='Birth Date, Previous Week'}birth_date_tdt", SearchConstants.FACETED_SEARCH_ENCODING) +
                ":" + URLEncoder.encode("[NOW/DAY-7DAY TO *]", SearchConstants.FACETED_SEARCH_ENCODING) +
                "&facet.field=" +
                URLEncoder.encode("{!key='Last Name'}last_name_s", SearchConstants.FACETED_SEARCH_ENCODING);
        String found = unit.getFacetKeys();

        assertEquals(expected, found);
    }

    @Test
    public void escapeTermsInQuery()
    {
        assertEquals("\"term1\" \"term2\" \"quoted term3\" \"term4!@#$%()\"",
                unit.escapeTermsInQuery("term1 term2 \"quoted term3\" term4!@#$%()"));
        // if term containing quotes inside term should be split as separate term
        assertEquals("\"term1\" \"term2\" \"quoted term3\" \"term4!@\"#\"$%()\"",
                unit.escapeTermsInQuery("term1 term2 \"quoted term3\" term4!@\"#\"$%()"));
        // check for edge situations
        assertEquals("", unit.escapeTermsInQuery(null));
        assertEquals("", unit.escapeTermsInQuery(""));
        assertEquals("", unit.escapeTermsInQuery("   "));
    }
}
