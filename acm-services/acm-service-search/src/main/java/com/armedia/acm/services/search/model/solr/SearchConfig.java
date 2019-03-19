package com.armedia.acm.services.search.model.solr;

/*-
 * #%L
 * ACM Service: Search
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class SearchConfig implements InitializingBean
{
    @JsonProperty("search.plugin.time.period")
    @Value("${search.plugin.time.period}")
    private String timePeriod;

    @JsonProperty("search.plugin.objects.to.exclude")
    @Value("${search.plugin.objects.to.exclude:''}")
    private String objectsToExclude;

    @JsonProperty("search.plugin.export.fields")
    @Value("${search.plugin.export.fields}")
    private String exportFields;

    @JsonProperty("search.plugin.facet")
    @Value("${search.plugin.facet:''}")
    private String facetsMappingString;

    private Map<String, String> facets = new HashMap<>();

    private JSONUnmarshaller jsonUnmarshaller;

    public String getTimePeriod()
    {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod)
    {
        this.timePeriod = timePeriod;
    }

    public String getObjectsToExclude()
    {
        return objectsToExclude;
    }

    public void setObjectsToExclude(String objectsToExclude)
    {
        this.objectsToExclude = objectsToExclude;
    }

    public String getExportFields()
    {
        return exportFields;
    }

    public void setExportFields(String exportFields)
    {
        this.exportFields = exportFields;
    }

    public String getFacetsMappingString()
    {
        return facetsMappingString;
    }

    public void setFacetsMappingString(String facetsMappingString)
    {
        this.facetsMappingString = facetsMappingString;
    }

    public Map<String, String> getFacets()
    {
        return facets;
    }

    public void setFacets(Map<String, String> facets)
    {
        this.facets = facets;
    }

    @Override
    public void afterPropertiesSet()
    {
        facets = jsonUnmarshaller.unmarshall(facetsMappingString, Map.class);
    }

    @JsonIgnore
    public JSONUnmarshaller getJsonUnmarshaller()
    {
        return jsonUnmarshaller;
    }

    public void setJsonUnmarshaller(JSONUnmarshaller jsonUnmarshaller)
    {
        this.jsonUnmarshaller = jsonUnmarshaller;
    }
}
