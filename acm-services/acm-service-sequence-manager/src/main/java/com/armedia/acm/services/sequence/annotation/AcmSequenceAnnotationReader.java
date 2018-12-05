package com.armedia.acm.services.sequence.annotation;

/*-
 * #%L
 * ACM Service: Sequence Manager
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceAnnotationReader
{

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * @param clazz
     * @return annotated field
     */
    public List<Field> getAnnotatedFields(Class<? extends Object> clazz)
    {
        List<Field> annotatedFields = new ArrayList<Field>();
        Field[] allFields = getDeclaredFields(clazz);

        for (Field field : allFields)
        {
            if (field.isAnnotationPresent(AcmSequence.class))
                annotatedFields.add(field);
        }
        return annotatedFields;
    }

    /**
     * Retrieving fields list of specified class
     * If recursively is true, retrieving fields from all class hierarchy
     *
     * @param clazz
     *            where searching for fields
     * @return list of fields
     */
    public static Field[] getDeclaredFields(Class clazz)
    {
        List<Field> fields = new LinkedList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        Collections.addAll(fields, declaredFields);

        Class superClass = clazz.getSuperclass();

        if (superClass != null)
        {
            Field[] declaredFieldsOfSuper = getDeclaredFields(superClass);
            if (declaredFieldsOfSuper.length > 0)
                Collections.addAll(fields, declaredFieldsOfSuper);
        }

        return fields.toArray(new Field[fields.size()]);
    }

}
