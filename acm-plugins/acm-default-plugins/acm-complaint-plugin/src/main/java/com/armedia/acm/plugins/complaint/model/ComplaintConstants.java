/**
 * 
 */
package com.armedia.acm.plugins.complaint.model;

/**
 * @author riste.tutureski
 *
 */
public interface ComplaintConstants {

	String OBJECT_TYPE = "COMPLAINT";

	String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

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
}
