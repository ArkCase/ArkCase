package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.service.GetCaseByNumberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class GetCaseByNumberAPIController
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    private GetCaseByNumberService getCaseByNumberService;

    @RequestMapping(value = "/bynumber", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CaseFile getCaseFileByNumber(@RequestParam(value = "caseNumber", required = true) String caseNumber, Authentication auth)
    {
        return getCaseByNumberService.getCaseByNumber(caseNumber);
    }

    /**
     * @return the getCaseByNumberService
     */
    public GetCaseByNumberService getGetCaseByNumberService()
    {
        return getCaseByNumberService;
    }

    /**
     * @param getCaseByNumberService
     *            the getCaseByNumberService to set
     */
    public void setGetCaseByNumberService(GetCaseByNumberService getCaseByNumberService)
    {
        this.getCaseByNumberService = getCaseByNumberService;
    }

}
