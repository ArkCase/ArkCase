package gov.foia.web.api;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.users.service.tracker.UserTrackerService;

import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import gov.foia.model.ExemptionCodeDto;
import gov.foia.service.FOIAFileService;

/**
 * Manage FOIA document exemption codes.
 * <p>
 * Saving a document in Snowbound should update the exemption code list
 * of a FOIA document (https://project.armedia.com/jira/browse/AFDP-2453)
 * <p>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 19.09.2016.
 */
@Controller
@RequestMapping({ "/api/v1/service/ecm/file", "/api/latest/service/ecm/file" })
public class ExemptionCodesAPIController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;

    private UserTrackerService userTrackerService;

    private FOIAFileService foiaFileService;

    /**
     * Update FOIA document exemption codes.
     *
     * @param fileId
     *            FOIA document identifier
     * @param tags
     *            list of redaction tags (exemption codes)
     * @param auth
     *            authentication token
     * @param session
     *            HTTP session
     */
    @RequestMapping(value = "/{fileId}/tags", method = RequestMethod.POST)
    public void setExemptionCodes(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "tags") List<String> tags,
            Authentication auth,
            HttpSession session) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        String user = auth.getName();
        getAuditPropertyEntityAdapter().setUserId(user);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        // the file id may have a version identifier attached eg. 479:13.0
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        Long realFileId = Long.valueOf(fileIdOnly);


        log.debug("User [{}] coming from [{}] is updating exemption codes [{}] of document [{}]", user, ipAddress, tags, fileId);
        foiaFileService.updateExemptionCodes(realFileId, tags, user);
        log.debug("Exemption codes [{}] of document [{}] updated", tags, fileId);
    }

    /**
     * Get FOIA document exemption codes.
     *
     * @param caseId
     *            FOIA document identifier
     * @param auth
     *            authentication token
     * @param session
     *            HTTP session
     */
    @RequestMapping(value = "/{caseId}/tags", method = RequestMethod.GET)
    public @ResponseBody List<ExemptionCodeDto> getExemptionCodes(
            @PathVariable(value = "caseId") Long caseId,
            Authentication auth,
            HttpSession session) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
    {
        List<ExemptionCodeDto> tags;
        String user = auth.getName();
        getAuditPropertyEntityAdapter().setUserId(user);
        String ipAddress = (String) session.getAttribute("acm_ip_address");
        getUserTrackerService().trackUser(ipAddress);

        log.debug("User [{}] coming from [{}] is getting exemption codes of foia request (case file) [{}]", user, ipAddress, caseId);
        tags = foiaFileService.getExemptionCodes(caseId);
        log.debug("Exemption codes [{}] of foia request (case file) [{}] returned", tags, caseId);
        return tags;
    }

    /**
     * Update fo_exemption_statute field
     */

    @RequestMapping(value = "/exemption/statutes", method = RequestMethod.PUT)
    @ResponseBody
    public void updateExemptionCodesStatute(@RequestBody ExemptionCodeDto exemptionData)
    {
        foiaFileService.updateExemptionCodesStatute(exemptionData);
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public UserTrackerService getUserTrackerService()
    {
        return userTrackerService;
    }

    public void setUserTrackerService(UserTrackerService userTrackerService)
    {
        this.userTrackerService = userTrackerService;
    }

    public FOIAFileService getFoiaFileService()
    {
        return foiaFileService;
    }

    public void setFoiaFileService(FOIAFileService foiaFileService)
    {
        this.foiaFileService = foiaFileService;
    }
}
