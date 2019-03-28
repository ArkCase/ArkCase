package com.armedia.acm.services.mediaengine.factory;

/*-
 * #%L
 * ACM Service: Media engine
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

import com.armedia.acm.services.mediaengine.exception.MediaEngineServiceNotFoundException;
import com.armedia.acm.services.mediaengine.service.MediaEngineService;
import com.armedia.acm.spring.SpringContextHolder;

import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com>
 */
public class MediaEngineServiceFactory implements InitializingBean
{
    private Map<String, MediaEngineService> services;
    private SpringContextHolder springContextHolder;

    public MediaEngineService getService(String name) throws MediaEngineServiceNotFoundException
    {
        if (services.containsKey(name))
        {
            return services.get(name);
        }

        throw new MediaEngineServiceNotFoundException(
                String.format("Service [%s] not found.", name));
    }

    public Map<String, MediaEngineService> getServices()
    {
        return services;
    }

    public void setServices(Map<String, MediaEngineService> services)
    {
        this.services = services;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        services = springContextHolder.getAllBeansOfType(MediaEngineService.class)
                .values()
                .stream()
                .collect(Collectors.toMap((MediaEngineService it) -> it.getServiceName(), Function.identity()));
    }
}
