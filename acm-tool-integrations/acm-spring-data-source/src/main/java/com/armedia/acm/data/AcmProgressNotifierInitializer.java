package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Sep 22, 2017
 *
 */
public class AcmProgressNotifierInitializer implements ApplicationListener<ContextRefreshedEvent>
{

    private AcmProgressNotifier progressNotifier;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, AcmProgressNotifierMessageBuilder> progressNotifierMessageBuilders = applicationContext
                .getBeansOfType(AcmProgressNotifierMessageBuilder.class);
        Map<String, AcmProgressNotifierMessageBuilder> mappedKeys = progressNotifierMessageBuilders.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getValue().getObjectType(), e -> e.getValue()));
        if (!mappedKeys.isEmpty())
        {
            progressNotifier.setMessageBuilders(mappedKeys);
        }
    }

    /**
     * @param progressNotifier
     *            the progressNotifier to set
     */
    public void setProgressNotifier(AcmProgressNotifier progressNotifier)
    {
        this.progressNotifier = progressNotifier;
    }

}
