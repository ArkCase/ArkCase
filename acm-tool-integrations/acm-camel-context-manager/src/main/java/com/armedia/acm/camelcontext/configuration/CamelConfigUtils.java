package com.armedia.acm.camelcontext.configuration;

/*-
 * #%L
 * acm-camel-context-manager
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

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Sep, 2019
 */
public class CamelConfigUtils
{

    private PropertyFileManager propertyFileManager;
    private AcmCamelConfig acmCamelConfig;

    public Map<String, ArkCaseCMISConfig> getRepositoryConfigsFromFile() throws IOException
    {
        Map<String, ArkCaseCMISConfig> configMap = new HashMap<>();
        List<String> propertyFiles = getAllPropertieFiles();
        for (String propertieFile : propertyFiles)
        {
            Map<String, String> properties = getPropertyFileManager().readFromFileAsMap(new File(propertieFile));

            final ObjectMapper mapper = new ObjectMapper();
            final ArkCaseCMISConfig config = mapper.convertValue(properties, ArkCaseCMISConfig.class);
            // foreach map this to ArkCaseConfig object
            configMap.put(config.getId(), config);
        }
        return configMap;
    }

    public List<String> getAllPropertieFiles() throws IOException
    {
        List<String> properties = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(getAcmCamelConfig().getWorkingDirectory())))
        {
            return paths
                    .filter(Files::isRegularFile)
                    .map(x -> x.toString())
                    .filter(f -> f.endsWith(".properties"))
                    .collect(Collectors.toList());
        }
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public AcmCamelConfig getAcmCamelConfig()
    {
        return acmCamelConfig;
    }

    public void setAcmCamelConfig(AcmCamelConfig acmCamelConfig)
    {
        this.acmCamelConfig = acmCamelConfig;
    }
}
