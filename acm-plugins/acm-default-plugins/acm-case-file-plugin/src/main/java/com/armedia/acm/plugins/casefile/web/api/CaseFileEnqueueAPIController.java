package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.casefile.service.EnqueueCaseFileService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class CaseFileEnqueueAPIController
{

    private EnqueueCaseFileService enqueueCaseFileService;

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

        return enqueueCaseFileService.enqueueCaseFile(caseId, nextQueue, context);
    }

    public EnqueueCaseFileService getEnqueueCaseFileService()
    {
        return enqueueCaseFileService;
    }

    public void setEnqueueCaseFileService(EnqueueCaseFileService enqueueCaseFileService)
    {
        this.enqueueCaseFileService = enqueueCaseFileService;
    }

}
