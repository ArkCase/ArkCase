package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
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

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    public CreateOrFindRecordFolderService()
    {
        restTemplate = new RestTemplate();
    }


    /**
     * The context must have:
     * <ul>
     * <li>Key parentFolder: type Folder,  for the target folder; the new folder is created under here</li>
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

        Folder parentFolder = (Folder) context.get("parentFolder");
        String recordFolderName = (String) context.get("recordFolderName");

        LOG.debug("Searching for folder {} under category folder {}", recordFolderName, parentFolder.getName());

        JSONObject createFolderPayload = buildPost(parentFolder, recordFolderName);

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
                    recordFolderName, parentFolder.getName());
            String path = parentFolder.getPath() + "/" + recordFolderName;
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

    private JSONObject buildPost(CmisObject categoryFolder, String recordFolderName)
    {
        String categoryFolderId = categoryFolder.getId();

        // The rma_identifier must always be unique, need unique id of parent folder
        String rmaIdentifier = categoryFolder.getId() + "_" + recordFolderName;

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

        if (context.get("parentFolder") == null || !(context.get("parentFolder") instanceof Folder))
        {
            throw new IllegalArgumentException("Context must include a parentFolder of type Folder");
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


}
