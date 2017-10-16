package com.armedia.acm.services.search.service;

import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.services.search.model.SearchConstants;
import org.junit.Before;
import org.junit.Test;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by dmiller on 2/23/16.
 */
public class FacetedSearchServiceTest
{
    private FacetedSearchService unit;

    @Before
    public void setUp() throws Exception
    {
        unit = new FacetedSearchService();

        // faceted search requires certain properties to exist.
        unit.setPluginSearch(new AcmPlugin());

        Map<String, Object> p = new HashMap<>();
        unit.getPluginSearch().setPluginProperties(p);

        // this property copied straight from searchPlugin.properties
        p.put("search.time.period", "[{\"desc\": \"Previous Week\", \"value\":\"[NOW/DAY-7DAY TO *]\"}]");

        p.put("objects.to.exclude", "BAND,AUTHOR");
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
        String updatedQuery = unit.updateQueryWithExcludedObjects(query, URLEncoder.encode("{!field f=object_type_facet}BAND", SearchConstants.FACETED_SEARCH_ENCODING));

        assertEquals("ann.* AND -object_type_s:AUTHOR", updatedQuery);
        
        query = "ann.*";
        updatedQuery = unit.updateQueryWithExcludedObjects(query, URLEncoder.encode("{!field f=object_type_s}BAND", SearchConstants.FACETED_SEARCH_ENCODING));

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
        unit.getPluginSearch().getPluginProperties().put("facet.my_facet_lcs", "Example Facet");

        String filter = "\"Example Facet\":1401";
        String query = "&fq=" + URLEncoder.encode("{!field f=my_facet_lcs}1401", SearchConstants.FACETED_SEARCH_ENCODING);

        String found = unit.buildSolrQuery(filter);

        assertEquals(query, found);
    }

    @Test
    public void getFacetKeys() throws Exception
    {
        unit.getPluginSearch().getPluginProperties().put("facet.first_name_s", "First Name");
        unit.getPluginSearch().getPluginProperties().put("facet.last_name_s", "Last Name");
        unit.getPluginSearch().getPluginProperties().put("facet.date.birth_date_tdt", "Birth Date");

        String expected = "facet.query=" +
                URLEncoder.encode("{!key='Birth Date, Previous Week'}birth_date_tdt", SearchConstants.FACETED_SEARCH_ENCODING) +
                ":" + URLEncoder.encode("[NOW/DAY-7DAY TO *]", SearchConstants.FACETED_SEARCH_ENCODING) +
                "&facet.field=" +
                URLEncoder.encode("{!key='First Name'}first_name_s", SearchConstants.FACETED_SEARCH_ENCODING) +
                "&facet.field=" +
                URLEncoder.encode("{!key='Last Name'}last_name_s", SearchConstants.FACETED_SEARCH_ENCODING);
        String found = unit.getFacetKeys();

        assertEquals(expected, found);
    }


}
