package com.armedia.acm.services.sequence.web.api;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */

@Controller
@RequestMapping({ "/api/v1/plugin/sequence", "/api/latest/plugin/sequence" })
public class SequenceResetAPIController
{

    private AcmSequenceService sequenceService;

    @RequestMapping(value = "/reset", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmSequenceReset> getSequenceReset(@RequestParam(value = "sequenceName", required = true) String sequenceName,
            @RequestParam(value = "sequencePartName", required = true) String sequencePartName, Authentication authentication,
            HttpSession httpSession) throws AcmSequenceException
    {
        return getSequenceService().getSequenceResetList(sequenceName, sequencePartName);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceReset addSequenceReset(@RequestBody AcmSequenceReset sequenceReset,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceService().saveSequenceReset(sequenceReset);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmSequenceReset updateSequenceReset(@RequestBody AcmSequenceReset sequenceReset,
            Authentication authentication, HttpSession httpSession)
            throws AcmSequenceException
    {
        return getSequenceService().saveSequenceReset(sequenceReset);
    }

    /**
     * @return the sequenceService
     */
    public AcmSequenceService getSequenceService()
    {
        return sequenceService;
    }

    /**
     * @param sequenceService
     *            the sequenceService to set
     */
    public void setSequenceService(AcmSequenceService sequenceService)
    {
        this.sequenceService = sequenceService;
    }

}
