package com.armedia.acm.configuration.refresher.jms;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.api.RefreshScopeFacade;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.Message;

import javax.inject.Named;

@Configuration
public class ConfigurationChangedSubscriber
{
    private static final Logger log = LogManager.getLogger(ConfigurationChangedSubscriber.class);

    @Autowired
    @Named("proxyRefreshScopeFacade")
    private RefreshScopeFacade refreshScopeFacade;

    @Autowired
    @Named("proxyConfigurationFacade")
    private ConfigurationFacade configurationFacade;

    @Autowired
    @Named("proxyLabelsFacade")
    private ConfigurationFacade labelsFacade;

    @JmsListener(destination = "configuration.changed", containerFactory = "jmsTopicListenerContainerFactory")
    public void onConfigurationChanged(Message message)
    {
        log.info("Refresh beans on configuration change...");
        if (configurationFacade != null)
        {
            configurationFacade.refresh();
        }

        if (refreshScopeFacade != null)
        {
            refreshScopeFacade.refresh();
        }
    }

    @JmsListener(destination = "labels.changed", containerFactory = "jmsTopicListenerContainerFactory")
    public void onLabelsChanged(Message message)
    {
        log.info("Refreshing on labels change...");
        if (labelsFacade != null)
        {
            labelsFacade.refresh();
        }
    }
}
