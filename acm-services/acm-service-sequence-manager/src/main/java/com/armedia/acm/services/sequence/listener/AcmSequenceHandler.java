package com.armedia.acm.services.sequence.listener;

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

import com.armedia.acm.data.AcmBeforeInsertListener;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.data.event.AcmSequenceEvent;
import com.armedia.acm.services.sequence.annotation.AcmSequence;
import com.armedia.acm.services.sequence.annotation.AcmSequenceAnnotationReader;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.generator.AcmSequenceGeneratorManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceHandler implements AcmBeforeInsertListener, ApplicationListener<AcmSequenceEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmSequenceAnnotationReader sequenceAnnotationReader;
    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    @Override
    public void beforeInsert(Object object)
    {
        handleSequence(object);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(AcmSequenceEvent event)
    {
        handleSequence(event.getSource());
    }

    /**
     * @param object
     */
    private void handleSequence(Object object)
    {
        if (object instanceof AcmEntity)
        {
            log.trace("Entity type [{}] is an AcmEntity, setting annotated fields", object.getClass());

            List<Field> annotatedFields = getSequenceAnnotationReader().getAnnotatedFields(object.getClass());
            for (Field annotatedField : annotatedFields)
            {
                try
                {
                    if (annotatedField != null &&
                            (annotatedField.getAnnotation(AcmSequence.class).isAttributeNull()
                                    && PropertyUtils.getProperty(object, annotatedField.getName()) == null ||
                                    !annotatedField.getAnnotation(AcmSequence.class).isAttributeNull()
                                            && annotatedField.getAnnotation(AcmSequence.class).attributeValue()
                                                    .equals(PropertyUtils.getProperty(object, annotatedField.getName()).toString())))
                    {
                        String sequenceName = annotatedField.getAnnotation(AcmSequence.class).sequenceName();
                        if (getSequenceGeneratorManager().getSequenceEnabled(sequenceName))
                        {
                            String value = getSequenceGeneratorManager().generateValue(sequenceName, object);
                            PropertyUtils.setProperty(object, annotatedField.getName(), value);
                        }
                    }
                }
                catch (AcmSequenceException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                {
                    log.error("Error setting sequence on [{}]", annotatedField.getName(), e);
                }
            }
        }
    }

    /**
     * @return the sequenceGeneratorManager
     */
    public AcmSequenceGeneratorManager getSequenceGeneratorManager()
    {
        return sequenceGeneratorManager;
    }

    /**
     * @param sequenceGeneratorManager
     *            the sequenceGeneratorManager to set
     */
    public void setSequenceGeneratorManager(AcmSequenceGeneratorManager sequenceGeneratorManager)
    {
        this.sequenceGeneratorManager = sequenceGeneratorManager;
    }

    /**
     * @return the sequenceAnnotationReader
     */
    public AcmSequenceAnnotationReader getSequenceAnnotationReader()
    {
        return sequenceAnnotationReader;
    }

    /**
     * @param sequenceAnnotationReader
     *            the sequenceAnnotationReader to set
     */
    public void setSequenceAnnotationReader(AcmSequenceAnnotationReader sequenceAnnotationReader)
    {
        this.sequenceAnnotationReader = sequenceAnnotationReader;
    }

}
