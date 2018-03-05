package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.EnqueueCaseFileService;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.Date;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class CaseFileEnqueueAPIController
{

    private EnqueueCaseFileService enqueueCaseFileService;
    private UserTrackerService userTrackerService;
    private CaseFileDao caseFileDao;

    private CaseFileEventUtility caseFileEventUtility;

    @RequestMapping(value = "/enqueue/{caseId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CaseFileEnqueueResponse enqueue(@PathVariable("caseId") Long caseId,
            @RequestParam(value = "nextQueue", required = true) String nextQueue, HttpSession session, Authentication auth)
    {

        CaseFilePipelineContext context = new CaseFilePipelineContext();
        context.setAuthentication(auth);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        context.setIpAddress(ipAddress);
        context.setEnqueueName(nextQueue);

        getUserTrackerService().trackUser(ipAddress);

        CaseFileEnqueueResponse response = getEnqueueCaseFileService().enqueueCaseFile(caseId, nextQueue, context);

        if (response.isSuccess())
        {
            // be sure to send back the updated case file - the service does not flush the SQL and might not know
            // about any changes made in the Activiti layer
            CaseFile updated = getCaseFileDao().find(caseId);
            response.setCaseFile(updated);
            caseFileEventUtility.raiseEvent(updated, "updated", new Date(), ipAddress, auth.getName(), auth);
        }

        return response;
    }

    public EnqueueCaseFileService getEnqueueCaseFileService()
    {
        return enqueueCaseFileService;
    }

    public void setEnqueueCaseFileService(EnqueueCaseFileService enqueueCaseFileService)
    {
        this.enqueueCaseFileService = enqueueCaseFileService;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
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
