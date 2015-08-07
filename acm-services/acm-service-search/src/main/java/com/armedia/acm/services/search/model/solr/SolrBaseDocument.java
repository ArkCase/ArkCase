package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 10/23/14.
 */
public interface SolrBaseDocument
{

    String getId();

    void setId(String id);

    void setDeny_acl_ss(List<String> deny_acl_ss);

    void setAllow_acl_ss(List<String> allow_acl_ss);

    void setPublic_doc_b(boolean public_doc_b);

    void setProtected_object_b(boolean protected_object_b);

    // extensibility section below
    @JsonUnwrapped
    Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    default public Map<String, Object> getAdditionalProperties()
    {
        return additionalProperties;
    }

    @JsonAnySetter
    default public void setAdditionalProperty(String key, Object value)
    {
        additionalProperties.put(key, value);
    }
}
