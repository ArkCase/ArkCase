
package org.mule.module.cmis.processors;

import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
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
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;
import org.mule.module.cmis.process.ProcessAdapter;
import org.mule.module.cmis.process.ProcessCallback;
import org.mule.module.cmis.process.ProcessTemplate;

import javax.annotation.Generated;

import java.util.Arrays;
import java.util.List;

/**
 * MoveObjectMessageProcessor invokes the
 * {@link org.mule.module.cmis.CMISCloudConnector#moveObject(org.apache.chemistry.opencmis.client.api.FileableCmisObject, java.lang.String, java.lang.String, java.lang.String)}
 * method in {@link CMISCloudConnector }. For each argument there is a field in this processor to match it. Before
 * invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class MoveObjectMessageProcessor
        extends AbstractMessageProcessor<Object>
        implements Disposable, Initialisable, Startable, Stoppable, MessageProcessor, OperationMetaDataEnabled
{

    protected Object cmisObject;
    protected FileableCmisObject _cmisObjectType;
    protected Object objectId;
    protected String _objectIdType;
    protected Object sourceFolderId;
    protected String _sourceFolderIdType;
    protected Object targetFolderId;
    protected String _targetFolderIdType;

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
     * Sets targetFolderId
     * 
     * @param value
     *            Value to set
     */
    public void setTargetFolderId(Object value)
    {
        this.targetFolderId = value;
    }

    /**
     * Sets sourceFolderId
     * 
     * @param value
     *            Value to set
     */
    public void setSourceFolderId(Object value)
    {
        this.sourceFolderId = value;
    }

    /**
     * Sets objectId
     * 
     * @param value
     *            Value to set
     */
    public void setObjectId(Object value)
    {
        this.objectId = value;
    }

    /**
     * Sets cmisObject
     * 
     * @param value
     *            Value to set
     */
    public void setCmisObject(Object value)
    {
        this.cmisObject = value;
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
            final FileableCmisObject _transformedCmisObject = ((FileableCmisObject) evaluateAndTransform(getMuleContext(), event,
                    MoveObjectMessageProcessor.class.getDeclaredField("_cmisObjectType").getGenericType(), null, cmisObject));
            final String _transformedObjectId = ((String) evaluateAndTransform(getMuleContext(), event,
                    MoveObjectMessageProcessor.class.getDeclaredField("_objectIdType").getGenericType(), null, objectId));
            final String _transformedSourceFolderId = ((String) evaluateAndTransform(getMuleContext(), event,
                    MoveObjectMessageProcessor.class.getDeclaredField("_sourceFolderIdType").getGenericType(), null, sourceFolderId));
            final String _transformedTargetFolderId = ((String) evaluateAndTransform(getMuleContext(), event,
                    MoveObjectMessageProcessor.class.getDeclaredField("_targetFolderIdType").getGenericType(), null, targetFolderId));
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
                    return ((CMISCloudConnector) object).moveObject(_transformedCmisObject, _transformedObjectId,
                            _transformedSourceFolderId, _transformedTargetFolderId);
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
            throw new MessagingException(CoreMessages.failedToInvoke("moveObject"), event, e);
        }
    }

    @Override
    public Result<MetaData> getInputMetaData()
    {
        return new DefaultResult<MetaData>(null, (Result.Status.SUCCESS));
    }

    @Override
    public Result<MetaData> getOutputMetaData(MetaData inputMetadata)
    {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(FileableCmisObject.class)));
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
