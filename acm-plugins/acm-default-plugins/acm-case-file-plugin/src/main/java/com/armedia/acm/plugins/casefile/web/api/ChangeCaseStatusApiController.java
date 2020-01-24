package com.armedia.acm.plugins.casefile.web.api;

/*-
 * #%L
 * ACM Default Plugin: Case File
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.service.ChangeCaseFileStateService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class ChangeCaseStatusApiController
{
    private Logger log = LogManager.getLogger(getClass());

    private ChangeCaseFileStateService changeCaseFileStateService;

    @RequestMapping(value = "/change/status/{mode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> changeCaseFileState(
            @PathVariable("mode") String mode, @RequestBody ChangeCaseStatus form, Authentication auth,
            HttpServletRequest request, HttpSession session) throws AcmAppErrorJsonMsg
    {
        log.info("Changing case status with id [{}]...", form.getCaseId());

        Map<String, String> message = null;

        try
        {
            message = new HashMap<>();
            changeCaseFileStateService.save(form, auth, "");
            if(form.isChangeCaseStatusFlow()){
                message.put("info", "The case file is in approval mode");
            } else {
                message.put("info", "The case file status has changed");
            }

        }
        catch (Exception e)
        {
            log.error("Changing case status with id [{}] failed", form.getCaseId(), e);
            if (message != null)
            {
                message.put("info", e.getMessage());
            }
            AcmAppErrorJsonMsg acmAppErrorJsonMsg = new AcmAppErrorJsonMsg("Changing case status with id %d failed", ChangeCaseStateContants.CHANGE_CASE_STATUS,
                    form.getCaseId().toString(), e);
            throw acmAppErrorJsonMsg;
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
