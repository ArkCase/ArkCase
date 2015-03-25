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
public class ApproverItem extends Item{

	@XmlElement(name="participantId")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
	
	@XmlElement(name="approverId")
	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
}
