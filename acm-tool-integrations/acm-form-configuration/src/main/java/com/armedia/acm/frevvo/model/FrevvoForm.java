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
	private String xmlId;
	private String pdfId;
	private String docUriParameters;
	
	@XmlElement(name="mode")
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	@XmlElement(name="xmlId")
	public String getXmlId() {
		return xmlId;
	}
	
	public void setXmlId(String xmlId) {
		this.xmlId = xmlId;
	}
	
	@XmlElement(name="pdfId")
	public String getPdfId() {
		return pdfId;
	}
	
	public void setPdfId(String pdfId) {
		this.pdfId = pdfId;
	}

	@XmlElement(name="docUriParameters")
	public String getDocUriParameters() {
		return docUriParameters;
	}

	public void setDocUriParameters(String docUriParameters) {
		this.docUriParameters = docUriParameters;
	}
}
