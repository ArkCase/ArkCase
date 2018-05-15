package com.armedia.acm.service.stateofarkcase.interfaces;

/*-
 * #%L
 * ACM Service: State of Arkcase
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

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * abstract class containing pair of properties of state of the module.
 * It needs to be extended in each module, and can contain additional properties
 */
public abstract class StateOfModule
{
    private Map<String, Object> stateProperties = new HashMap<>();

    /**
     * add pair of property as key/value.
     *
     * @param name
     *            name of the property
     * @param value
     *            value of the property
     * @return StateOfModule to support fluent api
     */
    public StateOfModule addProperty(String name, Object value)
    {
        stateProperties.put(name, value);
        return this;
    }

    /**
     * remove property.
     *
     * @param name
     *            name of the property
     * @return StateOfModule to support fluent api
     */
    public StateOfModule removeProperty(String name)
    {
        stateProperties.remove(name);
        return this;
    }

    /**
     * boolean whether contains property name or not.
     *
     * @param name
     *            name of the property
     * @return boolean true if property name exists
     */
    public boolean containsProperty(String name)
    {
        return stateProperties.containsKey(name);
    }

    /**
     *
     * @return immutable map of properties
     */
    @JsonAnyGetter
    public Map<String, Object> getStateProperties()
    {
        return Collections.unmodifiableMap(stateProperties);
    }
}
