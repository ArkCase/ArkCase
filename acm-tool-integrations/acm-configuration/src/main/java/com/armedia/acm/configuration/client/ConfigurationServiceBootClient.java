package com.armedia.acm.configuration.client;

/*-
 * #%L
 * configuration-client
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

import com.armedia.acm.configuration.api.environment.Environment;
import com.armedia.acm.configuration.api.environment.PropertySource;
import com.armedia.acm.configuration.service.CollectionPropertiesConfigurationService;
import com.armedia.acm.configuration.service.ConfigurationPropertyException;
import com.armedia.acm.configuration.util.MergePropertiesUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides methods for retrieving the configuration from the Spring Cloud Config Server
 */
@Configuration
public class ConfigurationServiceBootClient
{
    private static final Logger logger = LogManager.getLogger(ConfigurationServiceBootClient.class);

    private final static String CONFIGURATION_SERVER_URL = "configuration.server.url";

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Autowired
    private org.springframework.core.env.Environment environment;

    @Autowired
    private CollectionPropertiesConfigurationService collectionPropertiesConfiguration;

    @Bean
    public RestTemplate configRestTemplate()
    {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(60 * 1000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        String serverUsername = (String) configurableEnvironment.getPropertySources()
                .get("bootstrap").getProperty("configuration.server.username");
        String serverPassword = (String) configurableEnvironment.getPropertySources()
                .get("bootstrap").getProperty("configuration.server.password");
        setInterceptors(restTemplate, serverUsername, serverPassword, null);
        return restTemplate;
    }

    public Map<String, Object> loadConfiguration(List<Environment> environments)
    {
        environments = getRemoteEnvironment(configRestTemplate(), null, environments);
        Map<String, Object> result = new HashMap<>();
        for (Environment environment : environments)
        {
            result.putAll(getCompositeMap(environment));
        }
        return result;
    }

    /**
     * Loads configuration from one specific yaml file.
     *
     * - url to the config server
     *
     * @param name
     *            - name of the yaml file that should be loaded
     * @return
     */
    public Map<String, Object> loadConfiguration(String name, List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), name, environments).get(0);
        return getCompositeMap(result);
    }

    /**
     * Loads specific language and checks if there are missing labels it adds them from the default language
     */
    public Map<String, Object> loadLangConfiguration(String name, Map<String, Object> defaultLangMap, List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), name, environments).get(0);
        Map<String, Object> langMap = getCompositeMap(result);

        if (defaultLangMap.size() != langMap.size())
        {
            defaultLangMap.forEach(langMap::putIfAbsent);
        }
        return langMap;
    }

    public Map<String, Object> loadLdapConfiguration(String name, Map<String, Object> defaultLdapMap, List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), name, environments).get(0);
        Map<String, Object> ldapMap = getCompositeMap(result);

        if (defaultLdapMap.size() != ldapMap.size())
        {
            defaultLdapMap.forEach(ldapMap::putIfAbsent);
        }
        return ldapMap;
    }

    public Map<String, Object> loadRuntimeConfigurationMap(List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), null, environments).get(0);
        return getRuntimeOrDefaultCompositeMap(result, false);
    }

    public Map<String, Object> loadWithoutRuntimeConfigurationMap(List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), null, environments).get(0);
        return getRuntimeOrDefaultCompositeMap(result, true);
    }

    public Map<String, Object> loadDefaultConfiguration(String name, List<Environment> environments)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), name, environments).get(0);
        return getRuntimeOrDefaultCompositeMap(result, true);
    }

    private Map<String, Object> getCompositeMap(Environment result)
    {
        Map<String, Object> compositeMap = new HashMap<>();


        if (result.getPropertySources() != null)
        {
            List<String> annotatedProperties = getMapValueAnnotatedPropertyKeys(result.getPropertySources());

            Collections.reverse(result.getPropertySources());

            for (PropertySource source : result.getPropertySources())
            {
                Map<String, Object> map = source.getSource();

                MergePropertiesUtil.mergePropertiesFromSources(compositeMap, map, source, annotatedProperties,
                        getActiveApplicationNames(null));
            }
        }

        return compositeMap;
    }

    private Map<String, Object> getRuntimeOrDefaultCompositeMap(Environment result, boolean withoutRuntime)
    {
        Map<String, Object> compositeMap = new HashMap<>();

        if (result.getPropertySources() != null)
        {
            for (PropertySource source : result.getPropertySources())
            {
                if (withoutRuntime)
                {
                    if (!source.getName().contains("-runtime.yaml"))
                    {
                        compositeMap = source.getSource();
                    }
                }
                else
                {
                    if (source.getName().contains("-runtime.yaml"))
                    {
                        compositeMap = source.getSource();
                    }
                }
            }
        }

        return compositeMap;
    }

    public org.springframework.core.env.PropertySource<?> locate(Object name, List<Environment> environments)
    {
        environments = getRemoteEnvironment(configRestTemplate(), name, environments);
        CompositePropertySource compositePropertySource = new CompositePropertySource("configService");

        for (Environment env : environments)
        {
            if (env.getPropertySources() != null)
            {
                for (PropertySource source : env.getPropertySources())
                {
                    Map<String, Object> map = source.getSource();
                    compositePropertySource.addPropertySource(new MapPropertySource(source.getName(), map));
                }
            }
        }
        return compositePropertySource;
    }

    /**
     *
     * @param restTemplate
     * @return
     */
    public List<Environment> getRemoteEnvironment(RestTemplate restTemplate, Object name, List<Environment> environments)
    {
        String url = environment.getProperty(CONFIGURATION_SERVER_URL);

        String path = "/{name}/{profile}";

        List<String> names = getActiveApplicationNames(name);

        Object profiles = getApplicationProfile();

        String activeProfiles;
        if (profiles == null)
        {
            activeProfiles = "default";
        }
        else if (profiles instanceof List)
        {
            activeProfiles = String.join(",", (List) profiles);
        }
        else
        {
            activeProfiles = (String) profiles;
        }

        if (environments == null)
        {
            environments = names.parallelStream().map(nameElement -> {
                Object[] args = new String[] { nameElement, activeProfiles };

                try
                {
                    ResponseEntity<Environment> response = restTemplate.exchange(url + path, HttpMethod.GET, null, Environment.class, args);
                    return Objects.requireNonNull(response).getBody();
                }
                catch (Throwable t)
                {
                    logger.error(t.getMessage(), t);
                }
                return null;
            }).collect(Collectors.toList());
        }
        else
        {
            return environments;
        }

        return environments;
    }

    private List<String> getActiveApplicationNames(Object name)
    {
        List<String> names;
        if (name == null)
        {
            name = getActiveApplicationName();

            if (StringUtils.isEmpty(name))
            {
                throw new IllegalStateException("Application name by configuration can't be empty");
            }
            else if (name instanceof List)
            {
                names = (List<String>) name;
            }
            else
            {
                names = Arrays.asList(name.toString());
            }
        }
        else
        {
            names = Arrays.asList(name.toString());
        }
        return names;
    }

    private RestTemplate setInterceptors(RestTemplate restTemplate, String username, String password, String authorization)
    {
        if (password != null && authorization != null)
        {
            throw new IllegalStateException("You must set either 'password' or 'authorization'");
        }

        if (password != null)
        {
            restTemplate.setInterceptors(Arrays.asList(new BasicAuthorizationInterceptor(username, password)));
        }
        else if (authorization != null)
        {
            restTemplate.setInterceptors(Arrays.asList(new GenericAuthorization(authorization)));
        }

        return restTemplate;
    }

    private static class BasicAuthorizationInterceptor implements ClientHttpRequestInterceptor
    {

        private final String username;

        private final String password;

        public BasicAuthorizationInterceptor(String username, String password)
        {
            this.username = username;
            this.password = (password == null ? "" : password);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException
        {
            byte[] token = Base64Utils.encode((this.username + ":" + this.password).getBytes());
            request.getHeaders().add("Authorization", "Basic " + new String(token));
            return execution.execute(request, body);
        }

    }

    private static class GenericAuthorization implements ClientHttpRequestInterceptor
    {

        private final String authorizationToken;

        public GenericAuthorization(String authorizationToken)
        {
            this.authorizationToken = (authorizationToken == null ? "" : authorizationToken);
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException
        {
            request.getHeaders().add("Authorization", authorizationToken);
            return execution.execute(request, body);
        }
    }

    /**
     * Returns list of module names from config server
     *
     * @return
     */
    public List<String> getModulesNames()
    {
        String modulesPath = System.getProperty("configuration.server.url") + getModulesPath();

        try
        {
            ResponseEntity<List<String>> response = configRestTemplate().exchange(
                    modulesPath,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<String>>()
                    {
                    });
            return response.getBody();
        }
        catch (RestClientException e)
        {
            logger.warn("Failed to get modules due to {}", e.getMessage());
            throw new ConfigurationPropertyException("Failed to get modules", e);
        }
    }

    private List<String> getMapValueAnnotatedPropertyKeys(List<PropertySource> propertySources)
    {
        List<String> annotatedProperties = new LinkedList<>();

        for (PropertySource propertySource : propertySources)
        {
            Map<String, Object> map = propertySource.getSource();

            String jpaModelPackages = (String) map.get("jpa.model.packages");

            if (jpaModelPackages != null)
            {
                annotatedProperties = collectionPropertiesConfiguration.getMapValueAnnotatedKeys(jpaModelPackages);
                return annotatedProperties;
            }
        }

        return annotatedProperties;
    }

    public Object getModulesPath()
    {
        return configurableEnvironment.getPropertySources().get("bootstrap").getProperty("configuration.server.modules.path");
    }

    public Object getActiveApplicationName()
    {
        return configurableEnvironment.getPropertySources().get("bootstrap").getProperty("application.name.active");
    }

    public Object getDefaultApplicationName()
    {
        return configurableEnvironment.getPropertySources().get("bootstrap").getProperty("application.name.default");
    }

    public Object getApplicationProfile()
    {
        return configurableEnvironment.getPropertySources().get("bootstrap").getProperty("application.profile");
    }

    public ConfigurableEnvironment getConfigurableEnvironment()
    {
        return configurableEnvironment;
    }

    public void setConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment)
    {
        this.configurableEnvironment = configurableEnvironment;
    }

    public void setEnvironment(org.springframework.core.env.Environment environment)
    {
        this.environment = environment;
    }

}
