/**
 *
 */
package com.armedia.acm.frevvo.config;

/*-
 * #%L
 * ACM Service: Form Configuration
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

/**
 * @author riste.tutureski
 */
public class FrevvoFormNamespace
{

    /**
     * This is namespace for ComplaintForm object while we creating XML The namespace can be found on Frevvo Complaint
     * form schema. To
     * access the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application, press
     * button "Schema"
     * button under "Complaint" form. The "Complaint.xsd" shcema will be downloaded. Open it and find "targetNamespace"
     * or "xmlns" in the
     * first tag "<xsd:schema ...>"
     */
    public static final String COMPLAINT_NAMESPACE = "http://www.frevvo.com/schemas/_JtTqMC7fEeS5l-bMPzqvwA";

    /**
     * This is namespace for CaseFileForm object while we creating XML The namespace can be found on Frevvo Case File
     * form schema. To access
     * the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application, press button
     * "Schema" button
     * under "Case File" form. The "Case File.xsd" schema will be downloaded. Open it and find "targetNamespace" or
     * "xmlns" value in the
     * first tag "<xsd:schema ...>"
     */
    public static final String CASE_FILE_NAMESPACE = "http://www.frevvo.com/schemas/_jTrYoLXwEeSNKN7wfymqgA";

    /**
     * This is namespace for TimesheetForm object while we creating XML The namespace can be found on Frevvo Timesheet
     * form schema. To
     * access the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application, press
     * button "Schema"
     * button under "Timesheet" form. The "Timesheet.xsd" schema will be downloaded. Open it and find "targetNamespace"
     * or "xmlns" value in
     * the first tag "<xsd:schema ...>"
     */
    public static final String TIMESHEET_NAMESPACE = "http://www.frevvo.com/schemas/_K2MQkLxHEeSms-PrS7te7w";

    /**
     * This is namespace for CostsheetForm object while we creating XML The namespace can be found on Frevvo Costsheet
     * form schema. To
     * access the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application, press
     * button "Schema"
     * button under "Costsheet" form. The "Costsheet.xsd" schema will be downloaded. Open it and find "targetNamespace"
     * or "xmlns" value in
     * the first tag "<xsd:schema ...>"
     */
    public static final String COSTSHEET_NAMESPACE = "http://www.frevvo.com/schemas/_YPmMQL2oEeSmjJjf63cgRw";

    /**
     * This is namespace for ProjectForm object while we creating XML The namespace can be found on Frevvo Project form
     * schema. To access
     * the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application, press button
     * "Schema" button
     * under "Project" form. The "Project.xsd" schema will be downloaded. Open it and find "targetNamespace" or "xmlns"
     * value in the first
     * tag "<xsd:schema ...>"
     */
    public static final String PROJECT_NAMESPACE = "http://www.frevvo.com/schemas/_wrtuYPoYEeSwvupLu0BE2g";

    /**
     * This is namespace for PlainConfigurationForm object while we creating XML The namespace can be found on Frevvo
     * Plain Configuration
     * form schema. To access the schema, login to Frevvo, press "Edit" button under "ACM - Armedia Case Management"
     * application, press
     * button "Schema" button under "Plain Configuration" form. The "PlainConfiguration.xsd" schema will be downloaded.
     * Open it and find
     * "targetNamespace" or "xmlns" value in the first tag "<xsd:schema ...>"
     */
    public static final String PLAIN_CONFIGURATION_NAMESPACE = "http://www.frevvo.com/schemas/_e5-gwAkvEeWTa_9lG6es1A";

}
