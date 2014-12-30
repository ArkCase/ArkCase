/**
 * 
 */
package com.armedia.acm.plugins.complaint.model.complaint;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParticipantItem extends Item {

	@XmlElements({
		@XmlElement(name="type"),
		@XmlElement(name="participantType")
	})
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
