package com.armedia.acm.pentaho.config;

/*-
 * #%L
 * Tool Integrations: report Configuration
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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ReportName
{
    CASE_SUMMARY_REPORT("Case Summary Report"),
    CONSULTATION_SUMMARY_REPORT("Consultation Summary Report"),
    CLOSE_CASE_REPORT("Close Case Report"),
    OPEN_CASE_REPORT(
            "Open Case Report"),
    COMPLAINT_REPORT("Complaint Report"),
    COMPLAINT_DISPOSITION_COUNT(
            "Complaint Disposition Count"),
    BILLING_REPORT(
            "Billing Report"),
    BACKGROUND_INVESTIGATION_SUMMARY_REPORT("Background Investigations Report");

    protected transient static Logger log = LogManager
            .getLogger(ReportName.class);
    private String displayName;

    private ReportName(String displayName)
    {
        this.displayName = displayName;
    }

    public static ReportName fromString(String name)
    {
        String s = org.apache.commons.lang.StringUtils.trimToEmpty(name);
        for (ReportName dct : values())
        {
            if (dct.getDisplayName().equalsIgnoreCase(s))
            {
                return dct;
            }
        }
        log.info("Could not found match for property: {}", name);
        return null;
    }

    public String getDisplayName()
    {
        return displayName;
    }

}
