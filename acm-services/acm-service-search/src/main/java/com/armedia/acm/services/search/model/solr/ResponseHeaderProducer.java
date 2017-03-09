package com.armedia.acm.services.search.model.solr;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface ResponseHeaderProducer<K extends ResponseHeader>
{
    K produceResponseHeader(JsonNode jsonSolrResponse);
}