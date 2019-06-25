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

import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * This API can be used for taking total count page for the files that are attached to the specific object.
 *
 * Created by riste.tutureski on 10/30/2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class GetTotalPageCountAPIController
{
    private transient final Logger LOG = LogManager.getLogger(getClass());

    private EcmFileService ecmFileService;

    /**
     * Return total page count
     *
     * @param parentObjectType
     *            - the type of the object where the file is attached
     * @param parentObjectId
     *            - the id of the object where the file is attached
     * @param fileTypes
     *            - file types for which the count should be calculated. Format: "fileType1,fileType2,..."
     * @param mimeTypes
     *            - mime types for which the count should be calculated. Format: "mimeTpe1,mimeType2,..."
     * @param auth
     *            - authentication object
     * @return - integer representation of page total count (0 is default)
     */
    @RequestMapping(value = "/totalpagecount/{parentObjectType}/{parentObjectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public int getTotalPageCount(@PathVariable("parentObjectType") String parentObjectType,
            @PathVariable("parentObjectId") Long parentObjectId,
            @RequestParam(value = "fileTypes", required = false, defaultValue = "") String fileTypes,
            @RequestParam(value = "mimeTypes", required = false, defaultValue = "") String mimeTypes, Authentication auth)
    {
        List<String> fileTypesList = Arrays.asList(fileTypes.split("\\s*,\\s*"));
        List<String> mimeTypesList = Arrays.asList(mimeTypes.split("\\s*,\\s*"));

        LOG.debug("File types: {}", fileTypesList);
        LOG.debug("Mime types: {}", mimeTypesList);

        return getEcmFileService().getTotalPageCount(parentObjectType, parentObjectId, fileTypesList, mimeTypesList, auth);
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
