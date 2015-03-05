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

	private Long id;
	private String type;
	private List<String> typeOptions;
	private String code;
	private Map<String, List<String>> codeOptions;
	private long sunday;
	private long monday;
	private long tuesday;
	private long wednesday;
	private long thursday;
	private long friday;
	private long saturday;
	
	@XmlElement(name="timeId")
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
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

	@XmlElement(name="sunday")
	public long getSunday() {
		return sunday;
	}
	
	public void setSunday(long sunday) {
		this.sunday = sunday;
	}
	
	@XmlElement(name="monday")
	public long getMonday() {
		return monday;
	}
	
	public void setMonday(long monday) {
		this.monday = monday;
	}
	
	@XmlElement(name="tuesday")
	public long getTuesday() {
		return tuesday;
	}
	
	public void setTuesday(long tuesday) {
		this.tuesday = tuesday;
	}
	
	@XmlElement(name="wednesday")
	public long getWednesday() {
		return wednesday;
	}
	
	public void setWednesday(long wednesday) {
		this.wednesday = wednesday;
	}
	
	@XmlElement(name="thursday")
	public long getThursday() {
		return thursday;
	}
	
	public void setThursday(long thursday) {
		this.thursday = thursday;
	}
	
	@XmlElement(name="friday")
	public long getFriday() {
		return friday;
	}
	
	public void setFriday(long friday) {
		this.friday = friday;
	}
	
	@XmlElement(name="saturday")
	public long getSaturday() {
		return saturday;
	}
	
	public void setSaturday(long saturday) {
		this.saturday = saturday;
	}
	
}
