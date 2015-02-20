/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @author riste.tutureski
 *
 */
public class MainInformation {

	private String title;
	private List<String> titles;
	private String anonymous;
	private String firstName;
	private String lastName;
	private String type;
	private List<String> types;
	private String description;
	
	@XmlTransient
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	@XmlTransient
	public List<String> getTitles() {
		return titles;
	}
	
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
	@XmlTransient
	public String getAnonymous() {
		return anonymous;
	}

	public void setAnonymous(String anonymous) {
		this.anonymous = anonymous;
	}

	@XmlTransient
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlTransient
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlTransient
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

	@XmlTransient
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
