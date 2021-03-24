package com.armedia.acm.services.zylab.jms;

import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;

import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.tool.zylab.model.ZylabProductionSyncDTO;

public class ZylabProductionSyncStatusToJmsSender implements InitializingBean
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private ObjectConverter objectConverter;
    private ConnectionFactory jmsConnectionFactory;
    private JmsTemplate jmsTemplate;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        jmsTemplate = new JmsTemplate(getJmsConnectionFactory());
    }

    public void sendProductionSyncStatus(ZylabProductionSyncDTO zylabProductionSyncDTO)
    {
        sendToJmsQueue(zylabProductionSyncDTO, "arkcase-zylab-integration");
    }

    private void sendToJmsQueue(ZylabProductionSyncDTO zylabProductionSyncDTO, String queueName)
    {
        try
        {
            String json = objectConverter.getJsonMarshaller().marshal(zylabProductionSyncDTO);

            getJmsTemplate().convertAndSend(queueName, json);

            log.debug("Production sync status sent to JMS queue: {}. JSON: {}", queueName, json);
        }
        catch (JmsException e)
        {
            log.error("Could not send production sync status to JMS queue: {}, {}", queueName, e.getMessage(), e);
        }
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public ConnectionFactory getJmsConnectionFactory()
    {
        return jmsConnectionFactory;
    }

    public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory)
    {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }

    public JmsTemplate getJmsTemplate()
    {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate)
    {
        this.jmsTemplate = jmsTemplate;
    }

}
