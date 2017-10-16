
package org.mule.module.cmis.processors;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import org.apache.chemistry.opencmis.client.api.CmisObject;
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
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.mule.module.cmis.exception.CMISConnectorConnectionException;
import org.mule.module.cmis.process.ProcessAdapter;
import org.mule.module.cmis.process.ProcessCallback;
import org.mule.module.cmis.process.ProcessTemplate;


/**
 * CheckInMessageProcessor invokes the {@link org.mule.module.cmis.CMISCloudConnector#checkIn(org.apache.chemistry.opencmis.client.api.CmisObject, java.lang.String, java.lang.Object, java.lang.String, java.lang.String, boolean, java.lang.String, java.util.Map)} method in {@link CMISCloudConnector }. For each argument there is a field in this processor to match it.  Before invoking the actual method the processor will evaluate and transform where possible to the expected argument type.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CheckInMessageProcessor
    extends AbstractMessageProcessor<Object>
    implements Disposable, Initialisable, Startable, Stoppable, MessageProcessor, OperationMetaDataEnabled
{

    protected Object document;
    protected CmisObject _documentType;
    protected Object documentId;
    protected String _documentIdType;
    protected Object content;
    protected Object _contentType;
    protected Object filename;
    protected String _filenameType;
    protected Object mimeType;
    protected String _mimeTypeType;
    protected Object major;
    protected boolean _majorType;
    protected Object checkinComment;
    protected String _checkinCommentType;
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
     * Sets content
     * 
     * @param value Value to set
     */
    public void setContent(Object value) {
        this.content = value;
    }

    /**
     * Sets document
     * 
     * @param value Value to set
     */
    public void setDocument(Object value) {
        this.document = value;
    }

    /**
     * Sets checkinComment
     * 
     * @param value Value to set
     */
    public void setCheckinComment(Object value) {
        this.checkinComment = value;
    }

    /**
     * Sets filename
     * 
     * @param value Value to set
     */
    public void setFilename(Object value) {
        this.filename = value;
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
     * Sets major
     * 
     * @param value Value to set
     */
    public void setMajor(Object value) {
        this.major = value;
    }

    /**
     * Sets mimeType
     * 
     * @param value Value to set
     */
    public void setMimeType(Object value) {
        this.mimeType = value;
    }

    /**
     * Sets documentId
     * 
     * @param value Value to set
     */
    public void setDocumentId(Object value) {
        this.documentId = value;
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
            final CmisObject _transformedDocument = ((CmisObject) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_documentType").getGenericType(), null, document));
            final String _transformedDocumentId = ((String) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_documentIdType").getGenericType(), null, documentId));
            final Object _transformedContent = ((Object) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_contentType").getGenericType(), null, content));
            final String _transformedFilename = ((String) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_filenameType").getGenericType(), null, filename));
            final String _transformedMimeType = ((String) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_mimeTypeType").getGenericType(), null, mimeType));
            final Boolean _transformedMajor = ((Boolean) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_majorType").getGenericType(), null, major));
            final String _transformedCheckinComment = ((String) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_checkinCommentType").getGenericType(), null, checkinComment));
            final Map<String, String> _transformedProperties = ((Map<String, String> ) evaluateAndTransform(getMuleContext(), event, CheckInMessageProcessor.class.getDeclaredField("_propertiesType").getGenericType(), null, properties));
            Object resultPayload;
            ProcessTemplate<Object, Object> processTemplate = ((ProcessAdapter<Object> ) moduleObject).getProcessTemplate();
            resultPayload = processTemplate.execute(new ProcessCallback<Object,Object>() {


                public List<Class> getManagedExceptions() {
                    return Arrays.asList(new Class[] {CMISConnectorConnectionException.class });
                }

                public boolean isProtected() {
                    return false;
                }

                public Object process(Object object)
                    throws Exception
                {
                    return ((CMISCloudConnector) object).checkIn(_transformedDocument, _transformedDocumentId, _transformedContent, _transformedFilename, _transformedMimeType, _transformedMajor, _transformedCheckinComment, _transformedProperties);
                }

            }
            , this, event);
            overwritePayload(event, resultPayload);
            return event;
        } catch (MessagingException messagingException) {
            messagingException.setProcessedEvent(event);
            throw messagingException;
        } catch (Exception e) {
            throw new MessagingException(CoreMessages.failedToInvoke("checkIn"), event, e);
        }
    }

    @Override
    public Result<MetaData> getInputMetaData() {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(Object.class)));
    }

    @Override
    public Result<MetaData> getOutputMetaData(MetaData inputMetadata) {
        return new DefaultResult<MetaData>(new DefaultMetaData(getPojoOrSimpleModel(ObjectId.class)));
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
