/**
 * 
 */
package com.armedia.acm.frevvo.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class FrevvoForm {

	private String mode;
	private Long containerId;
	private Long folderId;
	private String docUriParameters;
	
	@XmlElement(name="mode")
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}

	@XmlElement(name="containerId")
	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	@XmlElement(name="folderId")
	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}

	@XmlElement(name="docUriParameters")
	public String getDocUriParameters() {
		return docUriParameters;
	}

	public void setDocUriParameters(String docUriParameters) {
		this.docUriParameters = docUriParameters;
	}
}
