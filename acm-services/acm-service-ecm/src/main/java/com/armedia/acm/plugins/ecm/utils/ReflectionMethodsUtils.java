/**
 * 
 */
package com.armedia.acm.plugins.ecm.utils;

/*-
 * #%L
 * ACM Service: Form Configuration
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

import java.lang.reflect.Field;

/**
 * @author riste.tutureski
 *
 */
public class ReflectionMethodsUtils
{

    /**
     * The method will return value for given field name of the object.
     * It uses reflection.
     * 
     * @param object
     * @param fieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <E> E get(Object object, String fieldName)
    {
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            try
            {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (E) field.get(object);
            }
            catch (NoSuchFieldException e)
            {
                clazz = clazz.getSuperclass();
            }
            catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }

        return null;
    }

    /**
     * This method will set the value for the property in the object for given name of the property.
     * It uses reflection.
     * 
     * @param object
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public static Object set(Object object, String fieldName, Object fieldValue)
    {
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            try
            {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);

                return object;
            }
            catch (NoSuchFieldException e)
            {
                clazz = clazz.getSuperclass();
            }
            catch (Exception e)
            {
                throw new IllegalStateException(e);
            }
        }

        return object;
    }

}
