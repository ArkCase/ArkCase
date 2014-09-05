package com.armedia.acm.pentaho.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ReportName {
	COMPLAINT_REPORT("Complaint Report"),
	BILLING_REPORT("Billing Report");

	protected transient static Logger log = LoggerFactory
			.getLogger(ReportName.class);
	
	ReportName(String displayName) {
		this.displayName = displayName;		
	}
	
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }
    public void setDescription(String displayName) {
        this.displayName = displayName;
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
