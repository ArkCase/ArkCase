/**
 * 
 */
package com.armedia.acm.frevvo.model;

import java.util.Map;

/**
 * @author riste.tutureski
 *
 */
public class OptionsAndDetailsByType {

	private Map<String, Options> optionsByType;
	private Map<String, Map<String, Details>> optionsDetailsByType;
	
	public Map<String, Options> getOptionsByType() {
		return optionsByType;
	}
	
	public void setOptionsByType(Map<String, Options> optionsByType) {
		this.optionsByType = optionsByType;
	}

	public Map<String, Map<String, Details>> getOptionsDetailsByType() {
		return optionsDetailsByType;
	}

	public void setOptionsDetailsByType(
			Map<String, Map<String, Details>> optionsDetailsByType) {
		this.optionsDetailsByType = optionsDetailsByType;
	}
}
