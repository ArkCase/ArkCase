package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.utility.CaseFileEventUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.Date;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class SaveFOIARequestService
{

    private final Logger log = LoggerFactory.getLogger(getClass());
    private FOIARequestService foiaRequestService;
    private CaseFileEventUtility caseFileEventUtility;

    public CaseFile saveFOIARequest(CaseFile in, List<MultipartFile> files, HttpSession session, Authentication auth)
            throws AcmCreateObjectFailedException
    {
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        CaseFile saved = getFoiaRequestService().saveRequest(in, files, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, auth, ipAddress);
        return saved;
    }

    public CaseFile savePortalRequest(CaseFile in, List<MultipartFile> files, Authentication auth, String ipAddress)
            throws AcmCreateObjectFailedException
    {
        CaseFile saved = getFoiaRequestService().saveRequest(in, files, auth, ipAddress);
        raiseCaseEvent(in.getId() == null, saved, auth, ipAddress);
        return saved;
    }

    private void raiseCaseEvent(boolean isNew, CaseFile saved, Authentication auth, String ipAddress)
    {
        if (isNew)
        {
            caseFileEventUtility.raiseEvent(saved, "created", new Date(), ipAddress, auth.getName(), auth);
            caseFileEventUtility.raiseEvent(saved, saved.getStatus(), new Date(), ipAddress, auth.getName(), auth);
        }
        else
        {
            caseFileEventUtility.raiseEvent(saved, "updated", new Date(), ipAddress, auth.getName(), auth);
        }
    }

    /**
     * @return the foiaRequestService
     */
    public FOIARequestService getFoiaRequestService()
    {
        return foiaRequestService;
    }

    /**
     * @param foiaRequestService
     *            the foiaRequestService to set
     */
    public void setFoiaRequestService(FOIARequestService foiaRequestService)
    {
        this.foiaRequestService = foiaRequestService;
    }

    /**
     * @return the caseFileEventUtility
     */
    public CaseFileEventUtility getCaseFileEventUtility()
    {
        return caseFileEventUtility;
    }

    /**
     * @param caseFileEventUtility
     *            the caseFileEventUtility to set
     */
    public void setCaseFileEventUtility(CaseFileEventUtility caseFileEventUtility)
    {
        this.caseFileEventUtility = caseFileEventUtility;
    }

}
