package gov.privacy.web.api;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;

import gov.privacy.model.SubjectAccessRequest;
import gov.privacy.service.RequestAssignmentService;
import gov.privacy.service.SARService;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
@Controller
@RequestMapping(value = { "/api/v1/plugin/requests/", "/api/latest/plugin/requests/" })
public class StartWorkingOnRequestAPIController
{
    private RequestAssignmentService requestAssignmentService;
    private CaseFileEventUtility caseFileEventUtility;
    private SARService requestService;

    @RequestMapping(value = "/{queueId}/start-working", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SubjectAccessRequest assignRequest(
            @PathVariable(value = "queueId") Long queueId,
            HttpSession session, Authentication authentication) throws AcmUserActionFailedException
    {
        SubjectAccessRequest request = requestAssignmentService.startWorking(queueId, authentication, session);
        if (request != null)
        {
            caseFileEventUtility.raiseEvent(request, "updated", new Date(), (String) session.getAttribute("acm_ip_address"),
                    authentication.getName(), authentication);
            return request;
        }
        else
            throw new AcmUserActionFailedException("start-work", "QUEUE", queueId,
                    "No requests in this queue can be assigned to the current user.", null);
    }

    @RequestMapping(value = "/{requestId}/start-working-on-selected", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SubjectAccessRequest assignSelectedRequestFromQueue(@PathVariable(value = "requestId") Long requestId, HttpSession session,
                                                               Authentication auth) throws AcmUserActionFailedException, PipelineProcessException
    {

        SubjectAccessRequest request = requestService.getSARById(requestId);

        if (request != null)
        {
            requestAssignmentService.assignUserGroupToRequest(request, session);
            requestAssignmentService.assignRequestToUser(request, auth, session);
        }
        else
            throw new AcmUserActionFailedException("start-work on request", "Request", requestId,
                    "Request is not found and can't be assigned to the current user", null);

        return request;
    }

    public RequestAssignmentService getRequestAssignmentService()
    {
        return requestAssignmentService;
    }

    public void setRequestAssignmentService(RequestAssignmentService requestAssignmentService)
    {
        this.requestAssignmentService = requestAssignmentService;
    }

    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public void setRequestService(SARService requestService)
    {
        this.requestService = requestService;
    }

}
