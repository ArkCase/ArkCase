package com.armedia.acm.orbeon.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.armedia.acm.form.config.FormUrl;

public class OrbeonFormUrl implements FormUrl{
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String FORM_SERVER_URL = "ORBEON_SERVER_URL";
    private static final String FORM_SERVER_PORT = "ORBEON_SERVER_PORT";

    /**
     * List of form-specific properties.
     */
    private Map<String, Object> formProperties = new HashMap<>();
    
	public Map<String, Object> getFormProperties() {
		return formProperties;
	}

	public void setFormProperties(Map<String, Object> formProperties) {
		this.formProperties = formProperties;
	}

	public String getFormURL() {
        //form url data
        StringBuilder builder = new StringBuilder();
        String serverFormUrl = getFormProperties().get(FORM_SERVER_URL).toString();
        builder.append(serverFormUrl);
        String serverFormPort = getFormProperties().get(FORM_SERVER_PORT).toString();
        builder.append(serverFormPort);
        String url = builder.toString();
        log.debug("getFormURL(): " + url);
        return url;
	}

	/**
	 * Url path for create a new ROI form in Orbeon.
	 * 
	 * @return
	 */
	public String getCreateROIUrlPath(String formName) {
        StringBuilder builder = new StringBuilder();
        String serverFormUrl = getFormProperties().get(FORM_SERVER_URL).toString();
        builder.append(serverFormUrl);
        String serverFormPort = getFormProperties().get(FORM_SERVER_PORT).toString();
        builder.append(serverFormPort);
		String pathStr = getFormProperties().get(formName).toString();
        builder.append(pathStr);
		String path = builder.toString();
        log.debug("getCreateROIPath(): " + path);
		return path;
	}

	@Override
	public String getNewFormUrl(String formName) {
		return getCreateROIUrlPath(formName);
	}

	@Override
	public String getPdfRenditionUrl(String formName, String docId) {
		// TODO Auto-generated method stub
		return null;
	}
}
