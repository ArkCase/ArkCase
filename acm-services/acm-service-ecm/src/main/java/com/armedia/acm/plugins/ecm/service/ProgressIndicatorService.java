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
import org.apache.commons.io.input.CountingInputStream;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import java.util.HashMap;

public class ProgressIndicatorService
{
    private HashMap<String, ProgressbarExecutor> progressBars = new HashMap<>();
    private JmsTemplate jmsTemplate;
    private ConnectionFactory activeMQConnectionFactory;

    public void start(CountingInputStream inputStream, long size, Long id, String type, String name, String username, ProgressbarDetails progressbarDetails){
        String _id = type + "_" + id + "_" + name;
        ProgressbarExecutor executor = new ProgressbarExecutor(_id, username, activeMQConnectionFactory, jmsTemplate);
        executor.startProgress(inputStream, size, type, id, name, progressbarDetails);
        progressBars.put(_id, executor);
    }

    public void end(Long id, String type, String name, boolean successful){
        String _id = type + "_" + id + "_" + name;
        ProgressbarExecutor executor = progressBars.get(_id);
        if (executor != null) {
            executor.stopProgress(type, id, name, successful);
            progressBars.remove(_id);
        }
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setActiveMQConnectionFactory(ConnectionFactory activeMQConnectionFactory) {
        this.activeMQConnectionFactory = activeMQConnectionFactory;
    }
}
