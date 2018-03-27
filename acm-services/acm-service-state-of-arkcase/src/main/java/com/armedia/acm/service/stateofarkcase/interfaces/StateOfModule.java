package com.armedia.acm.service.stateofarkcase.interfaces;

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
