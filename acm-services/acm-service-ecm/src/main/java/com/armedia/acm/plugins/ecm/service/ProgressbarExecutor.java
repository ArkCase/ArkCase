package com.armedia.acm.plugins.ecm.service;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.model.ProgressbarDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.io.input.CountingInputStream;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.util.Timer;
import java.util.TimerTask;

public class ProgressbarExecutor {

    private String ID;
    private String username;
    private Timer timer;
    private int currentProgress = 0;
    private long partProgress;
    private JmsTemplate jmsTemplate;
    private ConnectionFactory activeMQConnectionFactory;
    private ProgressbarDetails progressbarDetails;

    public ProgressbarExecutor(String ID, String username, ConnectionFactory activeMQConnectionFactory, JmsTemplate jmsTemplate) {
        this.ID = ID;
        this.username = username;
        this.activeMQConnectionFactory = activeMQConnectionFactory;
        this.jmsTemplate = jmsTemplate;
    }

    public void send(ProgressbarDetails message, String destination)
    {
        ActiveMQTopic topic = new ActiveMQTopic(destination);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        jmsTemplate.send(topic, new MessageCreator() {
            public Message createMessage(Session inJmsSession) throws JMSException {
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String jsonMessageObj = "";
                try {
                    jsonMessageObj = ow.writeValueAsString(message);
                } catch (JsonProcessingException e) {
                    // Print error
                }

                TextMessage theTextMessage = inJmsSession.createTextMessage(jsonMessageObj);
                return theTextMessage;
            }
        });
    }

    public void startProgress(CountingInputStream fileInputStream, long size, String containerObjectType, Long containerObjectId, String fileName){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int _currentProgress = 0;
                if(progressbarDetails.getStage() == 2){

                    partProgress = fileInputStream.getByteCount();

                    if(partProgress > size) {
                        partProgress = size;
                    }
                    _currentProgress = 50 + Math.round(10 * (float) partProgress / size);
                    if(_currentProgress != currentProgress ) {
                        currentProgress = _currentProgress;
                        progressbarDetails.setCurrentProgress(currentProgress);
                        progressbarDetails.setSuccess(true);
                        send(progressbarDetails, "VirtualTopic.UploadFileManager:" + username.replaceAll("\\.", "_DOT_").replaceAll("@", "_AT_"));
                    }
                }
                else if(progressbarDetails.getStage() == 3) {
                    partProgress = fileInputStream.getByteCount();

                    if(partProgress > size) {
                        partProgress = size;
                    }
                    _currentProgress = 60 + Math.round(40 * (float) partProgress / size);
                    if(_currentProgress != currentProgress ) {
                        currentProgress = _currentProgress;
                        progressbarDetails.setCurrentProgress(currentProgress);
                        progressbarDetails.setSuccess(true);
                        send(progressbarDetails, "VirtualTopic.UploadFileManager:" + username.replaceAll("\\.", "_DOT_").replaceAll("@", "_AT_"));
                    }
                }

            }
        }, 1000, 2000);
    }


    public void stopProgress(boolean successfull){
        if(timer != null) {
            timer.cancel();
        }

        progressbarDetails.setCurrentProgress(currentProgress);
        progressbarDetails.setSuccess(successfull);
        send(progressbarDetails, "VirtualTopic.UploadFileManager:" + username.replaceAll("\\.", "_DOT_").replaceAll("@", "_AT_"));
    }

    public String getID() {
        return ID;
    }

    public ConnectionFactory getActiveMQConnectionFactory() {
        return activeMQConnectionFactory;
    }

    public void setActiveMQConnectionFactory(ConnectionFactory activeMQConnectionFactory) {
        this.activeMQConnectionFactory = activeMQConnectionFactory;
    }

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ProgressbarDetails getProgressbarDetails() {
        return progressbarDetails;
    }

    public void setProgressbarDetails(ProgressbarDetails progressbarDetails) {
        this.progressbarDetails = progressbarDetails;
    }
}
