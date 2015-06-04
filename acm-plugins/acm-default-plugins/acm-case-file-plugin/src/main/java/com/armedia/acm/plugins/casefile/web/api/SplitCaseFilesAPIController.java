package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.SplitCaseFileException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.SplitCaseOptions;
import com.armedia.acm.plugins.casefile.service.SplitCaseService;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping({"/api/v1/plugin/copyCaseFile", "/api/latest/plugin/copyCaseFile"})
public class SplitCaseFilesAPIController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SplitCaseService splitCaseService;


    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public CaseFile splitCaseFiles(
           @RequestBody SplitCaseOptions splitCaseOptions,
            HttpSession session,
            Authentication auth
    ) throws MuleException, AcmCreateObjectFailedException, AcmUserActionFailedException, SplitCaseFileException, AcmFolderException, AcmObjectNotFoundException {

        Objects.requireNonNull(splitCaseOptions.getCaseFileId(), "Case file for splitting should not be null");
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile splitedCaseFile = splitCaseService.splitCase(auth, ipAddress, splitCaseOptions);
        return splitedCaseFile;
    }


    public void setSplitCaseService(SplitCaseService splitCaseService) {
        this.splitCaseService = splitCaseService;
    }
}
