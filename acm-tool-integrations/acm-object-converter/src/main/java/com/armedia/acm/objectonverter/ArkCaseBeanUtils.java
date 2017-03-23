package com.armedia.acm.objectonverter;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by riste.tutureski on 9/15/2015.
 * <p>
 * Extend "copyProperty" method in the apache BeanUtilsBean to support not merging "null" values and recursive calls
 */
public class ArkCaseBeanUtils extends org.apache.commons.beanutils.BeanUtilsBean
{
    private Logger LOG = LoggerFactory.getLogger(getClass());
    private List<String> excludeFields;

    /**
     * This method overriding the superclass method. Skip "null" values and goes recursively if the field is
     * non-primitive and non-wrapped type.
     *
     * @param bean  - the object that should receive the value
     * @param name  - field name of the object
     * @param value - the value that should be added to the "bean" for given field "name"
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Override
    public void copyProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException
    {
        // Skip if value is null
        if (value != null)
        {
            // Get class name for the given bean
            Class<?> c = bean.getClass();

            // Get field from class for given field name (return null if that field not exist)
            Field field = getField(c, name);

            // If field exist
            if (field != null)
            {
                // Make it accessible
                field.setAccessible(true);

                // Get the value
                Object obj = field.get(bean);

                // If the value is null or the value is primitive or wrapper type (plus "String" - because
                // "String" is not in the wrapped types), then make a copy
                if (obj == null || ClassUtils.isPrimitiveOrWrapper(obj.getClass()) || obj instanceof String || obj instanceof List)
                {
                    super.copyProperty(bean, name, value);
                } else
                {
                    // Recursive call to whole process
                    copyProperties(obj, value);
                }
            }
        }
    }

    /**
     * Get field for given filed name from the class
     *
     * @param c    - class from where we want to take the filed
     * @param name - field name that we want to take
     * @return - Field object for given filed name
     */
    private Field getField(Class<?> c, String name)
    {
        Field field = null;

        // If class is not provided, skip the logic. This will terminate recursion for searching the field
        // in the supperclasses if not found in the current class
        if (c != null && (getExcludeFields() == null || !getExcludeFields().contains(name)))
        {
            try
            {
                // Try to get field from the class for given name
                field = c.getDeclaredField(name);
            } catch (Exception e)
            {
                // The filed is not found in the current class ... try to find it to superclass
                LOG.warn("There is no field '{}' declared. Try to find in the superclass.", name);
                return getField(c.getSuperclass(), name);
            }
        }

        return field;
    }

    public List<String> getExcludeFields()
    {
        return excludeFields;
    }

    public void setExcludeFields(List<String> excludeFields)
    {
        this.excludeFields = excludeFields;
    }
}
