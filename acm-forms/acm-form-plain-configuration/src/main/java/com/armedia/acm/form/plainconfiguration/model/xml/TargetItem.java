/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
public class TargetItem extends Item {

	@Override
	@XmlElement(name="target")
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
}
