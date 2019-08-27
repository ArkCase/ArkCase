package com.armedia.acm.plugins.admin.web.api;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.plugins.admin.model.CmisUrlConfig;
import com.armedia.acm.plugins.admin.service.CmisConfigurationService;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/admin/config/cmis/validate-url", "/api/latest/plugin/admin/config/cmis/validate-url" })
public class CmisConfigurationUrlValidation
{
    private Logger log = LogManager.getLogger(getClass());

    private CmisConfigurationService cmisConfigurationService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity validateUrl(@RequestBody CmisUrlConfig cmsiUrlConfig) throws AcmEncryptionException, AcmAppErrorJsonMsg
    {
        try
        {
            List<Repository> repositories = cmisConfigurationService.getRepositories(cmsiUrlConfig);
            log.info("Found repository with ID: " + repositories.get(0).getId());
        }
        catch (CmisConnectionException | CmisUnauthorizedException | NumberFormatException cmisException)
        {
            Map<String, String> urlError = new HashMap<>();
            urlError.put("message", cmisException.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(urlError);
        }
        catch (Exception ex)
        {
            throw new AcmAppErrorJsonMsg(ex.getMessage(), "CmisUrlConfig", ex);

        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public CmisConfigurationService getCmisConfigurationService()
    {
        return cmisConfigurationService;
    }

    public void setCmisConfigurationService(CmisConfigurationService cmisConfigurationService)
    {
        this.cmisConfigurationService = cmisConfigurationService;
    }

}
