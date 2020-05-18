package com.armedia.acm.services.comprehendmedical.job;

/*-
 * #%L
 * ACM Service: Transcribe
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
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ComprehendMedicalQueueJobDescriptor extends AcmJobDescriptor
{
    private ComprehendMedicalQueueJob comprehendMedicalQueueJob;

    @Override
    public String getJobName()
    {
        return "comprehendMedicalQueueJob";
    }

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        comprehendMedicalQueueJob.executeTask();
    }

    public ComprehendMedicalQueueJob getComprehendMedicalQueueJob()
    {
        return comprehendMedicalQueueJob;
    }

    public void setComprehendMedicalQueueJob(ComprehendMedicalQueueJob comprehendMedicalQueueJob)
    {
        this.comprehendMedicalQueueJob = comprehendMedicalQueueJob;
    }
}
