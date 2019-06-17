package com.armedia.acm.configuration.core;

/*-
 * #%L
 * ACM Service: Configuration Library
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

import com.armedia.acm.configuration.api.ConfigurationFacade;
import com.armedia.acm.configuration.client.ConfigurationServiceBootClient;
import com.armedia.acm.core.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holder class for retrieved properties from the property source.
 * Properties are decrypted if they are encrypted in the loaded resource.
 * <p>
 * A value is considered "encrypted" when it appears surrounded by <tt>ENC(...)</tt>, like:
 * <p>
 * <center><tt>my.value=ENC(!"DGAS24FaIO$)</tt></center>
 * <p>
 * Encrypted and unencrypted objects can be combined in the same resources file.
 * </p>
 */
@Configuration
public class ConfigurationContainer implements ConfigurationFacade
{
    private static final Logger log = LogManager.getLogger(ConfigurationContainer.class);

    private volatile static ConfigurationContainer INSTANCE;
    private final static String CONFIGURATION_SERVER_URL = "configuration.server.url";

    private Map<String, Object> configurationMap = null;

    @Autowired
    private ConfigurationServiceBootClient configurationServiceBootClient;

    @Autowired
    private Environment environment;

    @Autowired
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    @Bean
    public static ConfigurationContainer configurationContainer()
    {
        if (INSTANCE == null)
        {
            initialize();
        }
        return INSTANCE;
    }

    private static synchronized void initialize()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new ConfigurationContainer();
        }
    }

    private synchronized void initializeConfigurationMap()
    {
            String url = this.environment.getProperty(CONFIGURATION_SERVER_URL);
            Map<String, Object> configurationMap = this.configurationServiceBootClient.loadConfiguration(url);
            this.configurationMap = configurationMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, it -> {
                        if (it.getValue() instanceof String)
                        {
                            try
                            {
                                return encryptablePropertyUtils.decryptPropertyValue(it.getValue().toString());
                            }
                            catch (AcmEncryptionException e)
                            {
                                log.error("Property [{}] can't be decrypted", it.getValue().toString());
                                throw new RuntimeCryptoException("Failed to convert property value. Reason:" + e.getMessage());
                            }
                        }
                        else
                        {
                            return it.getValue();
                        }
                    }));
    }

    @Override
    public Object getProperty(String name)
    {
        return this.configurationMap.get(name);
    }

    @Override
    public Object getProperty(String channel, String stage, String instance, String name)
    {
        String propertyName = String.format("%s.%s.%s.%s", channel, stage, name, instance);
        return getProperty(propertyName);
    }

    @Override
    public void refresh()
    {
        initializeConfigurationMap();
    }

    public Map<String, Object> getConfigurationMap()
    {
        return this.configurationMap;
    }
}
