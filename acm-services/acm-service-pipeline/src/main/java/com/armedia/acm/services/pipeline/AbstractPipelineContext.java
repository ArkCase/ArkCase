package com.armedia.acm.services.pipeline;

/*-
 * #%L
 * ACM Service: Pipeline
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract Pipiline context.
 * Holds map for adding arbitrary properties,
 * container map is concurrent so is safe to be used in multi threaded environment.
 * <p/>
 * Created by nebojsha on 25.09.2015.
 */
public abstract class AbstractPipelineContext
{
    private Map<String, Object> properties = new ConcurrentHashMap<>();

    /**
     * Adds property into the map. If key exists than value is overridden
     *
     * @param key
     *            key for the property. Key must not be null.
     * @param value
     *            value of the property. Value must not be null.
     */
    public void addProperty(String key, Object value)
    {
        properties.put(key, value);
    }

    /**
     * if key exists return true, false otherwise. Key must not be null.
     *
     * @param key
     * @return returns boolean
     */
    public boolean hasProperty(String key)
    {
        return properties.containsKey(key);
    }

    /**
     * Returns value for provided key. If key not found than return null.
     *
     * @param key
     * @return return Object
     */
    public Object getPropertyValue(String key)
    {
        return properties.get(key);
    }

    /**
     * removes object and key from the map
     *
     * @param key
     *            must not be null.
     * @return returns Object associated with this key
     */
    public Object removeProperty(String key)
    {
        return properties.remove(key);
    }
}
