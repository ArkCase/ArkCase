package com.armedia.acm.services.search.model.solr;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

@FunctionalInterface
public interface PayloadProducer<T>
{
    T producePayload(JsonNode jsonSolrResponse) throws IOException, AcmObjectNotFoundException;
}