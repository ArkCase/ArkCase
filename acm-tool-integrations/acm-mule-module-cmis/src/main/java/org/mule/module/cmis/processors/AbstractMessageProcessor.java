
package org.mule.module.cmis.processors;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.ConfigurationException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.context.MuleContextAware;
import org.mule.api.registry.RegistrationException;
import org.mule.api.transformer.Transformer;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.TransformerTemplate;
import org.mule.transport.NullPayload;

import javax.annotation.Generated;

import java.util.ArrayList;
import java.util.List;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public abstract class AbstractMessageProcessor<O>
        extends AbstractConnectedProcessor
        implements FlowConstructAware, MuleContextAware
{

    /**
     * Module object
     * 
     */
    protected O moduleObject;
    /**
     * Mule Context
     * 
     */
    protected MuleContext muleContext;
    /**
     * Flow Construct
     * 
     */
    protected FlowConstruct flowConstruct;

    /**
     * Retrieves muleContext
     *
     */
    public MuleContext getMuleContext()
    {
        return this.muleContext;
    }

    /**
     * Sets muleContext
     *
     * @param value
     *            Value to set
     */
    public void setMuleContext(MuleContext value)
    {
        this.muleContext = value;
    }

    /**
     * Retrieves flowConstruct
     *
     */
    public FlowConstruct getFlowConstruct()
    {
        return this.flowConstruct;
    }

    /**
     * Sets flowConstruct
     *
     * @param value
     *            Value to set
     */
    public void setFlowConstruct(FlowConstruct value)
    {
        this.flowConstruct = value;
    }

    /**
     * Sets moduleObject
     * 
     * @param value
     *            Value to set
     */
    public void setModuleObject(O value)
    {
        this.moduleObject = value;
    }

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object has not
     * been set already it will search the Mule registry for a default one.
     * 
     * @throws InstantiationException
     * @throws ConfigurationException
     * @throws IllegalAccessException
     * @throws RegistrationException
     */
    protected O findOrCreate(Class moduleClass, boolean shouldAutoCreate, MuleEvent muleEvent)
            throws IllegalAccessException, InstantiationException, ConfigurationException, RegistrationException
    {
        Object temporaryObject = moduleObject;
        if (temporaryObject == null)
        {
            temporaryObject = ((O) muleContext.getRegistry().lookupObject(moduleClass));
            if (temporaryObject == null)
            {
                if (shouldAutoCreate)
                {
                    temporaryObject = ((O) moduleClass.newInstance());
                    muleContext.getRegistry().registerObject(moduleClass.getName(), temporaryObject);
                }
                else
                {
                    throw new ConfigurationException(MessageFactory.createStaticMessage("Cannot find object"));
                }
            }
        }
        if (temporaryObject instanceof String)
        {
            temporaryObject = ((O) muleContext.getExpressionManager().evaluate(((String) temporaryObject), muleEvent, true));
            if (temporaryObject == null)
            {
                throw new ConfigurationException(MessageFactory.createStaticMessage("Cannot find object by config name"));
            }
        }
        return ((O) temporaryObject);
    }

    /**
     * Overwrites the event payload with the specified one
     * 
     */
    public void overwritePayload(MuleEvent event, Object resultPayload)
            throws Exception
    {
        TransformerTemplate.OverwitePayloadCallback overwritePayloadCallback = null;
        if (resultPayload == null)
        {
            overwritePayloadCallback = new TransformerTemplate.OverwitePayloadCallback(NullPayload.getInstance());
        }
        else
        {
            overwritePayloadCallback = new TransformerTemplate.OverwitePayloadCallback(resultPayload);
        }
        List<Transformer> transformerList;
        transformerList = new ArrayList<Transformer>();
        transformerList.add(new TransformerTemplate(overwritePayloadCallback));
        event.getMessage().applyTransformers(event, transformerList);
    }

}
