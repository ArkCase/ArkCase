/**
 * 
 */
package com.armedia.acm.plugins.report.model;

import java.io.Serializable;

/**
 * @author riste.tutureski
 *
 */
public class ReportProperty implements Serializable{

	private static final long serialVersionUID = -9014562602560002389L;
	
	private String key;
	private String value;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
