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
import com.armedia.acm.services.sequence.annotation.AcmSequence;
import com.armedia.acm.services.sequence.annotation.AcmSequenceAnnotationReader;
import com.armedia.acm.services.sequence.generator.AcmSequenceGenerator;
import com.armedia.acm.services.sequence.model.AcmSequenceEvent;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceHandler implements AcmBeforeInsertListener, ApplicationListener<AcmSequenceEvent>
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmSequenceAnnotationReader acmSequenceAnnotationReader;
    private AcmSequenceGenerator acmSequenceGenerator;

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

            List<Field> annotatedFields = getAcmSequenceAnnotationReader().getAnnotatedFields(object.getClass());
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
                        if (getAcmSequenceGenerator().getSequenceEnabled(sequenceName) != null
                                && getAcmSequenceGenerator().getSequenceEnabled(sequenceName))
                        {
                            String value = getAcmSequenceGenerator().generateValue(sequenceName, object);
                            PropertyUtils.setProperty(object, annotatedField.getName(), value);
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error("Error getting or setting property [{}], reason [{}]", annotatedField.getName(), e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @return the acmSequenceGenerator
     */
    public AcmSequenceGenerator getAcmSequenceGenerator()
    {
        return acmSequenceGenerator;
    }

    /**
     * @param acmSequenceGenerator
     *            the acmSequenceGenerator to set
     */
    public void setAcmSequenceGenerator(AcmSequenceGenerator acmSequenceGenerator)
    {
        this.acmSequenceGenerator = acmSequenceGenerator;
    }

    /**
     * @return the acmSequenceAnnotationReader
     */
    public AcmSequenceAnnotationReader getAcmSequenceAnnotationReader()
    {
        return acmSequenceAnnotationReader;
    }

    /**
     * @param acmSequenceAnnotationReader
     *            the acmSequenceAnnotationReader to set
     */
    public void setAcmSequenceAnnotationReader(AcmSequenceAnnotationReader acmSequenceAnnotationReader)
    {
        this.acmSequenceAnnotationReader = acmSequenceAnnotationReader;
    }

}
