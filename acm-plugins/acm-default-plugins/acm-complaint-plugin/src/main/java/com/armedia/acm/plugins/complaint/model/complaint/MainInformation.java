/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

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
public class MainInformation {

	@XmlElements({
		@XmlElement(name="title"),
		@XmlElement(name="initiatorTitle"),
		@XmlElement(name="peopleTitle")
		
	})
	private String title;
	private List<String> titles;

	@XmlElements({
		@XmlElement(name="anonimuos"),
		@XmlElement(name="initiatorAnonimuos"),
		@XmlElement(name="peopleAnonimuos")
		
	})
	private String anonimuos;

	@XmlElements({
		@XmlElement(name="firstName"),
		@XmlElement(name="initiatorFirstName"),
		@XmlElement(name="peopleFirstName")
		
	})
	private String firstName;

	@XmlElements({
		@XmlElement(name="lastName"),
		@XmlElement(name="initiatorLastName"),
		@XmlElement(name="peopleLastName")
		
	})
	private String lastName;

	@XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="initiatorType"),
		@XmlElement(name="peopleType")
		
	})
	private String type;
	private List<String> types;

	@XmlElements({
		@XmlElement(name="description"),
		@XmlElement(name="initiatorDescription"),
		@XmlElement(name="peopleDescription")
		
	})
	private String description;
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the titles
	 */
	public List<String> getTitles() {
		return titles;
	}
	
	/**
	 * @param titles the titles to set
	 */
	public void setTitles(List<String> titles) {
		this.titles = titles;
	}
	
	/**
	 * @return the anonimuos
	 */
	public String getAnonimuos() {
		return anonimuos;
	}
	
	/**
	 * @param anonimuos the anonimuos to set
	 */
	public void setAnonimuos(String anonimuos) {
		this.anonimuos = anonimuos;
	}
	
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}
	
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}
	
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
}
