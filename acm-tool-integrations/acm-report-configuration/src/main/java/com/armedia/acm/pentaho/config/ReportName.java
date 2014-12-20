package com.armedia.acm.pentaho.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ReportName {
	CASE_SUMMARY_REPORT("Case Summary Report"),
	CLOSE_CASE_REPORT("Close Case Report"),
	OPEN_CASE_REPORT("Open Case Report"),
	COMPLAINT_REPORT("Complaint Report"),
	BILLING_REPORT("Billing Report"),
    BACKGROUND_INVESTIGATION_SUMMARY_REPORT("Background Investigations Report");

	protected transient static Logger log = LoggerFactory
			.getLogger(ReportName.class);
	
	private ReportName(String displayName) {
		this.displayName = displayName;		
	}
	
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public static ReportName fromString(String name)
    {
        String s = org.apache.commons.lang.StringUtils.trimToEmpty(name);
        for ( ReportName dct : values())
        {
            if (dct.getDisplayName().equalsIgnoreCase(s))
            {
                return dct;
            }
        }       
		log.info("Could not found match for property: {}", name);
        return null;
    }
    
}
