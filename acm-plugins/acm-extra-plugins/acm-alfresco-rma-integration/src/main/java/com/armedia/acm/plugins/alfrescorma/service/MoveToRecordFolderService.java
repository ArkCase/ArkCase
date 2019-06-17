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

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class MoveToRecordFolderService extends AlfrescoService<String>
{
    private final String service = "/s/slingshot/doclib/action/move-to/node";

    private transient final Logger LOG = LogManager.getLogger(getClass());

    /**
     * The context must have:
     * <ul>
     * <li>Key ecmFileId: String, CMIS Version Series ID (NOT the id, the versionSeriesId) of the document which will be
     * moved</li>
     * <li>Key recordFolderId: String, CMIS Object ID of the target folder</li>
     * </ul>
     */
    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        JSONObject moveToRecordFolderPayload = buildPost(context);

        String recordFolderId = (String) context.get("recordFolderId");
        String urlPathParameter = recordFolderId.replace("://", "/");

        final String fullService = service + "/" + urlPathParameter;

        final String url = baseUrl() + fullService;

        final HttpEntity<String> entity = buildRestEntity(moveToRecordFolderPayload);

        try
        {
            ResponseEntity<String> response = getRestTemplate().postForEntity(url, entity, String.class);
            LOG.debug("move record response: {}", response.getBody());

            if (HttpStatus.OK.equals(response.getStatusCode()))
            {
                LOG.debug("response: {}", response.getBody());
                JSONObject jsonResponse = new JSONObject(response.getBody());
                boolean overallSuccess = jsonResponse.getBoolean("overallSuccess");
                int successCount = jsonResponse.getInt("successCount");
                int failureCount = jsonResponse.getInt("failureCount");
                JSONArray results = jsonResponse.getJSONArray("results");
                if (overallSuccess && successCount == 1 && failureCount == 0 && results.length() > 0)
                {
                    JSONObject result = results.getJSONObject(0);
                    String nodeRef = result.getString("nodeRef");
                    return nodeRef;
                }
                else
                {
                    throw new AlfrescoServiceException("Could not move record");
                }
            }
            else
            {
                throw new AlfrescoServiceException("Could not move record: " + response.getStatusCode());
            }
        }
        catch (RestClientException e)
        {
            LOG.error("Exception moving record: {} {}", e.getMessage(), e);
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    private JSONObject buildPost(Map<String, Object> context)
    {
        String ecmFileId = (String) context.get("ecmFileId");

        JSONObject moveToRecordFolderPayload = new JSONObject();
        JSONArray nodeRefs = new JSONArray();
        nodeRefs.put(ecmFileId);
        moveToRecordFolderPayload.put("nodeRefs", nodeRefs);

        return moveToRecordFolderPayload;
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

        if (context.get("recordFolderId") == null || !(context.get("recordFolderId") instanceof String))
        {
            throw new IllegalArgumentException("Context must include a recordFolderId of type String");
        }
    }

}
