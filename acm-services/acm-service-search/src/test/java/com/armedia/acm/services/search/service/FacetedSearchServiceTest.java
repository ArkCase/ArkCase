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


}
