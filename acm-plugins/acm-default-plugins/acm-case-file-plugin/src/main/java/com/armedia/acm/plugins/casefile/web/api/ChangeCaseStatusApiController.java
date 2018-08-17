package com.armedia.acm.plugins.casefile.web.api;

import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class ChangeCaseStatusApiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ChangeCaseFileStateService changeCaseFileStateService;

    // {formName} -> change_case_status
    @RequestMapping(value = "/change/status/{mode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changeCaseFileState(
            @PathVariable("mode") String mode, @RequestBody ChangeCaseStatus form, Authentication auth,
            HttpServletRequest request, HttpSession session)
    {
        log.info("Changing case status with id [{}]...", form.getCaseId());

        Map<String, String> message = null;

        try
        {
            message = new HashMap<>();
            changeCaseFileStateService.save(form, auth, "");
        }
        catch (Exception e)
        {
            log.error("Changing case status with id [{}] failed", form.getCaseId(), e);
            if (message != null)
            {
                message.put("info", e.getMessage());
            }
        }

        if (message.isEmpty())
        {
            message.put("info", "Changing case status with id " + form.getCaseId() + " failed");
        }
        return message;
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

// closeComplaintService.save(form, auth, mode);
// message.put("info", "The complaint is in approval mode");
// }catch(
// Exception e)
// {
// log.error("Closing complaint with id [{}] failed", form.getComplaintId(), e);
// message.put("info", e.getMessage());
