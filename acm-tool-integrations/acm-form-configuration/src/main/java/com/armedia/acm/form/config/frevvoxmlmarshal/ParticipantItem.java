/**
 * 
 */
package com.armedia.acm.form.config.frevvoxmlmarshal;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
public class ParticipantItem  extends Item{
	
	private String type;

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

}
