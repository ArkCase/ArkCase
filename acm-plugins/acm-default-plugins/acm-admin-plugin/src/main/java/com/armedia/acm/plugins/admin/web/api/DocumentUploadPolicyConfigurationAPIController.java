package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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


import com.armedia.acm.plugins.admin.model.DocumentUploadPolicyConfig;
import com.armedia.acm.plugins.admin.service.DocumentUploadPolicyConfigurationService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping({"/api/v1/plugin/admin", "/api/latest/plugin/admin"})
public class DocumentUploadPolicyConfigurationAPIController
{
    private Logger log = LogManager.getLogger(getClass().getName());
    private DocumentUploadPolicyConfigurationService documentUploadPolicyConfigurationService;

    @RequestMapping(value = "/documentUploadPolicy", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDocumentUploadPolicyConfiguration()
    {
        log.debug("Reading Document Upload Policy configuration");
        return new ResponseEntity<>(getDocumentUploadPolicyConfigurationService().getDocumentUploadPolicyConfig(), HttpStatus.OK);
    }

    @RequestMapping(value = "/documentUploadPolicy", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> saveDocumentUploadPolicyConfiguration(@RequestBody DocumentUploadPolicyConfig documentUploadPolicyConfig)
    {
        try
        {
            log.debug("Saving document upload policy configuration");
            getDocumentUploadPolicyConfigurationService().saveDocumentUploadPolicyConfig(documentUploadPolicyConfig);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Error during saving document upload policy configuration", e);
            throw e;
        }
    }

    public DocumentUploadPolicyConfigurationService getDocumentUploadPolicyConfigurationService()
    {
        return documentUploadPolicyConfigurationService;
    }

    public void setDocumentUploadPolicyConfigurationService(DocumentUploadPolicyConfigurationService documentUploadPolicyConfigurationService)
    {
        this.documentUploadPolicyConfigurationService = documentUploadPolicyConfigurationService;
    }
}
