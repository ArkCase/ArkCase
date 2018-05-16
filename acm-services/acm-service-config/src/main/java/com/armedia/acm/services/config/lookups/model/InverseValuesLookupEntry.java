package com.armedia.acm.services.config.lookups.model;

/*-
 * #%L
 * ACM Service: Config
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

/**
 * Created by bojan.milenkoski on 24.8.2017
 */
public class InverseValuesLookupEntry extends AcmLookupEntry
{
    private String key;
    private String value;
    private String inverseKey;
    private String inverseValue;

    public InverseValuesLookupEntry()
    {
    }

    public InverseValuesLookupEntry(String key, String value, String inverseKey, String inverseValue)
    {
        this.key = key;
        this.value = value;
        this.inverseKey = inverseKey;
        this.inverseValue = inverseValue;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getInverseKey()
    {
        return inverseKey;
    }

    public void setInverseKey(String inverseKey)
    {
        this.inverseKey = inverseKey;
    }

    public String getInverseValue()
    {
        return inverseValue;
    }

    public void setInverseValue(String inverseValue)
    {
        this.inverseValue = inverseValue;
    }
}
