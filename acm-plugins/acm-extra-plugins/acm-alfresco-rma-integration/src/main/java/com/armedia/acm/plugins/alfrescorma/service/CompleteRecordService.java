package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;

import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Created by dmiller on 11/8/2016.
 */
public class CompleteRecordService extends AlfrescoService<String>
{
    private final String service = "/s/api/rma/actions/ExecutionQueue";

    private transient final Logger LOG = LogManager.getLogger(getClass());

    /**
     * The context must have:
     * <ul>
     * <li>Key ecmFileId: String, versionSeriesId (NOT the document id) of the document to be completed</li>
     * </ul>
     */
    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        JSONObject declareRecordPayload = buildPost(context);

        String url = baseUrl() + "/" + service;

        HttpEntity<String> entity = buildRestEntity(declareRecordPayload);

        try
        {
            ResponseEntity<String> response = getRestTemplate().postForEntity(url, entity, String.class);
            LOG.debug("complete record response: {}", response.getBody());

            if (HttpStatus.OK.equals(response.getStatusCode()))
            {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                // the 'message' field should look like this: Successfully queued action [declareRecord] on
                // workspace://SpacesStore/d935e1ac-5428-4304-b72b-a9a3d02c33ef
                String message = jsonResponse.getString("message");
                return message;
            }
            else
            {
                throw new AlfrescoServiceException("Could not complete record: " + response.getStatusCode());
            }
        }
        catch (RestClientException e)
        {
            LOG.error("Exception completing record: {} {}", e.getMessage(), e);
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    private JSONObject buildPost(Map<String, Object> context)
    {
        String ecmFileId = (String) context.get("ecmFileId");

        JSONObject post = new JSONObject();
        post.put("nodeRef", ecmFileId);
        post.put("name", "declareRecord");

        return post;
    }

    private void validateContext(Map<String, Object> context) throws IllegalArgumentException
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (context.get("ecmFileId") == null || !(context.get("ecmFileId") instanceof String))
        {
            throw new IllegalArgumentException("Context must include an ecmFileId of type String");
        }
    }
}
