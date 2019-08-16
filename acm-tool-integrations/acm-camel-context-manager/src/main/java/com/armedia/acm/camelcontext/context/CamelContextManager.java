package com.armedia.acm.camelcontext.context;

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

import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISActions;
import com.armedia.acm.camelcontext.arkcase.cmis.ArkCaseCMISConstants;
import com.armedia.acm.camelcontext.configuration.ArkCaseCMISConfig;
import com.armedia.acm.camelcontext.configuration.CamelConfigUtils;
import com.armedia.acm.camelcontext.exception.ArkCaseCamelException;
import com.armedia.acm.camelcontext.flow.queue.ArkCaseCMISQueue;
import com.armedia.acm.camelcontext.flow.route.ArkCaseRoute;
import com.armedia.acm.crypto.exceptions.AcmEncryptionException;
import com.armedia.acm.crypto.properties.AcmEncryptablePropertyUtils;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.engine.DefaultProducerTemplate;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Aug, 2019
 */
public class CamelContextManager implements ApplicationContextAware
{
    private static final String routesPackage = "com.armedia.acm.camelcontext.flow.route";
    private static final String queuesPackage = "com.armedia.acm.camelcontext.flow.queue";
    private Logger log = LogManager.getLogger(getClass());

    private Map<String, ArkCaseCMISQueue> queues;
    private Map<String, ArkCaseCMISConfig> repositoryConfigs;
    private CamelConfigUtils camelConfigUtils;
    private ApplicationContext applicationContext;
    private CamelContext camelContext;
    private AcmEncryptablePropertyUtils encryptablePropertyUtils;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
        if (getRepositoryConfigs() == null)
        {
            try
            {
                repositoryConfigs = getCamelConfigUtils().getRepositoryConfigsFromFile();
            }
            catch (IOException e)
            {
                log.error("Could not retrieve .properties files: " + e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }

        if (getCamelContext() == null)
        {
            try
            {
                startCamelContext(applicationContext);
            }
            catch (Exception e)
            {
                log.error("Could not start Camel context: " + e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    public void updateRepositoryConfigs()
    {
        try
        {
            int count = repositoryConfigs.size();
            Map<String, ArkCaseCMISConfig> refreshedConfigs = new HashMap<>();
            refreshedConfigs = getCamelConfigUtils().getRepositoryConfigsFromFile();

            if (refreshedConfigs.size() != count)
            {
                shutdownBeans();
                setRepositoryConfigs(refreshedConfigs);
                setCamelContext(null);
                try
                {
                    startCamelContext(applicationContext);
                }
                catch (Exception e)
                {
                    log.error("Could not start Camel context: " + e.getMessage(), e);
                    throw new IllegalStateException(e);
                }
            }
            else
            {
                setRepositoryConfigs(refreshedConfigs);
            }
        }
        catch (IOException e)
        {
            log.error("Could not retrieve .properties files: " + e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    private void startCamelContext(ApplicationContext applicationContext) throws Exception
    {
        log.debug("Starting camel context");
        CamelContext camelContext = new DefaultCamelContext();
        // Be prepared for extensive regression testing if you want access to Spring beans in any way
        // other than this way (e.g., appRegistry.get("ArkContent").getBean("beanName")).
        camelContext.getRegistry().bind("arkContext", applicationContext);

        createCamelRoutes(camelContext);
        createCamelQueues(camelContext);

        camelContext.start();
        log.debug("Camel context successfully started.");
    }

    private void createCamelQueues(CamelContext camelContext) throws ArkCaseCamelException
    {
        try
        {
            Reflections queuesReflections = new Reflections(queuesPackage);
            if (queues == null)
            {
                queues = new HashMap<>();
            }
            Set<Class<? extends ArkCaseCMISQueue>> queues = queuesReflections.getSubTypesOf(ArkCaseCMISQueue.class);
            for (ArkCaseCMISConfig repositoryConfig : repositoryConfigs.values())
            {
                for (Class queue : queues)
                {
                    ProducerTemplate template = new DefaultProducerTemplate(camelContext);
                    template.start();

                    ArkCaseCMISQueue arkCaseQueue = (ArkCaseCMISQueue) queue
                            .getConstructor(ProducerTemplate.class, String.class, String.class)
                            .newInstance(template, repositoryConfig.getId(), repositoryConfig.getTimeout());
                    getQueues().put(repositoryConfig.getId() + queue.getName(), arkCaseQueue);
                }
            }
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            log.error("Cant create queues for camel, REASON:{}", e.getMessage(), e);
            throw new ArkCaseCamelException(e);
        }
    }

    private void createCamelRoutes(CamelContext camelContext) throws ArkCaseCamelException
    {
        try
        {
            Reflections routesReflections = new Reflections(routesPackage);
            Set<Class<? extends RouteBuilder>> routes = routesReflections.getSubTypesOf(RouteBuilder.class);

            for (ArkCaseCMISConfig repositoryConfig : repositoryConfigs.values())
            {
                for (Class route : routes)
                {
                    Object routeInstance = route.newInstance();
                    if (routeInstance instanceof RouteBuilder && routeInstance instanceof ArkCaseRoute)
                    {
                        ((ArkCaseRoute) routeInstance).setRepositoryId(repositoryConfig.getId());
                        ((ArkCaseRoute) routeInstance).setTimeout(repositoryConfig.getTimeout());
                        camelContext.addRoutes((RoutesBuilder) routeInstance);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error("Cant add routes to camel context, REASON:{}", e.getMessage(), e);
            throw new ArkCaseCamelException(e);
        }
    }

    public void shutdownBeans()
    {
        try
        {
            if (camelContext != null)
            {
                log.debug("Stopping Camel context");
                camelContext.stop();
            }
        }
        catch (Exception e)
        {
            log.error("Could not stop Camel context: {}" + e.getMessage(), e);
        }
    }

    public Object send(ArkCaseCMISActions action, Map<String, Object> props) throws ArkCaseCamelException
    {
        log.debug("Sending message on queue={} with cmisDocumentId={}", action.getQueueName(), props.get("cmisDocumentId"));
        try
        {
            String cmisRepositoryId = String.valueOf(props.get("cmisRepositoryId"));
            if (cmisRepositoryId == null || cmisRepositoryId.isEmpty())
            {
                cmisRepositoryId = "alfresco";
            }
            ArkCaseCMISConfig config = repositoryConfigs.get(cmisRepositoryId);
            props.put(ArkCaseCMISConstants.CMIS_API_URL, config.getBaseUrl());
            props.put(SessionParameter.USER, config.getUsername());
            props.put(SessionParameter.PASSWORD, encryptablePropertyUtils.decryptPropertyValue(config.getPassword()));

            ArkCaseCMISQueue queue = getQueues().get(cmisRepositoryId + action.getQueueName());

            return queue.send(props);
        }
        catch (AcmEncryptionException | ArkCaseCamelException e)
        {
            log.error("Error sending message on queue={} with cmisDocumentId={}", action.getQueueName(), props.get("cmisDocumentId"));
            throw new ArkCaseCamelException(e);
        }
    }

    public Map<String, ArkCaseCMISQueue> getQueues()
    {
        return queues;
    }

    public void setQueues(Map<String, ArkCaseCMISQueue> queues)
    {
        this.queues = queues;
    }

    public Map<String, ArkCaseCMISConfig> getRepositoryConfigs()
    {
        return repositoryConfigs;
    }

    public void setRepositoryConfigs(Map<String, ArkCaseCMISConfig> repositoryConfigs)
    {
        this.repositoryConfigs = repositoryConfigs;
    }

    public CamelConfigUtils getCamelConfigUtils()
    {
        return camelConfigUtils;
    }

    public void setCamelConfigUtils(CamelConfigUtils camelConfigUtils)
    {
        this.camelConfigUtils = camelConfigUtils;
    }

    public CamelContext getCamelContext()
    {
        return camelContext;
    }

    public void setCamelContext(CamelContext camelContext)
    {
        this.camelContext = camelContext;
    }

    public AcmEncryptablePropertyUtils getEncryptablePropertyUtils()
    {
        return encryptablePropertyUtils;
    }

    public void setEncryptablePropertyUtils(AcmEncryptablePropertyUtils encryptablePropertyUtils)
    {
        this.encryptablePropertyUtils = encryptablePropertyUtils;
    }
}
