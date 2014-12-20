package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;
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
import java.util.Date;
import java.util.List;

/**
 * Created by marjan.stefanoski on 10/7/2014.
 */
@Controller
@RequestMapping( { "/api/v1/plugin/casefile", "/api/latest/plugin/casefile"})
public class ListCaseFilesByUserAPIController {


    private final Logger log = LoggerFactory.getLogger(getClass());

    private CaseFileDao caseFileDao;

    private CaseFileEventUtility caseFileEventUtility;

    @RequestMapping(value = "/forUser/{user}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<CaseFile> caseFilesForUser(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session
    ) throws AcmListObjectsFailedException, AcmObjectNotFoundException {
        if (log.isInfoEnabled()) {
            log.info("Finding cases assigned to the user '" + user + "'");
        }
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        try {
            List<CaseFile> retval = getCaseFileDao().getNotClosedCaseFilesByUser(user);
            for (CaseFile cf : retval) {
                getCaseFileEventUtility().raiseEvent(cf, "search", new Date(), ipAddress, user, authentication);
            }
            return retval;
        } catch (Exception e) {
            log.error("List Cases Failed: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("case", e.getMessage(), e);
        }
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public CaseFileEventUtility getCaseFileEventUtility() {
        return caseFileEventUtility;
    }

    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility) {
        this.caseFileEventUtility = caseFileEventUtility;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

}
