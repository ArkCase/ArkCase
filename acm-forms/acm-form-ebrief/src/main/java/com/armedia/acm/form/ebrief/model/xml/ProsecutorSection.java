/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.xml.ProsecutorContactMethod;

/**
 * @author riste.tutureski
 *
 */
public class ProsecutorSection {

	private String solicitor;
	private List<ContactMethod> contacts;
	private String timeCustody;
	private String bond;
	private String applicationLaid;
	private String ilol;
	private String months;
	private String ilolLifted;
	private Date dateLifted;
	private String impoundingVehicle;
	private String impoundingFee;
	private String vehicleReleased;
	private Date vehiceReleasedDate;
	private String compensation;
	private String amountSought;
	private Date licenceExpiryDate;
	private String licenceExpiryText;
	private String courtCode;
	private Date dateTime;
	
	@XmlElement(name="prosecutorSolicitor")
	public String getSolicitor() {
		return solicitor;
	}
	
	public void setSolicitor(String solicitor) {
		this.solicitor = solicitor;
	}
	
	@XmlElement(name="prosecutorContact", type=ProsecutorContactMethod.class)
	public List<ContactMethod> getContacts() {
		return contacts;
	}
	
	public void setContacts(List<ContactMethod> contacts) {
		this.contacts = contacts;
	}
	
	@XmlElement(name="prosecutorTimeCustody")
	public String getTimeCustody() {
		return timeCustody;
	}
	
	public void setTimeCustody(String timeCustody) {
		this.timeCustody = timeCustody;
	}
	
	@XmlElement(name="prosecutorBond")
	public String getBond() {
		return bond;
	}
	
	public void setBond(String bond) {
		this.bond = bond;
	}
	
	@XmlElement(name="prosecutorApplicationLaid")
	public String getApplicationLaid() {
		return applicationLaid;
	}
	
	public void setApplicationLaid(String applicationLaid) {
		this.applicationLaid = applicationLaid;
	}
	
	@XmlElement(name="prosecutorIlol")
	public String getIlol() {
		return ilol;
	}
	
	public void setIlol(String ilol) {
		this.ilol = ilol;
	}
	
	@XmlElement(name="prosecutorIlolMonths")
	public String getMonths() {
		return months;
	}
	
	public void setMonths(String months) {
		this.months = months;
	}
	
	@XmlElement(name="prosecutorIlolLifted")
	public String getIlolLifted() {
		return ilolLifted;
	}
	
	public void setIlolLifted(String ilolLifted) {
		this.ilolLifted = ilolLifted;
	}
	
	@XmlElement(name="prosecutorDateLifted")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getDateLifted() {
		return dateLifted;
	}
	
	public void setDateLifted(Date dateLifted) {
		this.dateLifted = dateLifted;
	}
	
	@XmlElement(name="prosecutorImpoundingVehicle")
	public String getImpoundingVehicle() {
		return impoundingVehicle;
	}
	
	public void setImpoundingVehicle(String impoundingVehicle) {
		this.impoundingVehicle = impoundingVehicle;
	}
	
	@XmlElement(name="prosecutorImpoundingFee")
	public String getImpoundingFee() {
		return impoundingFee;
	}
	
	public void setImpoundingFee(String impoundingFee) {
		this.impoundingFee = impoundingFee;
	}
	
	@XmlElement(name="prosecutorVehicleReleased")
	public String getVehicleReleased() {
		return vehicleReleased;
	}
	
	public void setVehicleReleased(String vehicleReleased) {
		this.vehicleReleased = vehicleReleased;
	}
	
	@XmlElement(name="prosecutorVehicleReleasedDate")
	public Date getVehiceReleasedDate() {
		return vehiceReleasedDate;
	}
	
	public void setVehiceReleasedDate(Date vehiceReleasedDate) {
		this.vehiceReleasedDate = vehiceReleasedDate;
	}
	
	@XmlElement(name="prosecutorCompensation")
	public String getCompensation() {
		return compensation;
	}
	
	public void setCompensation(String compensation) {
		this.compensation = compensation;
	}
	
	@XmlElement(name="prosecutorAmountSought")
	public String getAmountSought() {
		return amountSought;
	}
	
	public void setAmountSought(String amountSought) {
		this.amountSought = amountSought;
	}
	
	@XmlElement(name="prosecutorLicenseExpiryDate")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getLicenceExpiryDate() {
		return licenceExpiryDate;
	}
	
	public void setLicenceExpiryDate(Date licenceExpiryDate) {
		this.licenceExpiryDate = licenceExpiryDate;
	}
	
	@XmlElement(name="prosecutorLicenseExpiryText")
	public String getLicenceExpiryText() {
		return licenceExpiryText;
	}
	
	public void setLicenceExpiryText(String licenceExpiryText) {
		this.licenceExpiryText = licenceExpiryText;
	}
	
	@XmlElement(name="prosecutorCourtCode")
	public String getCourtCode() {
		return courtCode;
	}
	
	public void setCourtCode(String courtCode) {
		this.courtCode = courtCode;
	}
	
	@XmlElement(name="prosecutorDateTime")
	public Date getDateTime() {
		return dateTime;
	}
	
	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}
	
}
