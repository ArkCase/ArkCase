package com.armedia.acm.correspondence.web.api;

/*-
 * #%L
 * ACM Service: Correspondence Library
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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.correspondence.service.CorrespondenceService;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping({ "/api/v1/service/correspondence", "/api/latest/service/correspondence" })
public class GenerateCorrespondenceAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private CorrespondenceService correspondenceService;
    private AcmFolderService acmFolderService;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFile generateCorrespondence(
            @RequestParam("templateName") String templateName,
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam("folderId") Long folderId,
            Authentication authentication)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        log.debug("User '{}' is generating template '{}'", authentication.getName(), templateName);

        try
        {
            AcmFolder folder = getAcmFolderService().findById(folderId);
            String targetCmisFolderId = folder.getCmisFolderId();

            EcmFile retval = getCorrespondenceService().generate(authentication, templateName, parentObjectType, parentObjectId,
                    targetCmisFolderId);
            return retval;
        }
        catch (AcmCreateObjectFailedException e)
        {
            log.error("Could not add correspondence: {}", e.getMessage(), e);
            throw e;
        }
        catch (IOException e)
        {
            log.error("Could not add correspondence: {}", e.getMessage(), e);
            throw new AcmCreateObjectFailedException("correspondence", e.getMessage(), e);
        }
    }

    @RequestMapping(value = "/multitemplate", method = RequestMethod.POST)
    @ResponseBody
    public EcmFile generateMultiCorrespondence(
            @RequestBody List<CorrespondenceTemplate> correspondenceTemplates,
            @RequestParam("parentObjectType") String parentObjectType,
            @RequestParam("parentObjectId") Long parentObjectId,
            @RequestParam("folderId") Long folderId,
            @RequestParam("documentName") String documentName,
            Authentication authentication)
            throws Exception {


        if(correspondenceTemplates.size() < 1)
        {
            log.debug("No correspondence templates selected");
            throw new Exception("No correspondence templates selected");
        }

        log.debug("User '{}' is generating multi correspondence", authentication.getName());

        try
        {
            AcmFolder folder = getAcmFolderService().findById(folderId);
            String targetCmisFolderId = folder.getCmisFolderId();

            EcmFile retval = null;

            retval = getCorrespondenceService().generateMultiTemplate(authentication, correspondenceTemplates, parentObjectType, parentObjectId, targetCmisFolderId, documentName);
            if(Objects.isNull(retval))
            {
                throw new Exception("Multi correspondence not generated");
            }

            return retval;
        }
        catch (Exception e)
        {
            log.error("Could not generate multi correspondence: {}", e.getMessage(), e);
            throw e;
        }
    }

    public CorrespondenceService getCorrespondenceService()
    {
        return correspondenceService;
    }

    public void setCorrespondenceService(CorrespondenceService correspondenceService)
    {
        this.correspondenceService = correspondenceService;
    }

    public AcmFolderService getAcmFolderService()
    {
        return acmFolderService;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }
}
