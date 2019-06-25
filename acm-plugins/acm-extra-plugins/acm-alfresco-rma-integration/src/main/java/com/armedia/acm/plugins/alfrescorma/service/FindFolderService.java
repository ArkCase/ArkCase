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
import com.armedia.acm.plugins.alfrescorma.model.AlfrescoRmaConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class FindFolderService extends AlfrescoService<Folder>
{
    private static final String BASE_PATH = "/Sites/rm/documentLibrary/";
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private EcmFileService ecmFileService;
    private AlfrescoRmaConfig rmaConfig;

    /**
     * The context must contain either "objectType" or "folderPath" keys. If "objectType" is present, the category
     * folder path is constructed based on the property "rma_categoryFolder_${objectType}" (this is core ArkCase
     * behavior). Otherwise, the category folder path is constructed by concatenating ${folderPath} onto the
     * RMA base path (this allows extensions to construct the path however they want).
     *
     * @param context
     * @return
     * @throws AlfrescoServiceException
     */
    @Override
    public Folder doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        validateContext(context);

        String fullPath = buildFolderPath(context);

        LOG.debug("Looking for folder {}", fullPath);

        try
        {
            Folder object = (Folder) getEcmFileService().findObjectByPath(fullPath);
            LOG.debug("Found object with id {} for path {}", object.getId(), fullPath);
            return object;

        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(e.getMessage(), e);
        }
    }

    private String buildFolderPath(Map<String, Object> context)
    {
        if (context.containsKey("folderPath"))
        {
            String path = (String) context.get("folderPath");
            return BASE_PATH + rmaConfig.getRootFolder() + "/" + path;
        }

        String objectType = (String) context.get("objectType");

        String categoryFolderName = rmaConfig.getCategoryFolderForObject(objectType);

        return BASE_PATH + rmaConfig.getRootFolder() + "/" + categoryFolderName;
    }

    private void validateContext(Map<String, Object> context) throws IllegalArgumentException
    {
        if (context == null || (!context.containsKey("objectType") && !context.containsKey("folderPath")))
        {
            throw new IllegalArgumentException("Context must include either the key 'objectType' or the key 'folderPath'");
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

    public AlfrescoRmaConfig getRmaConfig()
    {
        return rmaConfig;
    }

    public void setRmaConfig(AlfrescoRmaConfig rmaConfig)
    {
        this.rmaConfig = rmaConfig;
    }
}
