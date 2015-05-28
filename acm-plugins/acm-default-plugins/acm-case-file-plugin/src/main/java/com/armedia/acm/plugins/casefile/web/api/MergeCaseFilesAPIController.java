package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.MergeCaseService;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({"/api/v1/plugin/merge-casefiles", "/api/latest/plugin/merge-casefiles"})
public class MergeCaseFilesAPIController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MergeCaseService mergeCaseService;


    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public CaseFile mergeCaseFiles(
            @RequestParam(value = "sourceId", required = true) Long sourceId,
            @RequestParam(value = "targetId", required = true) Long targetId,
            HttpSession session,
            Authentication auth
    ) throws MuleException {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile targetCaseFile = mergeCaseService.mergeCases(auth, ipAddress, sourceId, targetId);
        return targetCaseFile;
    }


    public void setMergeCaseService(MergeCaseService mergeCaseService) {
        this.mergeCaseService = mergeCaseService;
    }
}
