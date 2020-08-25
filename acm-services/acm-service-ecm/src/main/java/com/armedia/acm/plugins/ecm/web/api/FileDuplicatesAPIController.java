package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileDeDuplicationConfig;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileDeDuplicationConfigurationServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class FileDuplicatesAPIController {

    private transient final Logger log = LogManager.getLogger(getClass());
    private EcmFileService fileService;
    private EcmFileDeDuplicationConfigurationServiceImpl ecmFileDeDuplicationConfigurationService;

    @PreAuthorize("hasPermission(#objectId, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/fileDuplicates/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EcmFile> getFileDuplicates(@PathVariable("fileId") Long objectId, Authentication authentication, HttpSession session)
            throws AcmObjectNotFoundException
    {
        return getFileService().getFileDuplicates(objectId);
    }

    @RequestMapping(value = "deDuplication/getConfig", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EcmFileDeDuplicationConfig getConfiguration()
    {
        log.debug("Reading the de duplication configuration");
        return getEcmFileDeDuplicationConfigurationService().getEcmFileDeDuplicationConfig();
    }

    @RequestMapping(value = "deDuplication/updateConfig", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void updateConfiguration(@RequestBody Map<String, Object> properties)
    {
        log.debug("Updating the de duplication configuration [{}] ", properties);
        getEcmFileDeDuplicationConfigurationService().writeConfiguration(properties);
    }

    public EcmFileService getFileService() {
        return fileService;
    }

    public void setFileService(EcmFileService fileService) {
        this.fileService = fileService;
    }

    public EcmFileDeDuplicationConfigurationServiceImpl getEcmFileDeDuplicationConfigurationService() {
        return ecmFileDeDuplicationConfigurationService;
    }

    public void setEcmFileDeDuplicationConfigurationService(EcmFileDeDuplicationConfigurationServiceImpl ecmFileDeDuplicationConfigurationService) {
        this.ecmFileDeDuplicationConfigurationService = ecmFileDeDuplicationConfigurationService;
    }
}
