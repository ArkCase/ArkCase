/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model.xml;

import com.armedia.acm.form.config.Item;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class TargetItem extends Item
{

    @Override
    @XmlElement(name = "target")
    public String getValue()
    {
        return super.getValue();
    }

    @Override
    public void setValue(String value)
    {
        super.setValue(value);
    }

}
