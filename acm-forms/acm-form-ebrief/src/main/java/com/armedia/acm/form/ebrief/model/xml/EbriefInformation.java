/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
public class EbriefInformation {

	private String type;
	private List<String> types;
	private String number;
	private String sapolNumber;
	private String odppNumber;
	private String courtCaseNumber;
	private List<Item> apPirNumbers;
	
	@XmlElement(name="eBriefType")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlTransient
	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	@XmlElement(name="eBriefNumber")
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	@XmlElement(name="sapolEBriefNumber")
	public String getSapolNumber() {
		return sapolNumber;
	}
	
	public void setSapolNumber(String sapolNumber) {
		this.sapolNumber = sapolNumber;
	}
	
	@XmlElement(name="odppCaseTrackingNumber")
	public String getOdppNumber() {
		return odppNumber;
	}
	
	public void setOdppNumber(String odppNumber) {
		this.odppNumber = odppNumber;
	}
	
	@XmlElement(name="courtCaseFileNumber")
	public String getCourtCaseNumber() {
		return courtCaseNumber;
	}
	
	public void setCourtCaseNumber(String courtCaseNumber) {
		this.courtCaseNumber = courtCaseNumber;
	}
	
	@XmlElement(name="apPirNumberTableItem", type=ApPirNumberItem.class)
	public List<Item> getApPirNumbers() {
		return apPirNumbers;
	}
	
	public void setApPirNumbers(List<Item> apPirNumbers) {
		this.apPirNumbers = apPirNumbers;
	}
	
}
