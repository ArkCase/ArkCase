package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.casefile.exceptions.MergeCaseFilesException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.MergeCaseOptions;
import com.armedia.acm.plugins.casefile.service.MergeCaseService;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
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
@RequestMapping({"/api/v1/plugin/merge-casefiles", "/api/latest/plugin/merge-casefiles"})
public class MergeCaseFilesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private MergeCaseService mergeCaseService;


    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE})
    @ResponseBody
    public CaseFile mergeCaseFiles(
            @RequestBody MergeCaseOptions mergeCaseOptions,
            HttpSession session,
            Authentication auth
    ) throws MuleException, MergeCaseFilesException, AcmCreateObjectFailedException, AcmUserActionFailedException, PipelineProcessException, AcmAccessControlException
    {

        Objects.requireNonNull(mergeCaseOptions.getSourceCaseFileId(), "Source Id should not be null");
        Objects.requireNonNull(mergeCaseOptions.getTargetCaseFileId(), "Target Id should not be null");
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile targetCaseFile = mergeCaseService.mergeCases(auth, ipAddress, mergeCaseOptions);
        return targetCaseFile;
    }


    public void setMergeCaseService(MergeCaseService mergeCaseService)
    {
        this.mergeCaseService = mergeCaseService;
    }
}
