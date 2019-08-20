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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class provides methods for retrieving the configuration from the Spring Cloud Config Server
 */
@Configuration
public class ConfigurationServiceBootClient
{
    private static final Logger logger = LogManager.getLogger(ConfigurationServiceBootClient.class);

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

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

    public Map<String, Object> loadConfiguration(String url)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), url, null);
        return getCompositeMap(result);
    }

    public Map<String, Object> loadConfiguration(String url, String name)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), url, name);
        return getCompositeMap(result);
    }

    private Map<String, Object> getCompositeMap(Environment result)
    {
        Map<String, Object> compositeMap = new HashMap<>();

        if (result.getPropertySources() != null)
        {
            for (PropertySource source : result.getPropertySources())
            {
                Map<String, Object> map = source.getSource();
                map.forEach(compositeMap::putIfAbsent);
            }
        }

        return compositeMap;
    }

    public org.springframework.core.env.PropertySource<?> locate(String url, String name)
    {
        Environment result = getRemoteEnvironment(configRestTemplate(), url, name);
        CompositePropertySource compositePropertySource = new CompositePropertySource("configService");

        if (result.getPropertySources() != null)
        {
            for (PropertySource source : result.getPropertySources())
            {
                Map<String, Object> map = source.getSource();
                compositePropertySource.addPropertySource(new MapPropertySource(source.getName(), map));
            }
        }

        return compositePropertySource;
    }

    /**
     *
     * @param restTemplate
     * @param url
     * @return
     */
    private Environment getRemoteEnvironment(RestTemplate restTemplate, String url, String name)
    {
        String path = "/{name}/{profile}";

        if (name == null)
        {
            name = (String) configurableEnvironment.getPropertySources().get("bootstrap").getProperty("application.name");
        }

        if (StringUtils.isEmpty(name))
        {
            throw new IllegalStateException("Application name by configuration can't be empty");
        }

        Object profiles = configurableEnvironment.getPropertySources()
                .get("bootstrap").getProperty("application.profile");

        String activeProfiles;
        if (profiles == null)
        {
            activeProfiles = "default";
        }
        else if (profiles instanceof List)
        {
            activeProfiles = String.join(",", (ArrayList) profiles);
        }
        else
        {
            activeProfiles = (String) profiles;
        }

        Object[] args = new String[] { name, activeProfiles };

        ResponseEntity<Environment> response = null;
        try
        {
            response = restTemplate.exchange(url + path, HttpMethod.GET, null, Environment.class, args);
        }
        catch (Throwable t)
        {
            logger.error(t.getMessage(), t);
        }

        return Objects.requireNonNull(response).getBody();
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

    public ConfigurableEnvironment getConfigurableEnvironment()
    {
        return configurableEnvironment;
    }

    public void setConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment)
    {
        this.configurableEnvironment = configurableEnvironment;
    }
}
