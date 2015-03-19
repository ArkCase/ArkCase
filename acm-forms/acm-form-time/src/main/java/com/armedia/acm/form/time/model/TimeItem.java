/**
 * 
 */
package com.armedia.acm.form.time.model;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class TimeItem {

	private String type;
	private List<String> typeOptions;
	
	private Long objectId;
	
	private String code;
	private Map<String, List<String>> codeOptions;
	
	private Long sundayId;
	private Double sunday;
	
	private Long mondayId;
	private Double monday;
	
	private Long tuesdayId;
	private Double tuesday;
	
	private Long wednesdayId;
	private Double wednesday;
	
	private Long thursdayId;
	private Double thursday;
	
	private Long fridayId;
	private Double friday;
	
	private Long saturdayId;
	private Double saturday;
	
	@XmlElement(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@XmlTransient
	public List<String> getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(List<String> typeOptions) {
		this.typeOptions = typeOptions;
	}

	@XmlElement(name="objectId")
	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	@XmlElement(name="code")
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@XmlTransient
	public Map<String, List<String>> getCodeOptions() {
		return codeOptions;
	}

	public void setCodeOptions(Map<String, List<String>> codeOptions) {
		this.codeOptions = codeOptions;
	}

	@XmlElement(name="sundayId")
	public Long getSundayId() {
		return sundayId;
	}

	public void setSundayId(Long sundayId) {
		this.sundayId = sundayId;
	}

	@XmlElement(name="sunday")
	public Double getSunday() {
		return sunday;
	}
	
	public void setSunday(Double sunday) {
		this.sunday = sunday;
	}
	
	@XmlElement(name="mondayId")
	public Long getMondayId() {
		return mondayId;
	}

	public void setMondayId(Long mondayId) {
		this.mondayId = mondayId;
	}

	@XmlElement(name="monday")
	public Double getMonday() {
		return monday;
	}
	
	public void setMonday(Double monday) {
		this.monday = monday;
	}
	
	@XmlElement(name="tuesdayId")
	public Long getTuesdayId() {
		return tuesdayId;
	}

	public void setTuesdayId(Long tuesdayId) {
		this.tuesdayId = tuesdayId;
	}

	@XmlElement(name="tuesday")
	public Double getTuesday() {
		return tuesday;
	}
	
	public void setTuesday(Double tuesday) {
		this.tuesday = tuesday;
	}
	
	@XmlElement(name="wednesdayId")
	public Long getWednesdayId() {
		return wednesdayId;
	}

	public void setWednesdayId(Long wednesdayId) {
		this.wednesdayId = wednesdayId;
	}

	@XmlElement(name="wednesday")
	public Double getWednesday() {
		return wednesday;
	}
	
	public void setWednesday(Double wednesday) {
		this.wednesday = wednesday;
	}
	
	@XmlElement(name="thursdayId")
	public Long getThursdayId() {
		return thursdayId;
	}

	public void setThursdayId(Long thursdayId) {
		this.thursdayId = thursdayId;
	}

	@XmlElement(name="thursday")
	public Double getThursday() {
		return thursday;
	}
	
	public void setThursday(Double thursday) {
		this.thursday = thursday;
	}
	
	@XmlElement(name="fridayId")
	public Long getFridayId() {
		return fridayId;
	}

	public void setFridayId(Long fridayId) {
		this.fridayId = fridayId;
	}

	@XmlElement(name="friday")
	public Double getFriday() {
		return friday;
	}
	
	public void setFriday(Double friday) {
		this.friday = friday;
	}
	
	@XmlElement(name="saturdayId")
	public Long getSaturdayId() {
		return saturdayId;
	}

	public void setSaturdayId(Long saturdayId) {
		this.saturdayId = saturdayId;
	}

	@XmlElement(name="saturday")
	public Double getSaturday() {
		return saturday;
	}
	
	public void setSaturday(Double saturday) {
		this.saturday = saturday;
	}
	
}
