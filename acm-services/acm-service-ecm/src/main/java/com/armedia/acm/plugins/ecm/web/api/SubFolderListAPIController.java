package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 14.04.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SubFolderListAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileService;

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read|group-read|write|group-write')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}/{folderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmCmisObjectList listFolderChildren(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            @PathVariable("folderId") Long folderId,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "50") int maxRows,
            @RequestParam(value = "category", required = false) String category,
            Authentication authentication)
            throws AcmListObjectsFailedException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {

        if (log.isInfoEnabled())
        {
            log.info("Getting children for the folder with id:  " + folderId);
        }

        AcmContainer container = getFileService().getOrCreateContainer(objectType, objectId);

        // the special category "all" should not be sent to Solr
        category = "all".equals(category) ? null : category;

        AcmCmisObjectList objectList;
        try
        {
            objectList = getFileService().listAllSubFolderChildren(category, authentication, container, folderId, startRow, maxRows, sortBy,
                    sortDirection);
            if (log.isInfoEnabled())
            {
                log.info("Children of the folder with id: " + folderId + " retrieved successfully");
            }
        }
        catch (AcmObjectNotFoundException e)
        {
            if (log.isErrorEnabled())
            {
                log.debug("Folder with id: " + folderId + " not found in the DB");
            }
            throw new AcmUserActionFailedException(AcmFolderConstants.USER_ACTION_LIST_FOLDER, AcmFolderConstants.OBJECT_FOLDER_TYPE,
                    folderId, "Folder not found.", e);
        }
        return objectList;
    }

    public EcmFileService getFileService()
    {
        return fileService;
    }

    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }
}
