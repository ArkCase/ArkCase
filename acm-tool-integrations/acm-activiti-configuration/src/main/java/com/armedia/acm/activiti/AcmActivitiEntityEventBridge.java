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

import com.armedia.acm.activiti.model.SpringActivitiEntityEvent;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * Created by dmiller on 12/6/2016.
 */
public class AcmActivitiEntityEventBridge extends BaseEntityEventListener implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected void onCreate(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("create", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    protected void onDelete(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("delete", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    protected void onUpdate(ActivitiEvent event)
    {
        SpringActivitiEntityEvent springActivitiEntityEvent = new SpringActivitiEntityEvent("update", event);
        applicationEventPublisher.publishEvent(springActivitiEntityEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
