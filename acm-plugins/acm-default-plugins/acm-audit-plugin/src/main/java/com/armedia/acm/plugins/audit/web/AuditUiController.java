package com.armedia.acm.plugins.audit.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@RequestMapping("/plugin/audit")
public class AuditUiController
{
    private Logger log = LoggerFactory.getLogger(getClass());

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
