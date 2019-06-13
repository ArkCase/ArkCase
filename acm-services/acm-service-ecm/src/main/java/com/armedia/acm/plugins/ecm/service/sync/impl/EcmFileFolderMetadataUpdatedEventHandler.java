package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import static com.armedia.acm.plugins.ecm.utils.ReflectionMethodsUtils.get;
import static com.armedia.acm.plugins.ecm.utils.ReflectionMethodsUtils.set;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.UpdateMetadataConfig;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ivana.shekerova on 1/22/2019.
 */
public class EcmFileFolderMetadataUpdatedEventHandler implements ApplicationListener<EcmEvent>
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private AcmFolderDao folderDao;
    private EcmFileDao fileDao;
    private FolderAndFilesUtils folderAndFilesUtils;
    private UpdateMetadataConfig updateMetadataConfig;

    public void onEcmMetadataUpdated(EcmEvent ecmEvent)
    {
        getAuditPropertyEntityAdapter().setUserId(ecmEvent.getUserId());

        JSONArray propertiesFromConfigArray;
        if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT))
        {
            propertiesFromConfigArray = new JSONArray(updateMetadataConfig.getMetadataUpdateFile());
            EcmFile file = getFolderAndFilesUtils().lookupArkCaseFile(ecmEvent.getNodeId());
            Map<String, String> propertiesForUpdateFromConfig = mapPropertiesFromConfig(propertiesFromConfigArray);
            EcmFile updatedFile = (EcmFile) setObjectMetadata(ecmEvent.getProperties(), propertiesForUpdateFromConfig, file);
            if (updatedFile != null)
            {
                getFileDao().save(updatedFile);
                log.debug("The file with name {} and id {} was updated.", updatedFile.getFileName(), updatedFile.getFileId());
            }
        }
        else if (ecmEvent.getNodeType().equals(EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER))
        {
            propertiesFromConfigArray = new JSONArray(updateMetadataConfig.getMetadataUpdateFolder());
            AcmFolder folder = getFolderAndFilesUtils().lookupArkCaseFolder(ecmEvent.getNodeId());
            Map<String, String> propertiesForUpdateFromConfig = mapPropertiesFromConfig(propertiesFromConfigArray);
            AcmFolder updatedFolder = (AcmFolder) setObjectMetadata(ecmEvent.getProperties(), propertiesForUpdateFromConfig, folder);
            if (updatedFolder != null)
            {
                getFolderDao().save(updatedFolder);
                log.debug("The folder with name {} and id {} was updated.", updatedFolder.getName(), updatedFolder.getId());
            }
        }
    }

    /**
     * Checks if any values are updated and if that is the case it sets them to the variables.
     *
     * @param propertiesFromEvent
     *            - map with properties which alfresco returns
     * @param propertiesForUpdateFromConfig
     *            - map with properties from the configuration
     * @param object
     *            - is the EcmFile or AcmFolder which is being updated
     */
    private Object setObjectMetadata(
            Map<String, String> propertiesFromEvent,
            Map<String, String> propertiesForUpdateFromConfig,
            Object object)
    {
        Object updated = null;
        for (Map.Entry<String, String> entry : propertiesForUpdateFromConfig.entrySet())
        {
            if (propertiesFromEvent.containsKey(entry.getKey()))
            {
                Object currentValue = get(object, entry.getValue());
                String possibleUpdatedValue = propertiesFromEvent.get(entry.getKey());
                if (!possibleUpdatedValue.equals(currentValue))
                {
                    updated = set(object, entry.getValue(), possibleUpdatedValue);
                }
            }
        }
        return updated;
    }

    /**
     * Returns a map, where key/value pairs are the key/value pairs from the JSONObjects in the
     * JSONArray. In every JSONObject should be only one key/value pair.
     *
     * @param propertiesFromConfig
     *            - JSONArray with properties from alfrescoUpdateMetadata.properties
     * @return - mapped properties from the array
     */
    private Map<String, String> mapPropertiesFromConfig(JSONArray propertiesFromConfig)
    {
        Map<String, String> propertiesForUpdateFromConfig = new HashMap<>();
        for (int i = 0; i < propertiesFromConfig.length(); i++)
        {
            // takes one element of the array which is JSONObject
            // {"{http://www.alfresco.org/model/content/1.0}description": "description"}
            JSONObject property = propertiesFromConfig.getJSONObject(i);
            if (property.keys().hasNext())
            {
                String key = property.keys().next().toString();
                propertiesForUpdateFromConfig.put(key, property.getString(key));
            }
        }
        return propertiesForUpdateFromConfig;
    }

    protected boolean isMetadataUpdatedEvent(EcmEvent ecmEvent)
    {
        return EcmEventType.UPDATE.equals(ecmEvent.getEcmEventType());
    }

    @Override
    public void onApplicationEvent(EcmEvent ecmEvent)
    {
        if (isMetadataUpdatedEvent(ecmEvent))
        {
            onEcmMetadataUpdated(ecmEvent);
        }
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public AcmFolderDao getFolderDao()
    {
        return folderDao;
    }

    public void setFolderDao(AcmFolderDao folderDao)
    {
        this.folderDao = folderDao;
    }

    public EcmFileDao getFileDao()
    {
        return fileDao;
    }

    public void setFileDao(EcmFileDao fileDao)
    {
        this.fileDao = fileDao;
    }

    public FolderAndFilesUtils getFolderAndFilesUtils()
    {
        return folderAndFilesUtils;
    }

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    public UpdateMetadataConfig getUpdateMetadataConfig()
    {
        return updateMetadataConfig;
    }

    public void setUpdateMetadataConfig(UpdateMetadataConfig updateMetadataConfig)
    {
        this.updateMetadataConfig = updateMetadataConfig;
    }
}
