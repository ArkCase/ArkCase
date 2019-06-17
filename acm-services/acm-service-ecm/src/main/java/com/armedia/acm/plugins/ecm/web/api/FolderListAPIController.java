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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmCmisObjectList;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class FolderListAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private EcmFileService ecmFileService;

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read|write|group-read|group-write')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisObjectList listFolderContents(Authentication auth, @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId, @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = false) String category)
            throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = findContainerWithFolder(objectType, objectId);
        category = filterCategory(category);

        return getEcmFileService().listFolderContents(auth, container, category, sortBy, sortDirection, startRow, maxRows);
    }

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read|write|group-read|group-write')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}/search", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisObjectList listFlatSearchResultsFromFolderContent(Authentication auth, @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId, @RequestParam(value = "fq") String searchFilter,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = false) String category)
            throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = findContainerWithFolder(objectType, objectId);
        category = filterCategory(category);

        return getEcmFileService().listFlatSearchResults(auth, container, category, sortBy, sortDirection, startRow, maxRows, searchFilter);
    }

    @PreAuthorize("hasPermission(#objectId, #objectType, 'read|group-read')")
    @RequestMapping(value = "/folder/{objectType}/{objectId}/searchAdvanced", method = RequestMethod.GET)
    @ResponseBody
    public AcmCmisObjectList listFlatSearchResultsFromFolderContentAdvanced(Authentication auth,
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId, @RequestParam(value = "fq") String searchFilter,
            @RequestParam(value = "s", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "dir", required = false, defaultValue = "ASC") String sortDirection,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "1000") int maxRows,
            @RequestParam(value = "category", required = false) String category)
            throws AcmListObjectsFailedException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        AcmContainer container = findContainerWithFolder(objectType, objectId);
        category = filterCategory(category);

        return getEcmFileService().listFlatSearchResultsAdvanced(auth, container, category, sortBy, sortDirection, startRow, maxRows,
                searchFilter);
    }

    /**
     * the special category "all" should not be sent to Solr
     */
    private String filterCategory(final String category)
    {
        return "all".equals(category) ? null : category;
    }

    private AcmContainer findContainerWithFolder(String objectType, Long objectId)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        // just to ensure a folder really exists
        AcmContainer container = getEcmFileService().getOrCreateContainer(objectType, objectId);

        if (container.getFolder() == null)
        {
            // not really possible since the cm_folder_id is not nullable. But we'll account for it anyway
            throw new IllegalStateException("Container '" + container.getId() + "' does not have a folder!");
        }
        return container;

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
