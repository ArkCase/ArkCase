package com.armedia.acm.services.search.model.solr;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Optional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 8, 2017
 *
 */
public class GetResponseGenerator
{

    /**
     * @param solrResponse
     * @param payloadProducer
     * @return
     * @throws IOException
     * @throws JsonProcessingException
     * @throws AcmObjectNotFoundException
     */
    public static <K extends ResponseHeader, T> SolrSearchResponse<K, T> generateGetResponse(String solrResponse,
            Optional<ResponseHeaderProducer<K>> headerProducer, PayloadProducer<T> payloadProducer)
            throws IOException, JsonProcessingException, AcmObjectNotFoundException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonSolrResponse = mapper.readTree(solrResponse);
        JsonNode responseNode = jsonSolrResponse.get("response");
        SolrSearchResponse<K, T> response = new SolrSearchResponse<>();
        if (headerProducer.isPresent())
        {
            response.setHeader(headerProducer.get().produceResponseHeader(jsonSolrResponse));
        }
        response.setNumFound(responseNode.get("numFound").asInt());
        response.setStart(responseNode.get("start").asInt());
        response.setPayload(payloadProducer.producePayload(jsonSolrResponse));
        return response;
    }

}
