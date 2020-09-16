package com.armedia.acm.services.config.lookups.service;

/*-
 * #%L
 * ACM Service: Config
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

import com.armedia.acm.configuration.model.LookupsUpdatedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aleksandar Acevski <aleksandar.acevski@armedia.com> on September, 2020
 */
public class LookupsUpdatedListener implements ApplicationListener<LookupsUpdatedEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private MessageChannel configurationUpdatedChannel;
    private LookupDao lookupDao;

    public static final String LOOKUPS_UPDATED = "lookups-updated";

    @Override
    public void onApplicationEvent(LookupsUpdatedEvent lookupsUpdatedEvent)
    {
        String lookups = getLookupDao().getMergedLookups();

        Map<String, Object> message = new HashMap<>();
        message.put("eventType", LOOKUPS_UPDATED);
        message.put("lookupsData", lookups);

        log.debug("Send lookups updated trigger");

        getConfigurationUpdatedChannel().send(MessageBuilder.withPayload(message).build());
    }

    public MessageChannel getConfigurationUpdatedChannel()
    {
        return configurationUpdatedChannel;
    }

    public void setConfigurationUpdatedChannel(MessageChannel configurationUpdatedChannel)
    {
        this.configurationUpdatedChannel = configurationUpdatedChannel;
    }

    public LookupDao getLookupDao()
    {
        return lookupDao;
    }

    public void setLookupDao(LookupDao lookupDao)
    {
        this.lookupDao = lookupDao;
    }
}
