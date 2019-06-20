package com.armedia.acm.plugins.admin.web;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/plugin/admin")
public class AdminUiController
{
    private Logger log = LogManager.getLogger(getClass());

    private FormUrl formUrl;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAdminPage()
    {
        ModelAndView retval = new ModelAndView();

        // Add Frevvo form URLs
        retval.addObject("plainConfigurationFormUrl", getFormUrl().getNewFormUrl(FrevvoFormName.PLAIN_CONFIGURATION, false));

        retval.setViewName("admin");
        return retval;
    }

    @RequestMapping(value = "/locks", method = RequestMethod.GET)
    public ModelAndView showLocksPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("adminLocks");
        return retval;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public ModelAndView showDashboardPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("adminDashboard");
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

}
