package com.armedia.broker;

import javax.jms.JMSException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * File broker client interface
 * 
 * @author dame.gjorgjievski
 *
 */
public interface IAcmFileBrokerClient
{
    public void sendFile(File file, Map<String, Object> properties) throws JMSException, IOException;

    public void sendFile(File file, String queueName, Map<String, Object> properties) throws JMSException, IOException;

    public File receiveFile() throws JMSException, IOException;

    public File receiveFile(String queueName) throws JMSException, IOException;

}
