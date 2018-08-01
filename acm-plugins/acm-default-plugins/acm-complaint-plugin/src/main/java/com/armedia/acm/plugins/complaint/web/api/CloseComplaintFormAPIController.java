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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping({ "/api/latest/plugin/complaint" })
public class CloseComplaintFormAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private CloseComplaintService closeComplaintService;

    @RequestMapping(value = "/close", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Boolean completeTask(
            @RequestParam(value = "mode", required = true) String mode, @RequestBody CloseComplaintRequest form, Authentication auth,
            HttpServletRequest request, HttpSession session)
    {
        log.info("Closing complaint with id {}...", form.getComplaintId());

        try
        {
            // Tuka CloseCompliantRequest treba da go zemam od UI
            closeComplaintService.save(form, auth, mode);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return true;
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
