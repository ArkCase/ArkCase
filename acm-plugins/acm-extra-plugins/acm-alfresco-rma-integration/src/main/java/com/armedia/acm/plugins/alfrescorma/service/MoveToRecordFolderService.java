package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class MoveToRecordFolderService extends AlfrescoService<String>
{
    private final String service = "/s/slingshot/doclib/action/move-to/site/rm/documentLibrary";
    private final String query = "alf_ticket={ticket}";

    private String rmaRootFolder;

    private final RestTemplate restTemplate;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    public MoveToRecordFolderService()
    {
        restTemplate = new RestTemplate();
    }

    /**
     * The context must have:
     * <ul>
     * <li>Key ecmFileId: String, CMIS Version Series ID (NOT the id, the versionSeriesId) of the document which will have its metadata set</li>
     * <li>Key categoryFolderName: String</li>
     * <li>Key recordFolderName: String</li>t
     * <li>Key ticket: String, Alfresco ticket</li>
     * </ul>
     */
    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        String ticket = (String) context.get("ticket");
        String categoryFolderName = (String) context.get("categoryFolderName");
        String recordFolderName = (String) context.get("recordFolderName");

        JSONObject moveToRecordFolderPayload = buildPost(context);

        final String fullService = service + "/" + getRmaRootFolder() + "/" + categoryFolderName + "/" +
                recordFolderName;

        final String url = baseUrl() + fullService + "?" + query;

        final HttpEntity<String> entity = buildRestEntity(moveToRecordFolderPayload);

        try
        {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class, ticket);
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
                } else
                {
                    throw new AlfrescoServiceException("Could not move record");
                }
            } else
            {
                throw new AlfrescoServiceException("Could not move record: " + response.getStatusCode());
            }
        } catch (RestClientException e)
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

        if (context.get("categoryFolderName") == null || !(context.get("categoryFolderName") instanceof String))
        {
            throw new IllegalArgumentException("Context must include a categoryFolderName of type String");
        }

        if (context.get("recordFolderName") == null || !(context.get("recordFolderName") instanceof String))
        {
            throw new IllegalArgumentException("Context must include a recordFolderName of type String");
        }

        if (context.get("ticket") == null || !(context.get("ticket") instanceof String))
        {
            throw new IllegalArgumentException("Context must include a ticket of type String");
        }
    }

    public String getRmaRootFolder()
    {
        return rmaRootFolder;
    }

    public void setRmaRootFolder(String rmaRootFolder)
    {
        this.rmaRootFolder = rmaRootFolder;
    }
}
