package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.casefile.service.SplitCaseService;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Objects;

@Controller
@RequestMapping({ "/api/v1/plugin/copyCaseFile", "/api/latest/plugin/copyCaseFile" })
public class SplitCaseFilesAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private SplitCaseService splitCaseService;

    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile splitCaseFiles(
            @RequestBody SplitCaseOptions splitCaseOptions,
            HttpSession session,
            Authentication auth) throws AcmCreateObjectFailedException, AcmUserActionFailedException, SplitCaseFileException,
            AcmFolderException, AcmObjectNotFoundException, PipelineProcessException
    {

        Objects.requireNonNull(splitCaseOptions.getCaseFileId(), "Case file for splitting should not be null");
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile copyCaseFile = splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);
        return copyCaseFile;
    }

    public void setSplitCaseService(SplitCaseService splitCaseService)
    {
        this.splitCaseService = splitCaseService;
    }
}
