/**
 * 
 */
package com.armedia.acm.form.plainconfiguration.model.xml;

/*-
 * #%L
 * ACM Forms: Plain Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import javax.xml.bind.annotation.XmlElement;

/**
 * @author riste.tutureski
 *
 */
public class UrlParameterItem
{

    /**
     * The name of parameter that will be sent to the Frevvo form
     */
    private String name;

    /**
     * Default (hardcoded) value that will be sent to the Frevvo form.
     * 
     * Note: If the value should be taken from the object, this should be blank and "keyValue" should contain the
     * property name
     * of the object from where the value should be taken
     */
    private String defaultValue;

    /**
     * Dynamic value that will be sent to the Frevvo form. It's taken from object itself.
     * 
     * Note: If the value should be hardcoded, this should be blank and "defaultValue" should contain the value
     */
    private String keyValue;

    /**
     * Indicate if the property is required or not. We have several required properties that are configured in the
     * poperties file
     */
    private boolean required;

    @XmlElement(name = "propertyName")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name = "propertyDefaultValue")
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    @XmlElement(name = "propertyKeyValue")
    public String getKeyValue()
    {
        return keyValue;
    }

    public void setKeyValue(String keyValue)
    {
        this.keyValue = keyValue;
    }

    @XmlElement(name = "propertyRequired")
    public boolean getRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }
}
