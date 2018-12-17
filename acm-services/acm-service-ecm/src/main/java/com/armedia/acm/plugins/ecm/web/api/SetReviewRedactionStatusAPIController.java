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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.AcmFolderConstants;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.FileEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping({ "/api/v1/service/ecm", "/api/latest/service/ecm" })
public class SetReviewRedactionStatusAPIController
{

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private EcmFileService ecmFileService;

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{fileId}/version/{fileVersion}/review/{reviewStatus}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void setReviewStatus(@PathVariable("fileId") Long fileId,
                                            @PathVariable("fileVersion") String fileVersion,
                                            @PathVariable("reviewStatus") String reviewStatus,
                                            Authentication authentication)
    {
        log.debug("Trying to set Review Status for EcmFile with Id [{}]", fileId);
        getEcmFileService().setReviewStatus(fileId, fileVersion, reviewStatus);

    }

    @PreAuthorize("hasPermission(#fileId, 'FILE', 'write|group-write')")
    @RequestMapping(value = "/file/{fileId}/version/{fileVersion}/redaction/{redactionStatus}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void setRedactionStatus(@PathVariable("fileId") Long fileId,
                                @PathVariable("fileVersion") String fileVersion,
                                @PathVariable("redactionStatus") String redactionStatus,
                                Authentication authentication)
    {
        log.debug("Trying to set Redaction Status for EcmFile with Id [{}]", fileId);
        getEcmFileService().setRedactionStatus(fileId, fileVersion, redactionStatus);

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
