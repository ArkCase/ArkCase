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
import com.armedia.acm.services.sequence.generator.AcmSequenceGeneratorManager;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceHandler implements AcmBeforeInsertListener, ApplicationListener<AcmSequenceEvent>
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmSequenceAnnotationReader sequenceAnnotationReader;
    private AcmSequenceGeneratorManager sequenceGeneratorManager;
    private MessageChannel genericMessagesChannel;

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
                        if (getSequenceGeneratorManager().isSequenceEnabled(sequenceName))
                        {
                            String value = getSequenceGeneratorManager().generateValue(sequenceName, object);
                            if (value != null && !value.isEmpty())
                            {
                                PropertyUtils.setProperty(object, annotatedField.getName(), value);
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error("Error setting sequence on [{}] [{}]", object.getClass().getSimpleName(), annotatedField.getName(), e);
                    propagateSequenceMessage(object, annotatedField.getName());
                }
            }
        }
    }

    private void propagateSequenceMessage(Object object, String attributeName)
    {
        Map<String, Object> message = new HashMap<>();
        message.put("user", ((AcmEntity) object).getModifier());
        message.put("eventType", "sequence-error");
        message.put("message", String.format("Error setting sequence on [%s] [%s]", object.getClass().getSimpleName(), attributeName));

        getGenericMessagesChannel().send(MessageBuilder.withPayload(message).build());
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

    /**
     * @return the genericMessagesChannel
     */
    public MessageChannel getGenericMessagesChannel()
    {
        return genericMessagesChannel;
    }

    /**
     * @param genericMessagesChannel
     *            the genericMessagesChannel to set
     */
    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }

}
