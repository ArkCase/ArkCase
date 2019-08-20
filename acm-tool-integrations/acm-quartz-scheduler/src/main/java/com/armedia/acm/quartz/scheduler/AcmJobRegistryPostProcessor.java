package com.armedia.acm.quartz.scheduler;

/*-
 * #%L
 * ACM Tool Integrations: Quartz Scheduler
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

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class allows registering of further bean definitions of {@link org.quartz.JobDetail} type
 * before initializing beans in the application context.
 * Bean definitions are configured in the file `scheduledJobs.json` in the configuration folder.
 */
public class AcmJobRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, InitializingBean
{
    private List<AcmJobConfig> jobConfigurations;

    private JSONUnmarshaller unmarshaller;

    private String jobsJsonConfig;

    private static final Logger logger = LogManager.getLogger(AcmJobRegistryPostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException
    {
        jobConfigurations.forEach(it -> {
            logger.debug("Register bean definition for job: [{}]", it.getName());
            BeanDefinitionBuilder jobDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(JobDetailFactoryBean.class);
            jobDefinitionBuilder.addPropertyValue("jobClass", it.getClazz());
            jobDefinitionBuilder.addPropertyValue("durability", true);
            if (it.getRequestsRecovery() != null)
            {
                jobDefinitionBuilder.addPropertyValue("requestsRecovery", it.getRequestsRecovery());
            }
            jobDefinitionBuilder.addPropertyValue("jobDataMap", new HashMap<>());
            registry.registerBeanDefinition(it.getName(), jobDefinitionBuilder.getBeanDefinition());
        });
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException
    {

    }

    @Override
    public void afterPropertiesSet()
    {
        JSONObject jsonJobConfiguration = new JSONObject(jobsJsonConfig);
        JSONArray jobs = jsonJobConfiguration.getJSONArray("jobs");
        jobConfigurations = IntStream.range(0, jobs.length())
                .mapToObj(jobs::getJSONObject)
                .map(JSONObject::toString)
                .map(it -> unmarshaller.unmarshall(it, AcmJobConfig.class))
                .collect(Collectors.toList());
    }

    public JSONUnmarshaller getUnmarshaller()
    {
        return unmarshaller;
    }

    public void setUnmarshaller(JSONUnmarshaller unmarshaller)
    {
        this.unmarshaller = unmarshaller;
    }

    public String getJobsJsonConfig()
    {
        return jobsJsonConfig;
    }

    public void setJobsJsonConfig(String jobsJsonConfig)
    {
        this.jobsJsonConfig = jobsJsonConfig;
    }
}
