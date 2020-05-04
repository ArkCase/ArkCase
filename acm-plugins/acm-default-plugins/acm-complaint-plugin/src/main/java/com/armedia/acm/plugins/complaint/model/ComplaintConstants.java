/**
 * 
 */
package com.armedia.acm.plugins.complaint.model;

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

/**
 * @author riste.tutureski
 *
 */
public interface ComplaintConstants
{
    String OBJECT_TYPE = "COMPLAINT";

    String XML_BATCH_CLASS_NAME_VALUE = "ComplaintTracking";
    String XML_BATCH_INCIDENT_CATEGORY_KEY = "IncidentCategory";
    String XML_BATCH_COMPLAINT_DESCRIPTION_KEY = "ComplaintDescription";
    String XML_BATCH_COMPLAINANT_FIRST_NAME_KEY = "ComplainantFirstName";
    String XML_BATCH_COMPLAINANT_LAST_NAME_KEY = "ComplainantLastName";
    String XML_BATCH_COMPLAINANT_STREET_ADDRESS_KEY = "ComplainantStreetAddress";
    String XML_BATCH_COMPLAINANT_CITY_KEY = "ComplainantCity";
    String XML_BATCH_COMPLAINANT_STATE_KEY = "ComplainantState";
    String XML_BATCH_COMPLAINANT_ZIP_CODE_KEY = "ComplainantZipCode";
    String XML_BATCH_COMPLAINANT_PHONE_KEY = "ComplainantPhone";
    String XML_BATCH_EMPLOYER_NAME_KEY = "EmployerName";
    String XML_BATCH_EMPLOYER_STREET_ADDRESS_KEY = "EmployerStreetAddress";
    String XML_BATCH_EMPLOYER_CITY_KEY = "EmployerCity";
    String XML_BATCH_EMPLOYER_STATE_KEY = "EmployerState";
    String XML_BATCH_EMPLOYER_ZIP_CODE_KEY = "EmployerZipCode";
    String XML_BATCH_EMPLOYER_PHONE_KEY = "EmployerPhone";
    String XML_BATCH_COMPLAINT_DOC_ID = "ComplaintDocID";
    String XML_BATCH_EVENT_TYPE = "CAPTURE";

    String ACTIVE_COMPLAINT_FORM_KEY = "active.complaint.form";
    String ACTIVE_CLOSE_COMPLAINT_FORM_KEY = "active.close.complaint.form";

    String OWNING_GROUP = "owning group";
    String ASSIGNEE = "assignee";

    String MIME_TYPE_PDF = "application/pdf";
    String NEW_FILE = "NEW_FILE";
    String FILE_ID = "FILE_ID";
    String FILE_VERSION = "FILE_VERSION";

    String NEW_COMPLAINT_TYPE = "New Complaint";
    // TODO '/acm-config-server-repo' is for temporal compatibility with current configuration.
    String COMPLAINT_STYLESHEET = System.getProperty("user.home") + "/.arkcase/acm/acm-config-server-repo/pdf-stylesheets/complaint-document.xsl";
    String COMPLAINT_DOCUMENT = "Complaint";
    String COMPLAINT_FILENAMEFORMAT = "Complaint.pdf";
}
