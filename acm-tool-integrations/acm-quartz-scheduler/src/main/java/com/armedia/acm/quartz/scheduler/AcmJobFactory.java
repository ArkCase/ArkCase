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

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import com.armedia.acm.objectonverter.json.JSONUnmarshaller;
import com.armedia.acm.spring.SpringContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.spi.TriggerFiredBundle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class applies scheduler context, for job dependencies.
 * Also, collects all registered {@link org.quartz.JobDetail} beans and for each bean
 * creates {@link org.quartz.Trigger} based on the configuration file `scheduledJobs.json` in the configuration folder.
 */
public class AcmJobFactory extends SpringBeanJobFactory implements InitializingBean
{
    private SpringContextHolder springContextHolder;

    private String jobsJsonConfig;

    private JSONUnmarshaller unmarshaller;

    private Map<String, JobDetail> jobDetailsMap;

    private List<Trigger> triggerList;

    private Map<String, AcmJobDescriptor> acmSimpleJobDescriptorMap;

    private static final Logger logger = LogManager.getLogger(AcmJobFactory.class);

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle)
    {
        JobDetail jobDetail = bundle.getJobDetail();
        Class<? extends Job> clazz = jobDetail.getJobClass();
        String jobName = jobDetail.getKey().getName();

        Map<String, ? extends Job> jobBeans = springContextHolder.getAllBeansOfType(clazz);
        return jobBeans.values()
                .stream()
                .map(it -> (AcmJobDescriptor) it)
                .filter(it -> it.getJobName().equals(jobName))
                .findFirst()
                .orElseThrow(() -> new NoSuchBeanDefinitionException(jobName));
    }

    @Override
    public void afterPropertiesSet()
    {
        acmSimpleJobDescriptorMap = springContextHolder.getAllBeansOfType(AcmJobDescriptor.class).values()
                .stream()
                .collect(Collectors.toMap(AcmJobDescriptor::getJobName, Function.identity()));

        JSONObject jsonJobConfiguration = new JSONObject(jobsJsonConfig);
        JSONArray jobs = jsonJobConfiguration.getJSONArray("jobs");
        List<AcmJobConfig> jobConfigurations = IntStream.range(0, jobs.length())
                .mapToObj(jobs::getJSONObject)
                .map(JSONObject::toString)
                .map(it -> unmarshaller.unmarshall(it, AcmJobConfig.class))
                .collect(Collectors.toList());

        Predicate<AcmJobConfig> notImplementedConfiguredJob = it -> acmSimpleJobDescriptorMap.containsKey(it.getName());

        jobDetailsMap = jobConfigurations.stream()
                .filter(notImplementedConfiguredJob)
                .peek(it -> logger.info("Job [{}] will be added to the scheduler", it.getName()))
                .map(it -> new AbstractMap.SimpleEntry<>(it.getName(), springContextHolder.getBeanByName(it.getName(), JobDetail.class)))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        triggerList = jobConfigurations.stream()
                .filter(notImplementedConfiguredJob)
                .map(jobConfig -> createTrigger(jobConfig, jobDetailsMap.get(jobConfig.getName())))
                .collect(Collectors.toList());
    }

    public Map<String, AcmJobDescriptor> getAcmSimpleJobDescriptors()
    {
        return acmSimpleJobDescriptorMap;
    }

    public List<JobDetail> getJobDetailList()
    {
        return new ArrayList<>(jobDetailsMap.values());
    }

    public List<Trigger> getTriggerList()
    {
        return triggerList;
    }

    public JSONUnmarshaller getUnmarshaller()
    {
        return unmarshaller;
    }

    public void setUnmarshaller(JSONUnmarshaller unmarshaller)
    {
        this.unmarshaller = unmarshaller;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public String getJobsJsonConfig()
    {
        return jobsJsonConfig;
    }

    public void setJobsJsonConfig(String jobsJsonConfig)
    {
        this.jobsJsonConfig = jobsJsonConfig;
    }

    private TriggerBuilder buildTrigger(AcmJobConfig jobConfig)
    {
        TriggerBuilder triggerBuilder = newTrigger()
                .withIdentity(jobConfig.getName() + "Trigger");

        if (StringUtils.isNotBlank(jobConfig.getCronExpression()))
        {
            logger.info("Trigger with cron schedule [{}] created for job [{}]", jobConfig.getCronExpression(), jobConfig.getName());
            return triggerBuilder
                    .withSchedule(cronSchedule(jobConfig.getCronExpression())
                            .withMisfireHandlingInstructionDoNothing());
        }
        else
        {
            logger.info("Trigger with repeating interval of [{}] seconds created for job [{}]",
                    jobConfig.getRepeatIntervalInSeconds(), jobConfig.getName());
            if (jobConfig.getStartDelayInSeconds() > 0)
            {
                Date startAt = new Date(System.currentTimeMillis() + jobConfig.getStartDelayInSeconds() * 1000);
                triggerBuilder.startAt(startAt);
            }
            return triggerBuilder
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(jobConfig.getRepeatIntervalInSeconds())
                            .withMisfireHandlingInstructionNextWithExistingCount()
                            .repeatForever());
        }
    }


    public Trigger createTrigger(AcmJobConfig jobConfig)
    {
        return buildTrigger(jobConfig).build();
    }

    public Trigger createTrigger(AcmJobConfig jobConfig, JobDetail jobDetail)
    {
        TriggerBuilder triggerBuilder = buildTrigger(jobConfig)
                .forJob(jobDetail);

        AcmJobDescriptor acmJobDescriptor = acmSimpleJobDescriptorMap.get(jobConfig.getName());
        if (acmJobDescriptor != null)
        {
            Map<String, String> jobData = acmJobDescriptor.getJobData();
            if (jobData != null)
            {
                JobDataMap jobDataMap = new JobDataMap(jobData);
                triggerBuilder.usingJobData(jobDataMap);
            }
        }
        return triggerBuilder.build();
    }
}
