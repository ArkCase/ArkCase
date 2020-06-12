package com.armedia.acm.web.api.service;

/*-
 * #%L
 * ACM Shared Web Artifacts
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

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by jovan.ivanovski on 10/7/2016.
 */
public class ApplicationMetaInfoService implements InitializingBean, ServletContextAware
{

    private final Logger log = LogManager.getLogger(getClass());
    ServletContext servletContext;
    private Map<String, String> version;
    @Value("${extension.groupId:}")
    private String groupId;
    @Value("${extension.artifactId:}")
    private String artifactId;

    public Map<String, String> getVersion()
    {
        return version;
    }

    public void setVersion(Map<String, String> version)
    {
        this.version = version;
    }

    @Override
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    public void findVersion()
    {
        Properties prop = new Properties();

        try (InputStream manifestStream = servletContext.getResourceAsStream("/META-INF/MANIFEST.MF"))
        {
            prop.load(manifestStream);
            version = prop.entrySet().stream().collect(
                    Collectors.toMap(
                            e -> (String) e.getKey(),
                            e -> (String) e.getValue()));
            if (!groupId.isEmpty() && !artifactId.isEmpty())
            {
                extensionVersion();
            }
        }
        catch (IOException e)
        {
            log.warn("Could not open manifest file: {}", e.getMessage(), e);
        }

    }

    public void extensionVersion()
    {
        String extensionVersion;
        Properties prop = new Properties();
        InputStream inputStream = getClass().getResourceAsStream("/META-INF/maven/" + groupId + "/" + artifactId + "/pom.properties");
        try
        {
            prop.load(inputStream);
            extensionVersion = prop.getProperty("version", "");
            version.put("extensionVersion", extensionVersion);
        } catch (IOException e) {
            log.warn("Could not retrieve version from properties file: ", e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        findVersion();
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
}
