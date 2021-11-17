package com.armedia.acm.plugins.complaint.web.api;

/*-
 * #%L
 * ACM Default Plugin: Complaints
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

import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.service.CloseComplaintService;
import com.armedia.acm.plugins.complaint.service.CloseComplaintServiceImpl;

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
@RequestMapping({ "/api/latest/plugin/complaint" })
public class CloseComplaintFormAPIController
{
    private Logger log = LogManager.getLogger(getClass());
    private CloseComplaintService closeComplaintService;

    @RequestMapping(value = "/close/{mode}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> closeComplaint(
            @PathVariable String mode, @RequestBody CloseComplaintRequest form, Authentication auth,
            HttpServletRequest request, HttpSession session)
    {
        log.info("Closing complaint with id [{}]...", form.getComplaintId());
        Map<String, String> message = new HashMap<>();
        try
        {
            if(form.getReferExternalOrganizationId() != null && form.getReferExternalPersonId() != null)
            {
                closeComplaintService.createPersonOrganizationAssociation(form);    
            }
            closeComplaintService.save(form, auth, mode);
            if (form.isCloseComplaintStatusFlow())
            {
                message.put("info", "The complaint is in approval mode");
            }
            else
            {
                message.put("info", "The complaint is closed");
            }
        }
        catch (Exception e)
        {
            log.error("Closing complaint with id [{}] failed", form.getComplaintId(), e);
            message.put("info", e.getMessage());
        }
        finally
        {
            if (message.isEmpty())
            {
                message.put("info", "Closing complaint with id " + form.getComplaintId() + " failed");
            }
        }
        return message;
    }

    public CloseComplaintService getCloseComplaintService()
    {
        return closeComplaintService;
    }

    public void setCloseComplaintService(CloseComplaintServiceImpl closeComplaintService)
    {
        this.closeComplaintService = closeComplaintService;
    }
}
