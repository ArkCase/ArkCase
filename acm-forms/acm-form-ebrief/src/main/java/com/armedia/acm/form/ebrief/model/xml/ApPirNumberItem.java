/**
 * 
 */
package com.armedia.acm.form.ebrief.model.xml;

import javax.xml.bind.annotation.XmlElement;

import com.armedia.acm.form.config.Item;

/**
 * @author riste.tutureski
 *
 */
public class ApPirNumberItem extends Item {
	
	@XmlElement(name="apPirNumber")
	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public void setValue(String value) {
		super.setValue(value);
	}
	
}
