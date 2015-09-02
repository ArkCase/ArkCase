package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by armdev on 9/2/15.
 */
public abstract class SolrAbstractDocument implements SolrBaseDocument
{
    // extensibility section below
    @JsonUnwrapped
    private Map<String, Object> additionalProperties = new HashMap<>();

    @Override
    public Map<String, Object> getAdditionalProperties()
    {
        return additionalProperties;
    }

    public void setAdditionalProperty(String key, Object value)
    {
        getAdditionalProperties().put(key, value);
    }

    ;
}
