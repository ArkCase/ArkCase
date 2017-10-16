
package org.mule.module.cmis.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
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
import org.mule.common.metadata.DefaultParameterizedMapMetaDataModel;
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


/**
 * ApplyAspectMessageProcessor invokes the {@link org.mule.module.cmis.CMISCloudConnector#applyAspect(java.lang.String, java.lang.String, java.util.Map)} method in {@link CMISCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class ApplyAspectMessageProcessor
    extends AbstractMessageProcessor<Object>
    implements Disposable, Initialisable, Startable, Stoppable, MessageProcessor, OperationMetaDataEnabled
{

    protected Object objectId;
    protected String _objectIdType;
    protected Object aspectName;
    protected String _aspectNameType;
    protected Object properties;
    protected Map<String, String> _propertiesType;

    /**
     * Obtains the expression manager from the Mule context and initialises the connector. If a target object  has not been set already it will search the Mule registry for a default one.
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

    public void dispose() {
    }

    /**
     * Set the Mule context
     * 
     * @param context Mule context to set
     */
    public void setMuleContext(MuleContext context) {
        super.setMuleContext(context);
    }

    /**
     * Sets flow construct
     * 
     * @param flowConstruct Flow construct to set
     */
    public void setFlowConstruct(FlowConstruct flowConstruct) {
        super.setFlowConstruct(flowConstruct);
    }

    /**
     * Sets objectId
     * 
     * @param value Value to set
     */
    public void setObjectId(Object value) {
        this.objectId = value;
    }

    /**
     * Sets aspectName
     * 
     * @param value Value to set
     */
    public void setAspectName(Object value) {
        this.aspectName = value;
    }

    /**
     * Sets properties
     * 
     * @param value Value to set
     */
    public void setProperties(Object value) {
        this.properties = value;
    }

    /**
     * Invokes the MessageProcessor.
     * 
     * @param event MuleEvent to be processed
     * @throws MuleException
     */
    public MuleEvent process(final MuleEvent event)
        throws MuleException
    {
        Object moduleObject = null;
        try {
            moduleObject = findOrCreate(CMISCloudConnectorConnectionManager.class, true, event);
            final String _transformedObjectId = ((String) evaluateAndTransform(getMuleContext(), event, ApplyAspectMessageProcessor.class.getDeclaredField("_objectIdType").getGenericType(), null, objectId));
            final String _transformedAspectName = ((String) evaluateAndTransform(getMuleContext(), event, ApplyAspectMessageProcessor.class.getDeclaredField("_aspectNameType").getGenericType(), null, aspectName));
            final Map<String, String> _transformedProperties = ((Map<String, String> ) evaluateAndTransform(getMuleContext(), event, ApplyAspectMessageProcessor.class.getDeclaredField("_propertiesType").getGenericType(), null, properties));
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
            processTemplate.execute(new ProcessCallback<Object,Object>() {


                public List<Class> getManagedExceptions() {
                    return Arrays.asList(new Class[] {CMISConnectorConnectionException.class });
                }

                public boolean isProtected() {
                    return false;
                }

                public Object process(Object object)
                    throws Exception
                {
                    ((CMISCloudConnector) object).applyAspect(_transformedObjectId, _transformedAspectName, _transformedProperties);
                    return null;
                }

            }
            , this, event);
            return event;
        } catch (MessagingException messagingException) {
            messagingException.setProcessedEvent(event);
            throw messagingException;
        } catch (Exception e) {
            throw new MessagingException(CoreMessages.failedToInvoke("applyAspect"), event, e);
        }
    }

    @Override
    public Result<MetaData> getInputMetaData() {
        return new DefaultResult<MetaData>(new DefaultMetaData(new DefaultParameterizedMapMetaDataModel(getPojoOrSimpleModel(String.class), getPojoOrSimpleModel(String.class))));
    }

    @Override
    public Result<MetaData> getOutputMetaData(MetaData inputMetadata) {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(void.class)));
    }

    private MetaDataModel getPojoOrSimpleModel(Class clazz) {
        DataType dataType = DataTypeFactory.getInstance().getDataType(clazz);
        if (DataType.POJO.equals(dataType)) {
            return new DefaultPojoMetaDataModel(clazz);
        } else {
            return new DefaultSimpleMetaDataModel(dataType);
        }
    }

}
