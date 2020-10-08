package com.armedia.acm.services.exemption.web.api;

import com.armedia.acm.services.exemption.exception.DeleteExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.GetExemptionStatuteException;
import com.armedia.acm.services.exemption.exception.SaveExemptionStatuteException;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import com.armedia.acm.services.exemption.service.ExemptionStatuteService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/service/exemptionStatute", "/api/latest/service/exemptionStatute" })
public class ExemptionStatuteAPIController
{

    private final Logger log = LogManager.getLogger(getClass());
    private ExemptionStatuteService exemptionStatuteService;

    @RequestMapping(value = "/tags", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ExemptionStatute> saveExemptionStatutes(@RequestBody ExemptionStatute exemptionStatutes,
            Authentication authentication) throws SaveExemptionStatuteException
    {
        String user = authentication.getName();
        return getExemptionStatuteService().saveExemptionStatutes(exemptionStatutes, user);

    }

    @RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity deleteExemptionStatute(@PathVariable Long tagId) throws DeleteExemptionStatuteException
    {
        getExemptionStatuteService().deleteExemptionStatute(tagId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/{caseId}/tags/{fileId}", method = RequestMethod.GET)
    public @ResponseBody List<ExemptionStatute> getExemptionCodes(
            @PathVariable(value = "caseId") Long caseId,
            @PathVariable(value = "fileId") Long fileId,
            Authentication auth,
            HttpSession session) throws GetExemptionStatuteException
    {
        List<ExemptionStatute> tags;
        String user = auth.getName();

        log.debug("User [{}] coming from [{}] is getting exemption statutes of foia request (case file) [{}]", user, caseId);
        tags = getExemptionStatuteService().getExemptionStatutes(caseId, fileId);
        log.debug("Exemption statutes [{}] of foia request (case file) [{}] returned", tags, caseId);
        return tags;
    }

    @RequestMapping(value = "/{fileId}//tags/manually", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void setExemptionStatutesManually(
            @PathVariable(value = "fileId") String fileId,
            @RequestParam(value = "tags") List<String> tags,
            Authentication auth,
            HttpSession session) throws SaveExemptionStatuteException
    {
        String user = auth.getName();

        String fileIdOnly = fileIdOnlyChecker(fileId);
        Long realFileId = Long.valueOf(fileIdOnly);

        log.debug("User [{}] coming from [{}] is updating exemption statutes [{}] of document [{}]", user, tags, fileId);
        getExemptionStatuteService().saveExemptionStatutesOnDocument(realFileId, tags, user);
        log.debug("Exemption statutes [{}] of document [{}] updated", tags, fileId);
    }

    private String fileIdOnlyChecker(String fileId)
    {

        // the file id may have a version identifier attached eg. 479:13.0
        String fileIdOnly = fileId == null ? null
                : fileId.contains(":") ? StringUtils.substringBefore(fileId, ":")
                        : fileId;
        log.debug("File id without version identifier: {}", fileIdOnly);

        return fileIdOnly;

    }

    public ExemptionStatuteService getExemptionStatuteService()
    {
        return exemptionStatuteService;
    }

    public void setExemptionStatuteService(ExemptionStatuteService exemptionStatuteService)
    {
        this.exemptionStatuteService = exemptionStatuteService;
    }
}
