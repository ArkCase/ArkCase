/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class UrlParameterItem {

	/**
	 * The name of parameter that will be sent to the Frevvo form
	 */
	private String name;
	
	/**
	 * Default (hardcoded) value that will be sent to the Frevvo form.
	 * 
	 * Note: If the value should be taken from the object, this should be blank and "keyValue" should contain the property name
	 * of the object from where the value should be taken
	 */
	private String defaultValue;
	
	/**
	 * Dynamic value that will be sent to the Frevvo form. It's taken from object itself.
	 * 
	 * Note: If the value should be hardcoded, this should be blank and "defaultValue" should contain the value
	 */
	private String keyValue;
	
	/**
	 * Indicate if the property is required or not. We have several required properties that are configured in the
	 * poperties file
	 */
	private boolean required;
	
	@XmlElement(name="propertyName")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="propertyDefaultValue")
	public String getDefaultValue() {
		return defaultValue;
	}
	
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlElement(name="propertyKeyValue")
	public String getKeyValue() {
		return keyValue;
	}
	
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	
	@XmlElement(name="propertyRequired")
	public boolean getRequired() {
		return required;
	}
	
	public void setRequired(boolean required) {
		this.required = required;
	}	
}
