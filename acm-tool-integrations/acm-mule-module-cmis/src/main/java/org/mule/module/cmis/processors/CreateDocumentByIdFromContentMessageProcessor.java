
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

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.mule.api.MessagingException;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.processor.MessageProcessor;
import org.mule.common.DefaultResult;
import org.mule.common.Result;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultPojoMetaDataModel;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.OperationMetaDataEnabled;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.datatype.DataTypeFactory;
import org.mule.config.i18n.CoreMessages;
import org.mule.module.cmis.CMISCloudConnector;
import org.mule.module.cmis.VersioningState;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;
import org.mule.module.cmis.process.ProcessAdapter;
import org.mule.module.cmis.process.ProcessCallback;
import org.mule.module.cmis.process.ProcessTemplate;

import javax.annotation.Generated;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * CreateDocumentByIdFromContentMessageProcessor invokes the
 * {@link org.mule.module.cmis.CMISCloudConnector#createDocumentByIdFromContent(java.lang.String, java.lang.String, java.lang.Object, java.lang.String, org.mule.module.cmis.VersioningState, java.lang.String, java.util.Map)}
 * method in {@link CMISCloudConnector }. For each argument there is a field in this processor to match it. Before
 * invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CreateDocumentByIdFromContentMessageProcessor
        extends AbstractMessageProcessor<Object>
        implements Disposable, Initialisable, Startable, Stoppable, MessageProcessor, OperationMetaDataEnabled
{

    protected Object folderId;
    protected String _folderIdType;
    protected Object filename;
    protected String _filenameType;
    protected Object content;
    protected Object _contentType;
    protected Object mimeType;
    protected String _mimeTypeType;
    protected Object versioningState;
    protected VersioningState _versioningStateType;
    protected Object objectType;
    protected String _objectTypeType;
    protected Object properties;
    protected Map<String, String> _propertiesType;

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object has not
     * been set already it will search the Mule registry for a default one.
     * 
     * @throws InitialisationException
     */
    public void initialise()
            throws InitialisationException
    {
    }

    public void start()
            throws MuleException
    {
    }

    public void stop()
            throws MuleException
    {
    }

    public void dispose()
    {
    }

    /**
     * Set the Mule context
     * 
     * @param context
     *            Mule context to set
     */
    public void setMuleContext(MuleContext context)
    {
        super.setMuleContext(context);
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct
     *            Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct)
    {
        super.setFlowConstruct(flowConstruct);
    }

    /**
     * Sets content
     * 
     * @param value
     *            Value to set
     */
    public void setContent(Object value)
    {
        this.content = value;
    }

    /**
     * Sets versioningState
     * 
     * @param value
     *            Value to set
     */
    public void setVersioningState(Object value)
    {
        this.versioningState = value;
    }

    /**
     * Sets filename
     * 
     * @param value
     *            Value to set
     */
    public void setFilename(Object value)
    {
        this.filename = value;
    }

    /**
     * Sets properties
     * 
     * @param value
     *            Value to set
     */
    public void setProperties(Object value)
    {
        this.properties = value;
    }

    /**
     * Sets objectType
     * 
     * @param value
     *            Value to set
     */
    public void setObjectType(Object value)
    {
        this.objectType = value;
    }

    /**
     * Sets mimeType
     * 
     * @param value
     *            Value to set
     */
    public void setMimeType(Object value)
    {
        this.mimeType = value;
    }

    /**
     * Sets folderId
     * 
     * @param value
     *            Value to set
     */
    public void setFolderId(Object value)
    {
        this.folderId = value;
    }

    /**
     * Invokes the MessageProcessor.
     * 
     * @param event
     *            MuleEvent to be processed
     * @throws MuleException
     */
    public MuleEvent process(final MuleEvent event)
            throws MuleException
    {
        Object moduleObject = null;
        try
        {
            moduleObject = findOrCreate(CMISCloudConnectorConnectionManager.class, true, event);
            final String _transformedFolderId = ((String) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_folderIdType").getGenericType(), null,
                    folderId));
            final String _transformedFilename = ((String) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_filenameType").getGenericType(), null,
                    filename));
            final Object _transformedContent = ((Object) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_contentType").getGenericType(), null, content));
            final String _transformedMimeType = ((String) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_mimeTypeType").getGenericType(), null,
                    mimeType));
            final VersioningState _transformedVersioningState = ((VersioningState) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_versioningStateType").getGenericType(), null,
                    versioningState));
            final String _transformedObjectType = ((String) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_objectTypeType").getGenericType(), null,
                    objectType));
            final Map<String, String> _transformedProperties = ((Map<String, String>) evaluateAndTransform(getMuleContext(), event,
                    CreateDocumentByIdFromContentMessageProcessor.class.getDeclaredField("_propertiesType").getGenericType(), null,
                    properties));
            Object resultPayload;
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object>) moduleObject).getProcessTemplate();
            resultPayload = processTemplate.execute(new ProcessCallback<Object, Object>()
            {

                public List<Class> getManagedExceptions()
                {
                    return Arrays.asList(new Class[] { CMISConnectorConnectionException.class });
                }

                public boolean isProtected()
                {
                    return false;
                }

                public Object process(Object object)
                        throws Exception
                {
                    return ((CMISCloudConnector) object).createDocumentByIdFromContent(_transformedFolderId, _transformedFilename,
                            _transformedContent, _transformedMimeType, _transformedVersioningState, _transformedObjectType,
                            _transformedProperties);
                }

            }, this, event);
            overwritePayload(event, resultPayload);
            return event;
        }
        catch (MessagingException messagingException)
        {
            messagingException.setProcessedEvent(event);
            throw messagingException;
        }
        catch (Exception e)
        {
            throw new MessagingException(CoreMessages.failedToInvoke("createDocumentByIdFromContent"), event, e);
        }
    }

    @Override
    public Result<MetaData> getInputMetaData()
    {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(Object.class)));
    }

    @Override
    public Result<MetaData> getOutputMetaData(MetaData inputMetadata)
    {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(ObjectId.class)));
    }

    private MetaDataModel getPojoOrSimpleModel(Class clazz)
    {
        DataType dataType = DataTypeFactory.getInstance().getDataType(clazz);
        if (DataType.POJO.equals(dataType))
        {
            return new DefaultPojoMetaDataModel(clazz);
        }
        else
        {
            return new DefaultSimpleMetaDataModel(dataType);
        }
    }

}
