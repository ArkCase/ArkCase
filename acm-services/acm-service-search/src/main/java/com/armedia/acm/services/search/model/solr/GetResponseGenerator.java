package com.armedia.acm.services.search.model.solr;

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
