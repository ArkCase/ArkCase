/**
 * 
 */
package com.armedia.acm.form.config.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
public class ParticipantItem  extends Item{
	
	private String type;
	private String name;

	@XmlElement(name="participantId")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
	
	@XmlElement(name="participant")
	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
	@XmlElement(name="participantType")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="participantName")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
