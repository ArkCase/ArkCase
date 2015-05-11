/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class BoxOneSection {

	private Date date;
	private String check;
	private String jpsjsm;
	private String sol;
	private String remand;
	private String bail;
	private String bailText;
	private String remandedTo;
	private String remandedAt;
	private String remandedReason;
	private String prosecutor;
	private String prosecutorId;
	
	@XmlElement(name="boxOneDateOn")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	@XmlElement(name="boxOneCheck")
	public String getCheck() {
		return check;
	}
	
	public void setCheck(String check) {
		this.check = check;
	}
	
	@XmlElement(name="boxOneJPSJSM")
	public String getJpsjsm() {
		return jpsjsm;
	}
	
	public void setJpsjsm(String jpsjsm) {
		this.jpsjsm = jpsjsm;
	}
	
	@XmlElement(name="boxOneSol")
	public String getSol() {
		return sol;
	}
	
	public void setSol(String sol) {
		this.sol = sol;
	}
	
	@XmlElement(name="boxOneRemand")
	public String getRemand() {
		return remand;
	}
	
	public void setRemand(String remand) {
		this.remand = remand;
	}
	
	@XmlElement(name="boxOneBail")
	public String getBail() {
		return bail;
	}
	
	public void setBail(String bail) {
		this.bail = bail;
	}
	
	@XmlElement(name="boxOneBailText")
	public String getBailText() {
		return bailText;
	}
	
	public void setBailText(String bailText) {
		this.bailText = bailText;
	}
	
	@XmlElement(name="boxOneRemandedTo")
	public String getRemandedTo() {
		return remandedTo;
	}
	
	public void setRemandedTo(String remandedTo) {
		this.remandedTo = remandedTo;
	}
	
	@XmlElement(name="boxOneRemandedAt")
	public String getRemandedAt() {
		return remandedAt;
	}

	public void setRemandedAt(String remandedAt) {
		this.remandedAt = remandedAt;
	}

	@XmlElement(name="boxOneRemandedReason")
	public String getRemandedReason() {
		return remandedReason;
	}
	
	public void setRemandedReason(String remandedReason) {
		this.remandedReason = remandedReason;
	}
	
	@XmlElement(name="boxOneProsecutor")
	public String getProsecutor() {
		return prosecutor;
	}
	
	public void setProsecutor(String prosecutor) {
		this.prosecutor = prosecutor;
	}
	
	@XmlElement(name="boxOneId")
	public String getProsecutorId() {
		return prosecutorId;
	}
	
	public void setProsecutorId(String prosecutorId) {
		this.prosecutorId = prosecutorId;
	}
	
}
