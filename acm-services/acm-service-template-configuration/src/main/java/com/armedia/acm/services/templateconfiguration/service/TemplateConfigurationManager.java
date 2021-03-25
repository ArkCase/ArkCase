package com.armedia.acm.services.templateconfiguration.service;

/*-
 * #%L
 * acm-service-template-configuration
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import com.armedia.acm.files.ConfigurationFileChangedEvent;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.services.templateconfiguration.model.Template;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateConfigurationManager implements ApplicationListener<ApplicationEvent>
{

    private final Logger LOG = LogManager.getLogger(getClass());

    private List<Template> templateConfigurations = new ArrayList<>();
    private Resource templatesConfiguration;
    private ObjectConverter objectConverter;

    @Override
    public void onApplicationEvent(ApplicationEvent event)
    {
        if (event instanceof ConfigurationFileChangedEvent
                && (((ConfigurationFileChangedEvent) event).getConfigFile().equals("templates-configuration.json")))
        {
            try
            {
                templateConfigurations = getObjectConverter().getJsonUnmarshaller()
                        .unmarshallCollection(FileUtils.readFileToString(templatesConfiguration.getFile()), List.class, Template.class);
            }
            catch (Exception e)
            {
                LOG.error("Error while reading from config file [{}]", e.getMessage(), e);
            }
        }
        else if (event instanceof ContextRefreshedEvent)
        {
            try
            {
                File file = templatesConfiguration.getFile();
                if (!file.exists())
                {
                    file.createNewFile();
                }

                String resource = FileUtils.readFileToString(file);
                if (resource.isEmpty())
                {
                    resource = "[]";
                }

                templateConfigurations = getObjectConverter().getJsonUnmarshaller()
                        .unmarshallCollection(FileUtils.readFileToString(templatesConfiguration.getFile()), List.class, Template.class);
            }
            catch (IOException ioe)
            {
                throw new IllegalStateException("Error while reading from config file [{}]", ioe);
            }
        }
    }

    public List<Template> getTemplateConfigurations()
    {
        return templateConfigurations;
    }

    public void setTemplateConfigurations(List<Template> templateConfigurations)
    {
        this.templateConfigurations = templateConfigurations;
    }

    public Resource getTemplatesConfiguration()
    {
        return templatesConfiguration;
    }

    public void setTemplatesConfiguration(Resource templatesConfiguration)
    {
        this.templatesConfiguration = templatesConfiguration;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }
}
