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
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class CreateOrFindRecordFolderOrRecordCategoryService extends AlfrescoService<String>
{

    private final String folderService = "/s/api/type/rma%3arecordFolder/formprocessor";
    private final String categoryService = "/s/api/type/rma%3arecordCategory/formprocessor";
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;

    /**
     * The context must have:
     * <ul>
     * <li>Key parentFolder: type Folder, for the target folder; the new folder is created under here</li>
     * <li>Key recordFolderName: String title of the record folder</li>
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

        Folder parentFolder = (Folder) context.get("parentFolder");
        String recordFolderName = (String) context.get("recordFolderName");
        String type = (String) context.get("type");

        LOG.debug("Searching for folder {} under category folder {}", recordFolderName, parentFolder.getName());

        JSONObject createFolderPayload = buildPost(parentFolder, recordFolderName);

        String url = baseUrl();
        if(type.equalsIgnoreCase("Record Category"))
        {
            url +=  "/"  + categoryService;
        }
        else {
             url += "/" + folderService;
        }

        HttpEntity<String> entity = buildRestEntity(createFolderPayload);

        try
        {
            ResponseEntity<String> response = getRestTemplate().postForEntity(url, entity, String.class);
            LOG.debug("create record folder response: {}", response.getBody());

            JSONObject jsonResponse = new JSONObject(response.getBody());
            String folderId = jsonResponse.getString("persistedObject");
            return folderId;
        }
        catch (HttpServerErrorException e)
        {
            LOG.debug("Error creating folder {} under {}, presumably it already exists, looking for it now.", recordFolderName,
                parentFolder.getName());
            String path = parentFolder.getPath() + "/" + recordFolderName;
            try
            {
                CmisObject recordFolder = getEcmFileService().findObjectByPath(path);
                return recordFolder.getProperty(EcmFileConstants.REPOSITORY_VERSION_ID).getValue();
            }
            catch (Exception e1)
            {
                throw new AlfrescoServiceException(e1.getMessage(), e1);
            }
        }

    }

    private JSONObject buildPost(CmisObject categoryFolder, String recordFolderName)
    {
        String categoryFolderId = categoryFolder.getProperty(EcmFileConstants.REPOSITORY_VERSION_ID).getValue();

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
