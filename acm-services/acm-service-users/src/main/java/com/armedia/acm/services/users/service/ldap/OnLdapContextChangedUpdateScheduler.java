package com.armedia.acm.services.users.service.ldap;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.quartz.scheduler.AcmJobConfig;
import com.armedia.acm.quartz.scheduler.AcmJobFactory;
import com.armedia.acm.quartz.scheduler.AcmSchedulerService;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.spring.SpringContextHolder;
import com.armedia.acm.spring.events.AbstractContextHolderEvent;
import com.armedia.acm.spring.events.ContextAddedEvent;
import com.armedia.acm.spring.events.ContextRemovedEvent;
import com.armedia.acm.spring.events.ContextReplacedEvent;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnLdapContextChangedUpdateScheduler implements ApplicationListener<AbstractContextHolderEvent>
{
    private AcmSchedulerService schedulerService;

    private SpringContextHolder contextHolder;

    private AcmJobFactory jobFactory;

    private Pattern pattern = Pattern.compile(".*spring-config(-\\w+)+-ldap\\.xml");

    private static final Logger logger = LoggerFactory.getLogger(OnLdapContextChangedUpdateScheduler.class);

    @Override
    public void onApplicationEvent(AbstractContextHolderEvent event)
    {
        String contextName = event.getContextName();

        if (isSpringLdapConfigFile(contextName))
        {
            String directoryId = StringUtils.substringBeforeLast(contextName, "-ldap.xml");
            directoryId = StringUtils.substringAfter(directoryId, "spring-config-");

            AcmLdapSyncConfig ldapSyncConfig = contextHolder.getBeanByNameIncludingChildContexts(String.format("%s_sync", directoryId),
                    AcmLdapSyncConfig.class);

            String ldapSyncJobName = String.format("%s_ldapSyncJob", directoryId);
            String ldapSyncTriggerName = String.format("%s_ldapSyncJobTrigger", directoryId);

            String ldapPartialSyncJobName = String.format("%s_ldapPartialSyncJob", directoryId);
            String ldapPartialSyncTriggerName = String.format("%s_ldapPartialSyncJobTrigger", directoryId);

            if (event instanceof ContextAddedEvent)
            {
                if (!schedulerService.isJobScheduled(ldapSyncTriggerName))
                {
                    scheduleJob(ldapSyncJobName, ldapSyncConfig.getFullSyncCron());
                    logger.info("Schedule ldap sync job [{}] for directory [{}].", ldapSyncJobName, directoryId);
                }
                if (!schedulerService.isJobScheduled(ldapPartialSyncTriggerName))
                {
                    scheduleJob(ldapPartialSyncJobName, ldapSyncConfig.getPartialSyncCron());
                    logger.info("Schedule ldap partial sync job [{}] for directory [{}].", ldapPartialSyncJobName, directoryId);
                }

                // when new ldap configuration added trigger full ldap sync
                schedulerService.triggerJob(ldapSyncJobName);
            }
            else if (event instanceof ContextReplacedEvent)
            {
                rescheduleJob(ldapSyncJobName, ldapSyncConfig.getFullSyncCron());
                logger.info("On ldap context replaced, reschedule job [{}] with trigger [{}].", ldapSyncJobName, ldapSyncTriggerName);

                rescheduleJob(ldapPartialSyncJobName, ldapSyncConfig.getPartialSyncCron());
                logger.info("On ldap context replaced, reschedule job [{}] with trigger [{}].",
                        ldapPartialSyncJobName, ldapPartialSyncJobName);
            }
            else if (event instanceof ContextRemovedEvent)
            {
                logger.info("On ldap context removed, remove ldap sync job [{}] from scheduler.", ldapSyncJobName);
                schedulerService.deleteJob(ldapSyncJobName);

                logger.info("On ldap context removed, remove ldap partial sync job [{}] from scheduler.", ldapPartialSyncJobName);
                schedulerService.deleteJob(ldapPartialSyncJobName);
            }
        }
    }

    private void rescheduleJob(String syncJobName, String cronExpression)
    {
        JobDetail jobDetail = contextHolder.getBeanByNameIncludingChildContexts(syncJobName, JobDetail.class);
        Trigger trigger = jobFactory.createTrigger(getJobConfig(cronExpression, syncJobName), jobDetail);
        schedulerService.rescheduleJob(trigger.getKey().getName(), trigger);
    }

    private void scheduleJob(String syncJobName, String cronExpression)
    {
        JobDetail jobDetail = contextHolder.getBeanByNameIncludingChildContexts(syncJobName, JobDetail.class);
        Trigger trigger = jobFactory.createTrigger(getJobConfig(cronExpression, syncJobName), jobDetail);
        schedulerService.scheduleJob(jobDetail, trigger);
    }

    private AcmJobConfig getJobConfig(String cronExpression, String syncJobName)
    {
        AcmJobConfig jobConfig = new AcmJobConfig();
        jobConfig.setName(syncJobName);
        jobConfig.setCronExpression(cronExpression);
        return jobConfig;
    }

    private boolean isSpringLdapConfigFile(String name)
    {
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public AcmSchedulerService getSchedulerService()
    {
        return schedulerService;
    }

    public void setSchedulerService(AcmSchedulerService schedulerService)
    {
        this.schedulerService = schedulerService;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }

    public AcmJobFactory getJobFactory()
    {
        return jobFactory;
    }

    public void setJobFactory(AcmJobFactory jobFactory)
    {
        this.jobFactory = jobFactory;
    }
}
