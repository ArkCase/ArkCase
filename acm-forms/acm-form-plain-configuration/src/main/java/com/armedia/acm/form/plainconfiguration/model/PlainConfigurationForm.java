/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.plainconfiguration.model.xml.UrlParameterItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoFormConstants;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name=FrevvoFormConstants.ELEMENT_KEY_PREFIX + FrevvoFormName.PLAIN_CONFIGURATION, namespace=FrevvoFormNamespace.PLAIN_CONFIGURATION_NAMESPACE)
public class PlainConfigurationForm {

	private String key;
	private String formId;
	private List<String> formOptions;
	private String name;
	private String type;
	private String mode;
	private String target;
	private List<String> targetOptions;
	private String description;
	private List<UrlParameterItem> urlParameters;
	private List<String> requiredUrlParemeters;
	private List<String> objectPropertiesOptions;
	private String url;
	
	
	@XmlElement(name="key")
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlElement(name="formId")
	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	@XmlTransient
	public List<String> getFormOptions() {
		return formOptions;
	}

	public void setFormOptions(List<String> formOptions) {
		this.formOptions = formOptions;
	}

	@XmlElement(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@XmlElement(name="formMode")
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@XmlElement(name="target")
	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
	@XmlTransient
	public List<String> getTargetOptions() {
		return targetOptions;
	}

	public void setTargetOptions(List<String> targetOptions) {
		this.targetOptions = targetOptions;
	}

	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@XmlElement(name="urlParametersItem")
	public List<UrlParameterItem> getUrlParameters() {
		return urlParameters;
	}
	
	public void setUrlParameters(List<UrlParameterItem> urlParameters) {
		this.urlParameters = urlParameters;
	}	
	
	@XmlTransient
	public List<String> getRequiredUrlParemeters() {
		return requiredUrlParemeters;
	}

	public void setRequiredUrlParemeters(List<String> requiredUrlParemeters) {
		this.requiredUrlParemeters = requiredUrlParemeters;
	}

	@XmlTransient
	public List<String> getObjectPropertiesOptions() {
		return objectPropertiesOptions;
	}

	public void setObjectPropertiesOptions(List<String> objectPropertiesOptions) {
		this.objectPropertiesOptions = objectPropertiesOptions;
	}

	@XmlTransient
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
