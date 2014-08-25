package com.armedia.acm.form.config;

public interface FormUrl {
	/**
	 * Retrieve the form server url based on the form name.
	 * 
	 * @param formName
	 * @return
	 */
	public String getNewFormUrl(String formName);
	
	/**
	 * Retrieve the form server url for the PDF attachment base on the form name.
	 * 
	 * @param formName
	 * @param docId
	 * @return
	 */
	public String getPdfRenditionUrl(String formName, String docId);
}
