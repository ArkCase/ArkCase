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
public class OwningGroupItem extends Item {

	private String type;
	
	@XmlElement(name="owningGroupId")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
	
	@XmlElement(name="owningGroupValue")
	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
	@XmlElement(name="owningGroupType")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
}
