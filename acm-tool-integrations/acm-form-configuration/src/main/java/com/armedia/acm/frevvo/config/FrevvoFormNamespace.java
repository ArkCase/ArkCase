/**
 * 
 */
package com.armedia.acm.frevvo.config;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoFormNamespace {

	/**
	 * This is namespace for ComplaintForm object while we creating XML 
	 * 
	 * The namespace can be found on Frevvo Complaint form schema. To access the schema,
	 * login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application,
	 * press button "Schema" button under "Complaint" form. The "Complaint.xsd" shcema will be downloaded.
	 * Open it and find "targetNamespace" or "xmlns" in the first tag "<xsd:schema ...>"
	 */
	public static final String COMPLAINT_NAMESPACE = "http://www.frevvo.com/schemas/_JtTqMC7fEeS5l-bMPzqvwA";
	
	/**
	 * This is namespace for CaseFileForm object while we creating XML 
	 * 
	 * The namespace can be found on Frevvo Case File form schema. To access the schema,
	 * login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application,
	 * press button "Schema" button under "Case File" form. The "Case File.xsd" schema will be downloaded.
	 * Open it and find "targetNamespace" or "xmlns" value in the first tag "<xsd:schema ...>"
	 */
	public static final String CASE_FILE_NAMESPACE = "http://www.frevvo.com/schemas/_jTrYoLXwEeSNKN7wfymqgA";
	
	/**
	 * This is namespace for CaseFilePSForm object while we creating XML (This is only for PS application)
	 * 
	 * The namespace can be found on Frevvo Case File form schema. To access the schema,
	 * login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application,
	 * press button "Schema" button under "Case File" form. The "Case File.xsd" schema will be downloaded.
	 * Open it and find "targetNamespace" or "xmlns" value in the first tag "<xsd:schema ...>"
	 */
	public static final String CASE_FILE_PS_NAMESPACE = "http://www.frevvo.com/schemas/_CquK8HoPEeS1j4PPlP9imA";
	
	/**
	 * This is namespace for TimeForm object while we creating XML
	 * 
	 * The namespace can be found on Frevvo Time form schema. To access the schema,
	 * login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application,
	 * press button "Schema" button under "Time" form. The "Time.xsd" schema will be downloaded.
	 * Open it and find "targetNamespace" or "xmlns" value in the first tag "<xsd:schema ...>"
	 */
	public static final String TIME_NAMESPACE = "http://www.frevvo.com/schemas/_K2MQkLxHEeSms-PrS7te7w";
	
	/**
	 * This is namespace for CostForm object while we creating XML
	 * 
	 * The namespace can be found on Frevvo Cost form schema. To access the schema,
	 * login to Frevvo, press "Edit" button under "ACM - Armedia Case Management" application,
	 * press button "Schema" button under "Cost" form. The "Cost.xsd" schema will be downloaded.
	 * Open it and find "targetNamespace" or "xmlns" value in the first tag "<xsd:schema ...>"
	 */
	public static final String COST_NAMESPACE = "http://www.frevvo.com/schemas/_YPmMQL2oEeSmjJjf63cgRw";
	
}
