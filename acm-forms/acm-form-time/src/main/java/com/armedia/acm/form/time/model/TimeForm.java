/**
 * 
 */
package com.armedia.acm.form.time.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;

/**
 * @author riste.tutureski
 *
 */
public class TimeForm {

	private Long id;
	private String user;
	private List<String> userOptions;
	private String type;
	private List<String> codeOptions;
	private Date period;
	private List<TimeItem> items;
	private String status;
	private List<String> statusOptions;
	
	@XmlElement(name="id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlElement(name="user")
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@XmlTransient
	public List<String> getUserOptions() {
		return userOptions;
	}

	public void setUserOptions(List<String> userOptions) {
		this.userOptions = userOptions;
	}	

	@XmlElement(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@XmlTransient
	public List<String> getCodeOptions() {
		return codeOptions;
	}

	public void setCodeOptions(List<String> codeOptions) {
		this.codeOptions = codeOptions;
	}
	
	@XmlElement(name="period")
	@XmlJavaTypeAdapter(value=DateFrevvoAdapter.class)
	public Date getPeriod() {
		return period;
	}
	
	public void setPeriod(Date period) {
		this.period = period;
	}
	
	@XmlElement(name="timeTableItem")
	public List<TimeItem> getItems() {
		return items;
	}
	
	public void setItems(List<TimeItem> items) {
		this.items = items;
	}

	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlTransient
	public List<String> getStatusOptions() {
		return statusOptions;
	}

	public void setStatusOptions(List<String> statusOptions) {
		this.statusOptions = statusOptions;
	}
}
