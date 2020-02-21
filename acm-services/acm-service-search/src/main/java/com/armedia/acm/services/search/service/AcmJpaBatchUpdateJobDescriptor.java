package com.armedia.acm.services.search.service;

/*-
 * #%L
 * ACM Service: Search
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

import com.armedia.acm.quartz.scheduler.AcmJobDescriptor;
import com.armedia.acm.services.search.model.SearchConstants;
import com.armedia.acm.spring.SpringContextHolder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Trigger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class AcmJpaBatchUpdateJobDescriptor extends AcmJobDescriptor
{
    private AcmJpaBatchUpdateService jpaBatchUpdateService;
    private SpringContextHolder springContextHolder;

    @Override
    public String getJobName()
    {
        return "jpaBatchUpdateJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws InterruptedException
    {
        Trigger trigger = context.getTrigger();

        JobDataMap triggerJobDataMap = trigger.getJobDataMap();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        boolean onDemandTrigger = trigger.getNextFireTime() == null;

        if (jobDataMap.isEmpty() || (onDemandTrigger && !triggerJobDataMap.isEmpty()))
        {
            jobDataMap.clear();
            jobDataMap.putAll(triggerJobDataMap);
        }

        jpaBatchUpdateService.jpaBatchUpdate(jobDataMap);
    }

    @Override
    public Map<String, String> getJobData()
    {
        Collection<? extends AcmObjectToSolrDocTransformer> transformers = getSpringContextHolder()
                .getAllBeansOfType(AcmObjectToSolrDocTransformer.class).values();

        DateFormat solrDateFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);

        Map<String, String> jobDataMap = new HashMap<>();
        String formattedDate = solrDateFormat.format(new Date());
        // put arkcase transformers first
        for (AcmObjectToSolrDocTransformer transformer : transformers.stream().filter(
                t -> t.getAcmObjectTypeSupported().getCanonicalName().startsWith("com.armedia.acm"))
                .collect(Collectors.toCollection(ArrayList::new)))
        {
            jobDataMap.put(transformer.getAcmObjectTypeSupported().getCanonicalName(), formattedDate);
        }
        // put not arkcase transformers from extensions to be able
        for (AcmObjectToSolrDocTransformer transformer : transformers.stream().filter(
                t -> !t.getAcmObjectTypeSupported().getCanonicalName().startsWith("com.armedia.acm"))
                .collect(Collectors.toCollection(ArrayList::new)))
        {
            jobDataMap.put(transformer.getAcmObjectTypeSupported().getCanonicalName(), formattedDate);
        }

        return jobDataMap;
    }

    public AcmJpaBatchUpdateService getJpaBatchUpdateService()
    {
        return jpaBatchUpdateService;
    }

    public void setJpaBatchUpdateService(AcmJpaBatchUpdateService jpaBatchUpdateService)
    {
        this.jpaBatchUpdateService = jpaBatchUpdateService;
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }
}
