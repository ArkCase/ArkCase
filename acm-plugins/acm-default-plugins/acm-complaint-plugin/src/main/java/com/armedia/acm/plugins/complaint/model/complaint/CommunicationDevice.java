package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunicationDevice {

	@XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="initiatorDeviceType"),
		@XmlElement(name="peopleDeviceType")
		
	})
	private String type;
	private List<String> types;

	@XmlElements({
		@XmlElement(name="value"),
		@XmlElement(name="initiatorDeviceValue"),
		@XmlElement(name="peopleDeviceValue")
		
	})
	private String value;

	@XmlElements({
		@XmlElement(name="date"),
		@XmlElement(name="initiatorDeviceDate"),
		@XmlElement(name="peopleDeviceDate")
		
	})
	private Date date;

	@XmlElements({
		@XmlElement(name="creator"),
		@XmlElement(name="initiatorDeviceAddedBy"),
		@XmlElement(name="peopleDeviceAddedBy")
		
	})
	private String creator;
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the types
	 */
	public List<String> getTypes() {
		return types;
	}
	
	/**
	 * @param types the types to set
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
}
