package com.armedia.acm.plugins.admin.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractPropertiesService
{
    private String propertiesFile;

    public void saveProperties(Map<String, String> properties) throws IOException
    {
        try(FileOutputStream outputStream = new FileOutputStream(new File(getPropertiesFile())))
        {
            Properties props = new Properties();
            props.putAll(properties);
            props.store(outputStream, String.format("Updated at yyyy-MM-dd hh:mm:ss", new Date()));
        }
    }

    public Map<String, String> loadProperties() throws IOException
    {
        Map<String, String> properties = new HashMap<>();
        try(FileInputStream inputStream = new FileInputStream(new File(getPropertiesFile())))
        {
            Properties props = new Properties();
            props.load(inputStream);
            props.forEach((o, o2) -> properties.put((String)o, (String)o2));
        }
        return properties;
    }

    public String getPropertiesFile()
    {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile)
    {
        this.propertiesFile = propertiesFile;
    }
}
