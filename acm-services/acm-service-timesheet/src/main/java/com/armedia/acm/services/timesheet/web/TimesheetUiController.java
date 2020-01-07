package com.armedia.acm.services.timesheet.web;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.form.config.FormUrl;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.services.timesheet.model.TimesheetConstants;
import com.armedia.acm.services.timesheet.service.TimesheetService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/timesheet")
public class TimesheetUiController
{
    private Logger log = LogManager.getLogger(getClass());
    private TimesheetService timesheetService;
    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showTimesheetPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheet");

        String treeSort = getTimesheetService().getConfiguration().getSearchTreeSort();
        if (null != treeSort)
        {
            retval.addObject("treeSort", treeSort);
        }
        else
        {
            log.warn("Tree sort missing");
        }

        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET, false));
        return retval;

    }

    @RequestMapping(value = "/wizard", method = RequestMethod.GET)
    public ModelAndView openTimesheetWizard()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheetWizard");

        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET, false));
        return retval;

    }

    @RequestMapping(value = "/{timesheetId}", method = RequestMethod.GET)
    public ModelAndView openTimesheet(Authentication auth, @PathVariable(value = "timesheetId") Long timesheetId)
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("timesheet");
        retval.addObject("objId", timesheetId);

        // Frevvo form URLs
        retval.addObject("newTimesheetFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.TIMESHEET, false));
        return retval;

    }

    public FormUrl getFormUrl()
    {
        return formUrl;
    }

    public void setFormUrl(FormUrl formUrl)
    {
        this.formUrl = formUrl;
    }

    public TimesheetService getTimesheetService()
    {
        return timesheetService;
    }

    public void setTimesheetService(TimesheetService timesheetService)
    {
        this.timesheetService = timesheetService;
    }

}
