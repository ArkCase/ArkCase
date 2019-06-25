package com.armedia.acm.plugins.audit.web;

/*-
 * #%L
 * ACM Default Plugin: Audit
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/plugin/audit")
public class AuditUiController
{
    private Logger log = LogManager.getLogger(getClass());

    private Map<String, String> auditProperties;
    private Map<String, String> reportsProperties;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView showAuditPage()
    {
        ModelAndView retval = new ModelAndView();
        retval.setViewName("audit");

        String pentahoHost = getReportsProperties().get("PENTAHO_SERVER_URL");
        String pentahoPort = getReportsProperties().get("PENTAHO_SERVER_PORT");
        String auditReportUri = getAuditProperties().get("AUDIT_REPORT");
        String auditCriteria = getAuditProperties().get("AUDIT_CRITERIA");

        if (pentahoHost == null)
        {
            log.warn("Property not found in reports property file : PENTAHO_SERVER_URL");
        }
        if (pentahoPort == null)
        {
            log.warn("Property not found in reports property file : PENTAHO_SERVER_PORT");
        }
        if (auditReportUri == null)
        {
            log.warn("Property not found in audit property file : AUDIT_REPORT");
        }
        if (auditCriteria == null)
        {
            log.warn("Property not found in audit property file : AUDIT_CRITERIA");
        }

        if (pentahoHost != null && pentahoPort != null && auditReportUri != null && auditCriteria != null)
        {
            String auditReportUrl = pentahoHost + pentahoPort + auditReportUri;
            retval.addObject("auditReportUrl", auditReportUrl);
            retval.addObject("auditCriteria", auditCriteria);
        }

        return retval;
    }

    public Map<String, String> getAuditProperties()
    {
        return auditProperties;
    }

    public void setAuditProperties(Map<String, String> auditProperties)
    {
        this.auditProperties = auditProperties;
    }

    public Map<String, String> getReportsProperties()
    {
        return reportsProperties;
    }

    public void setReportsProperties(Map<String, String> reportsProperties)
    {
        this.reportsProperties = reportsProperties;
    }

}
