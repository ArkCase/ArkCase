/**
 * 
 */
package com.armedia.acm.form.config;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class ResolveInformation {

	private Long id;
	private String number;
	private Date date;
	private String option;
	private List<String> resolveOptions;
	
	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlTransient
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@XmlTransient
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@XmlTransient
	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	@XmlTransient
	public List<String> getResolveOptions() {
		return resolveOptions;
	}

	public void setResolveOptions(List<String> resolveOptions) {
		this.resolveOptions = resolveOptions;
	}
	
}
