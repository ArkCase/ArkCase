package gov.foia.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;

import gov.foia.model.FOIARequest;
import gov.foia.service.RequestAssignmentService;

/**
 * Created by teng.wang on 6/12/2017.
 */
@Controller
@RequestMapping(value = { "/api/v1/plugin/requests/", "/api/latest/plugin/requests/" })
public class StartWorkingOnRequestAPIController
{
    private RequestAssignmentService requestAssignmentService;
    private CaseFileEventUtility caseFileEventUtility;

    @RequestMapping(value = "/{queueId}/start-working", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public FOIARequest assignRequest(
            @PathVariable(value = "queueId") Long queueId,
            HttpSession session, Authentication authentication) throws AcmUserActionFailedException
    {
        FOIARequest request = requestAssignmentService.startWorking(queueId, authentication, session);
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

}
