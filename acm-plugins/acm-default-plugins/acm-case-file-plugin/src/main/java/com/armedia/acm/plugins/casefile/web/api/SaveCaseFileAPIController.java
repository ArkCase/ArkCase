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

import com.armedia.acm.auth.AuthenticationUtils;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.SaveCaseService;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.participants.model.DecoratedAssignedObjectParticipants;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class SaveCaseFileAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private SaveCaseService saveCaseService;

    private CaseFileEventUtility caseFileEventUtility;

    private UserTrackerService userTrackerService;

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    @DecoratedAssignedObjectParticipants
    @ResponseBody
    public CaseFile createCaseFile(@RequestBody CaseFile in, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        return saveCase(in, null, session, auth);
    }

    @PreAuthorize("#in.id == null or hasPermission(#in.id, 'CASE_FILE', 'saveCase')")
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public CaseFile createCaseFileMutipart(@RequestPart(name = "casefile") CaseFile in,
            @RequestPart(name = "files")Map<String, List<MultipartFile>> filesMap, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        return saveCase(in, filesMap, session, auth);
    }

    private CaseFile saveCase(CaseFile in, Map<String, List<MultipartFile>> filesMap, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException,
            IOException
    {
        log.trace("Got a case file: [{}] ; case ID: [{}]", in, in.getId());
        String ipAddress = (String) session.getAttribute("acm_ip_address");

        userTrackerService.trackUser(ipAddress);

        try
        {
            boolean isNew = in.getId() == null;

            // explicitly set modifier and modified to trigger transformer to reindex data
            // fixes problem when some child objects are changed (e.g participants) and solr document is not updated
            in.setModifier(AuthenticationUtils.getUsername());
            in.setModified(new Date());

            CaseFile saved = getSaveCaseService().saveCase(in, filesMap, auth, ipAddress);

            // since the approver list is not persisted to the database, we want to send them back to the caller...
            // the approver list is only here to send to the Activiti engine. After the workflow is started the
            // approvers are stored in Activiti.
            saved.setApprovers(in.getApprovers());

            if (isNew)
            {
                caseFileEventUtility.raiseEvent(saved, "created", new Date(), ipAddress, auth.getName(), auth);
                caseFileEventUtility.raiseEvent(saved, saved.getStatus(), new Date(), ipAddress, auth.getName(), auth);
            }
            else
            {
                caseFileEventUtility.raiseEvent(saved, "updated", new Date(), ipAddress, auth.getName(), auth);
            }

            return saved;
        }
        catch (PipelineProcessException | PersistenceException e)
        {
            throw new AcmCreateObjectFailedException("Case File", e.getMessage(), e);
        }
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public SaveCaseService getSaveCaseService()
    {
        return saveCaseService;
    }

    public void setSaveCaseService(SaveCaseService saveCaseService)
    {
        this.saveCaseService = saveCaseService;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }
}
