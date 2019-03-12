package com.armedia.acm.configuration.yaml;

/*-
 * #%L
 * configuration-yaml
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

import org.apache.commons.configuration.AbstractFileConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

/**
 * Base class representing yaml configuration source.
 */
public class YamlFileConfiguration extends AbstractFileConfiguration
{

    public YamlFileConfiguration()
    {
    }

    public YamlFileConfiguration(File file) throws ConfigurationException
    {
        super(file);
    }

    public YamlFileConfiguration(String url) throws ConfigurationException
    {
        super(url);
    }

    public YamlFileConfiguration(URL url) throws ConfigurationException
    {
        super(url);
    }

    @Override
    public void load(Reader in)
    {
        Yaml yaml = new Yaml();
        Iterable<Object> it_conf = yaml.loadAll(in);

        for (Object obj : it_conf)
        {
            if (obj instanceof Map)
            {
                Map<String, Map<String, Object>> configuration = (Map<String, Map<String, Object>>) obj;
                getKeyValue(configuration, "");
            }
        }
    }

    @Override
    public void save(Writer out)
    {
    }

    private void getKeyValue(Map<String, Map<String, Object>> map, String key)
    {
        String localKey = key;
        for (String configKey : map.keySet())
        {
            Object configValue = map.get(configKey);

            if (configValue instanceof Map)
            {
                key += configKey;
                key += ".";

                getKeyValue((Map<String, Map<String, Object>>) configValue, key);
            }
            else
            {
                key += configKey;
                addProperty(key, configValue.toString());
            }
            key = localKey;
        }
    }
}
