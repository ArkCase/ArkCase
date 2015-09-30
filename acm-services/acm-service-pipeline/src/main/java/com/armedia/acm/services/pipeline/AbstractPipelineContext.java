package com.armedia.acm.services.pipeline;

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
     * @param key   key for the property. Key must not be null.
     * @param value value of the property. Value must not be null.
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
     * @param key must not be null.
     * @return returns Object associated with this key
     */
    public Object removeProperty(String key)
    {
        return properties.remove(key);
    }
}
