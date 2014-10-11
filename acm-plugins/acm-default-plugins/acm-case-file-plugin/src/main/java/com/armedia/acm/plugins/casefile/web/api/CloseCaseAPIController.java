package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class CloseCaseAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ChangeCaseFileStateService changeCaseFileStateService;

    @RequestMapping(
            value = "/closeCase/{caseId}",
            method = RequestMethod.POST,
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE })
    @ResponseBody
    public CaseFile closeCase(
            @PathVariable(value = "caseId") Long caseId,
            HttpSession session,
            Authentication auth
    ) throws AcmUserActionFailedException
    {
        if (log.isDebugEnabled())
        {
            log.debug("Got an case file to complete; case id: '" + caseId + "'");
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        return getChangeCaseFileStateService().changeCaseState(auth, caseId, "CLOSED", ipAddress);
    }

    public ChangeCaseFileStateService getChangeCaseFileStateService()
    {
        return changeCaseFileStateService;
    }

    public void setChangeCaseFileStateService(ChangeCaseFileStateService changeCaseFileStateService)
    {
        this.changeCaseFileStateService = changeCaseFileStateService;
    }
}
