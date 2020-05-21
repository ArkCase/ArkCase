/**
 *
 */
package com.armedia.acm.services.notification.model;

/*-
 * #%L
 * ACM Service: Notification
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

import com.armedia.acm.services.notification.service.Executor;

import java.util.Map;

/**
 * @author riste.tutureski
 */
public class BasicNotificationRule implements NotificationRule
{

    private boolean globalRule;
    private Executor executor;
    private Map<String, Object> jpaProperties;
    private String jpaQuery;

    @Override
    public boolean isGlobalRule()
    {
        return globalRule;
    }

    public void setGlobalRule(boolean globalRule)
    {
        this.globalRule = globalRule;
    }

    @Override
    public Executor getExecutor()
    {
        return executor;
    }

    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }

    @Override
    public Map<String, Object> getJpaProperties()
    {
        return jpaProperties;
    }

    public void setJpaProperties(Map<String, Object> jpaProperties)
    {
        this.jpaProperties = jpaProperties;
    }

    @Override
    public String getJpaQuery()
    {
        return jpaQuery;
    }

    public void setJpaQuery(String jpaQuery)
    {
        this.jpaQuery = jpaQuery;
    }
}
