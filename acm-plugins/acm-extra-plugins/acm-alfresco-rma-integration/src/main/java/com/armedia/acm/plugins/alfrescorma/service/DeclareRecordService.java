package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class DeclareRecordService extends AlfrescoService<String>
{
    private final String service = "/s/api/actionQueue";

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * The context must have:
     * <ul>
     * <li>Key ecmFileId: String, versionSeriesId (NOT the document id) of the document to be declared as a record</li>
     * </ul>
     */
    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        JSONObject declareRecordPayload = buildPost(context);

        LOG.debug("Payload: [{}]", declareRecordPayload.toString());

        String url = baseUrl() + "/" + service;

        HttpEntity<String> entity = buildRestEntity(declareRecordPayload);

        try
        {
            ResponseEntity<String> response = getRestTemplate().postForEntity(url, entity, String.class);
            LOG.debug("declare record response: {}", response.getBody());

            if (HttpStatus.OK.equals(response.getStatusCode()))
            {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONObject data = jsonResponse.getJSONObject("data");
                if ("success".equals(data.getString("status")))
                {
                    String actedUponNode = data.getString("actionedUponNode");
                    return actedUponNode;
                }
                else
                {
                    throw new AlfrescoServiceException("Could not declare record: " + data.getString("status"));
                }
            }
            else
            {
                throw new AlfrescoServiceException("Could not declare record: " + response.getStatusCode());
            }
        }
        catch (RestClientException e)
        {
            LOG.error("Exception declaring record: {} {}", e.getMessage(), e);
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    private JSONObject buildPost(Map<String, Object> context)
    {
        String ecmFileId = (String) context.get("ecmFileId");

        JSONObject post = new JSONObject();
        post.put("actionedUponNode", ecmFileId);
        post.put("actionDefinitionName", "create-record");
        JSONObject parameterValues = new JSONObject();
        post.put("parameterValues", parameterValues);

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
