package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;

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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.casefile.service.MergeCaseService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.api.MuleException;
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
@RequestMapping({ "/api/v1/plugin/merge-casefiles", "/api/latest/plugin/merge-casefiles" })
public class MergeCaseFilesAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private MergeCaseService mergeCaseService;

    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile mergeCaseFiles(
            @RequestBody MergeCaseOptions mergeCaseOptions,
            HttpSession session,
            Authentication auth) throws MuleException, MergeCaseFilesException, AcmCreateObjectFailedException,
            AcmUserActionFailedException, PipelineProcessException, AcmAccessControlException
    {

        Objects.requireNonNull(mergeCaseOptions.getSourceCaseFileId(), "Source Id should not be null");
        Objects.requireNonNull(mergeCaseOptions.getTargetCaseFileId(), "Target Id should not be null");
        if (mergeCaseOptions.getSourceCaseFileId().equals(mergeCaseOptions.getTargetCaseFileId()))
        {
            throw new AcmUserActionFailedException("Merge case file with id:[" + mergeCaseOptions.getSourceCaseFileId() + "]!", "CASE_FILE",
                    mergeCaseOptions.getSourceCaseFileId(), "Cannot merge a case to itself!", null);
        }
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile targetCaseFile = mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);
        return targetCaseFile;
    }

    public void setMergeCaseService(MergeCaseService mergeCaseService)
    {
        this.mergeCaseService = mergeCaseService;
    }
}
