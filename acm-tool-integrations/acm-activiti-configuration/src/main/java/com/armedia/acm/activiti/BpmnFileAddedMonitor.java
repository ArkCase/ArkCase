package com.armedia.acm.activiti;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
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

import com.armedia.acm.activiti.services.AcmBpmnService;
import com.armedia.acm.files.ConfigurationFileAddedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationListener;

import java.io.File;

/**
 * Created by armdev on 4/11/14.
 */
public class BpmnFileAddedMonitor implements ApplicationListener<ConfigurationFileAddedEvent>
{
    private AcmBpmnService acmBpmnService;

    private transient Logger log = LogManager.getLogger(getClass());

    @Override
    public void onApplicationEvent(ConfigurationFileAddedEvent configurationFileAddedEvent)
    {
        File eventFile = configurationFileAddedEvent.getConfigFile();
        if (eventFile.getParentFile().getName().equals("activiti") && eventFile.getName().endsWith("bpmn20.xml"))
        {

            try
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Deploying new Activiti file: " + eventFile.getCanonicalPath());
                }
                acmBpmnService.deploy(eventFile, "", false, false);

                if (log.isDebugEnabled())
                {
                    log.debug("... finished deploying from: " + eventFile.getCanonicalPath());
                }
            }
            catch (Throwable e)
            {
                log.error("Could not deploy Activiti definition file: " + e.getMessage(), e);
            }
        }
    }

    public void setAcmBpmnService(AcmBpmnService acmBpmnService)
    {
        this.acmBpmnService = acmBpmnService;
    }
}
