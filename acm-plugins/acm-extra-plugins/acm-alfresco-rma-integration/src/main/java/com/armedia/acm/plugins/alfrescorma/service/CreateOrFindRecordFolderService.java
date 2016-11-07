package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class CreateOrFindRecordFolderService extends AlfrescoService<String>
{

    private final String service = "/s/api/type/rma%3arecordFolder/formprocessor";
    private final String query = "alf_ticket={ticket}";
    private final RestTemplate restTemplate;

    private EcmFileService ecmFileService;

    private String rmaRootFolder;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final String BASE_PATH = "/Sites/rm/documentLibrary";

    public CreateOrFindRecordFolderService()
    {
        restTemplate = new RestTemplate();
    }


    /**
     * The context must have:
     * <ul>
     * <li>Key categoryFolder: CmisObject for the record category folder</li>
     * <li>Key recordFolderName: String title of the record folder</li>
     * <li>Key ticket: String, Alfresco ticket</li>
     * </ul>
     *
     * @param context
     * @return
     * @throws AlfrescoServiceException
     */
    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        String ticket = (String) context.get("ticket");
        CmisObject categoryFolder = (CmisObject) context.get("categoryFolder");
        String recordFolderName = (String) context.get("recordFolderName");

        LOG.debug("Searching for folder {} under category folder {}", recordFolderName, categoryFolder.getName());

        JSONObject createFolderPayload = buildPost(categoryFolder, recordFolderName);

        String url = baseUrl() + "/" + service + "?" + query;

        HttpEntity<String> entity = buildRestEntity(createFolderPayload);

        try
        {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class, ticket);
            LOG.debug("create record folder response: {}", response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String folderId = jsonResponse.getString("persistedObject");
            return folderId;
        } catch (HttpServerErrorException e)
        {
            LOG.debug("Error creating folder {} under {}, presumably it already exists, looking for it now.",
                    recordFolderName, categoryFolder.getName());
            String path = BASE_PATH + "/" + getRmaRootFolder() + "/" + categoryFolder.getName() + "/" + recordFolderName;
            try
            {
                CmisObject recordFolder = getEcmFileService().findObjectByPath(path);
                return recordFolder.getId();

            } catch (Exception e1)
            {
                throw new AlfrescoServiceException(e1.getMessage(), e1);
            }
        }

    }

    private HttpEntity<String> buildRestEntity(JSONObject createFolderPayload)
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(createFolderPayload.toString(), headers);
    }

    private JSONObject buildPost(CmisObject categoryFolder, String recordFolderName)
    {

        String categoryFolderId = categoryFolder.getId();


        String rmaIdentifier = recordFolderName + "_" + categoryFolder.getName();


        JSONObject createFolderPayload = new JSONObject();
        createFolderPayload.put("alf_destination", categoryFolderId);
        createFolderPayload.put("prop_cm_name", recordFolderName);
        createFolderPayload.put("prop_cm_title", recordFolderName);
        createFolderPayload.put("prop_rma_identifier", rmaIdentifier);
        createFolderPayload.put("prop_rma_vitalRecordIndicator", "false");
        createFolderPayload.put("prop_rma_reviewPeriod", "none|0");
        return createFolderPayload;
    }

    private void validateContext(Map<String, Object> context) throws IllegalArgumentException
    {
        if (context == null)
        {
            throw new IllegalArgumentException("Context must not be null");
        }

        if (context.get("categoryFolder") == null || !(context.get("categoryFolder") instanceof CmisObject))
        {
            throw new IllegalArgumentException("Context must include a categoryFolder of type CmisObject");
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

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
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
