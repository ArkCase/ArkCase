package com.armedia.acm.plugins.audit.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
        
        if (pentahoHost != null && pentahoPort != null && auditReportUri != null)
        {
        	String auditReportUrl = pentahoHost + pentahoPort + auditReportUri;
        	retval.addObject("auditReportUrl", auditReportUrl);
        }
        
        return retval;
    }

	public Map<String, String> getAuditProperties() {
		return auditProperties;
	}

	public void setAuditProperties(Map<String, String> auditProperties) {
		this.auditProperties = auditProperties;
	}

	public Map<String, String> getReportsProperties() {
		return reportsProperties;
	}

	public void setReportsProperties(Map<String, String> reportsProperties) {
		this.reportsProperties = reportsProperties;
	}
    
    
}
