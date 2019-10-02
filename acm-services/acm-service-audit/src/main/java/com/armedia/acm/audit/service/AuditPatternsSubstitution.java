package com.armedia.acm.audit.service;

/*-
 * #%L
 * ACM Service: Audit Library
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/***
 * Holds static patterns loaded from file'$user.home/.arkcase/acm/auditPatterns.properties'.The substitution string
 * is*default*"$1*****$3".*
 * <p>
 * *Created by Bojan Milenkoski on 30.5.2016
 */

public class AuditPatternsSubstitution
{
    private final static Logger LOG = LogManager.getLogger(AuditPatternsSubstitution.class);

    private static List<Pattern> PATTERNS = new ArrayList<>();
    private static String SUBSTITUTION = "$1*****$3";
    private static String PROPERTIES_FOR_SUBSTITUTION = "${configuration.server.url}/${application.name.default}/";
    private static String PATTERNS_PATH = "/default/spring/auditPatterns.properties";

    static
    {
        String yamlConfiguration = System.getProperty("acm.configurationserver.propertyfile");
        Resource yamlResource = new FileSystemResource(yamlConfiguration);

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(yamlResource);
        Properties properties = yaml.getObject();

        String profilesReversed = getProfilesReversed(properties.getProperty("application.profile"));

        StringSubstitutor sub = new StringSubstitutor();
        String serverUrlAndName = sub.replace(PROPERTIES_FOR_SUBSTITUTION, properties);

        String patternsFileUrl = String.format("%s%s%s", serverUrlAndName, profilesReversed, PATTERNS_PATH);

        try (InputStream inputStream = new URL(patternsFileUrl).openStream())
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null)
            {
                if (!currentLine.startsWith("#"))
                {
                    PATTERNS.add(Pattern.compile(currentLine));
                }
            }
        }
        catch (IOException e)
        {
            LOG.error("Exception reading patterns from file: {}", PATTERNS_PATH, e);
        }
    }

    public static String getProfilesReversed(String profiles)
    {
        String[] splitedProfiles = profiles.split(",");
        Collections.reverse(Arrays.asList(splitedProfiles));
        return StringUtils.join(splitedProfiles, ",");
    }

    public static List<Pattern> getPatterns()
    {
        return PATTERNS;
    }

    public static String getSubstitution()
    {
        return SUBSTITUTION;
    }
}