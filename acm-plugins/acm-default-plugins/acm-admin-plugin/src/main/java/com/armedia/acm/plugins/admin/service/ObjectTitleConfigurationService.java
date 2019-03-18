package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Default Plugin: admin
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

import com.armedia.acm.configuration.service.ConfigurationPropertyService;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.admin.model.ObjectTitleConfig;
import com.armedia.acm.plugins.admin.model.TitleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ObjectTitleConfigurationService
{
    private Resource objectTitleConfigurationFile;
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Logger log = LoggerFactory.getLogger(getClass());

    private ObjectConverter objectConverter;
    private ObjectTitleConfig objectTitleConfiguration;
    private ConfigurationPropertyService configurationPropertyService;

    public void saveObjectTitleConfiguration(ObjectTitleConfig objectTitleConfiguration)
    {
        configurationPropertyService.updateProperties(objectTitleConfiguration);
    }
    public ObjectTitleConfig getObjectTitleConfig()
    {
        return objectTitleConfiguration;

    }

    public ConfigurationPropertyService getConfigurationPropertyService()
    {
        return configurationPropertyService;
    }

    public void setConfigurationPropertyService(ConfigurationPropertyService configurationPropertyService)
    {
        this.configurationPropertyService = configurationPropertyService;
    }

    public Resource getObjectTitleConfigurationFile()
    {
        return objectTitleConfigurationFile;
    }

    public void setObjectTitleConfigurationFile(Resource objectTitleConfigurationFile)
    {
        this.objectTitleConfigurationFile = objectTitleConfigurationFile;
    }

    public ReadWriteLock getLock()
    {
        return lock;
    }

    public void setLock(ReadWriteLock lock)
    {
        this.lock = lock;
    }

    public Logger getLog()
    {
        return log;
    }

    public void setLog(Logger log)
    {
        this.log = log;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public void setObjectTitleConfiguration(ObjectTitleConfig objectTitleConfiguration)
    {
        this.objectTitleConfiguration = objectTitleConfiguration;
    }
}
