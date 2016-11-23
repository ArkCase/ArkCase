package com.armedia.broker;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

/**
 * File message producer for ActiveMQ Sends/receives large files as BlobMessage
 * 
 * @author dame.gjorgjievski
 *
 */
public class AcmFileBrokerClient
{
    private final ActiveMQConnectionFactory connectionFactory;
    private final String outboundQueue;
    private final String inboundQueue;

    private JmsTemplate producerTemplate;
    private String fileUploadUrl;

    public static final String PROP_FILE_NAME = "fileName";
    public static final String PROP_FILE_UPLOAD_URL = "fileUploadUrl";

    public AcmFileBrokerClient(ActiveMQConnectionFactory connectionFactory, String outboundQueue, String inboundQueue)
    {
        this.connectionFactory = connectionFactory;
        this.outboundQueue = outboundQueue;
        this.inboundQueue = inboundQueue;
        init();
    }

    /**
     * Initialize
     */
    private final void init()
    {
        CachingConnectionFactory cachedConnectionFactory = new CachingConnectionFactory(connectionFactory);
        producerTemplate = new JmsTemplate(cachedConnectionFactory);
        if (outboundQueue != null)
        {
            producerTemplate.setDefaultDestinationName(outboundQueue);
        }
    }

    /**
     * Send file to default outbound queue
     * 
     * @param file
     * @param properties
     * @throws JMSException
     * @throws IOException
     */
    public void sendFile(File file, Map<String, Object> properties) throws JMSException, IOException
    {
        if (outboundQueue == null)
        {
            throw new IllegalStateException("No default outbound queue is specified");
        }
        sendFile(file, outboundQueue, properties);
    }

    /**
     * Send file as blob message
     * 
     * @param file
     * @param queueName
     * @param properties
     * @throws JMSException
     * @throws IOException
     */
    public void sendFile(File file, String queueName, Map<String, Object> properties) throws JMSException, IOException
    {
        new AcmFileBrokerClientExecutor<Void>()
        {
            @Override
            protected Void execute() throws JMSException
            {
                Queue queue = session.createQueue(queueName);
                MessageProducer producer = session.createProducer(queue);
                BlobMessage message = session.createBlobMessage(file);
                message.setStringProperty(PROP_FILE_NAME, file.getName());
                if (fileUploadUrl != null)
                {
                    message.setStringProperty(PROP_FILE_UPLOAD_URL, fileUploadUrl);
                }
                if (properties != null)
                {
                    for (Map.Entry<String, Object> prop : properties.entrySet())
                    {
                        message.setObjectProperty(prop.getKey(), prop.getValue());
                    }
                }
                producer.send(message);

                return null;
            }
        }.execute(connectionFactory.createConnection());

    }

    /**
     * Receive file from default inbound queue
     * 
     * @return
     * @throws JMSException
     * @throws IOException
     */
    public File receiveFile() throws JMSException, IOException
    {
        if (inboundQueue == null)
        {
            throw new IllegalStateException("No default inbound queue is specified");
        }
        return receiveFile(inboundQueue);
    }

    /**
     * Receives file as blob message
     * 
     * @param queueName
     * @throws JMSException
     * @throws IOException
     */
    public File receiveFile(String queueName) throws JMSException, IOException
    {
        return new AcmFileBrokerClientExecutor<File>()
        {
            @Override
            protected File execute() throws JMSException, IOException
            {
                File file = null;
                Queue queue = session.createQueue(queueName);
                MessageConsumer consumer = session.createConsumer(queue);
                BlobMessage message = (BlobMessage) consumer.receive(2000);
                message.acknowledge();

                String fileName = message.getStringProperty(PROP_FILE_NAME);
                if (fileName == null)
                {
                    fileName = UUID.randomUUID().toString();
                }
                String[] fileNameParts = getFileNameParts(fileName);
                file = File.createTempFile(fileNameParts[0], fileNameParts[1]);
                file.setWritable(true);
                file.deleteOnExit();
                try (FileOutputStream fos = new FileOutputStream(file))
                {
                    InputStream in = message.getInputStream();
                    byte[] buffer = new byte[1024];
                    while (true)
                    {
                        int bytesRead = in.read(buffer);
                        if (bytesRead == -1)
                        {
                            break;
                        }
                        fos.write(buffer, 0, bytesRead);
                    }
                }

                return file;
            }
        }.execute(connectionFactory.createConnection());

    }

    /**
     * Split filename into name and extension parts
     * 
     * @param fileName
     * @return
     */
    private String[] getFileNameParts(String fileName)
    {
        int index = fileName.lastIndexOf(".");
        String[] result = new String[2];
        if (index > -1)
        {
            result[0] = fileName.substring(0, index);
            result[1] = fileName.substring(index, fileName.length());
        } else
        {
            result[0] = fileName;
            result[1] = ".tmp";
        }
        return result;
    }

    public String getFileUploadUrl()
    {
        return fileUploadUrl;
    }

    public void setFileUploadUrl(String fileUploadUrl)
    {
        if (!fileUploadUrl.endsWith("/"))
        {
            fileUploadUrl += "/";
        }
        this.fileUploadUrl = fileUploadUrl;
    }

    /**
     * Session executor wrapper
     * 
     * @author dame.gjorgjievski
     *
     * @param <E>
     */
    public abstract class AcmFileBrokerClientExecutor<E>
    {
        protected ActiveMQSession session;

        protected abstract E execute() throws JMSException, IOException;

        public E execute(Connection connection) throws JMSException, IOException
        {
            try
            {
                E result = null;
                connection.start();
                session = (ActiveMQSession) connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                result = execute();
                session.close();
                connection.close();
                return result;
            } catch (Exception e)
            {
                if (session != null)
                {
                    session.close();
                }
                if (connection != null)
                {
                    connection.close();
                }
                throw e;
            }
        }

    }
}
