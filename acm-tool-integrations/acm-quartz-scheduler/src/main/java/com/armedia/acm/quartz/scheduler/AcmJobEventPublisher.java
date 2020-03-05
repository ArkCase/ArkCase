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

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class AcmJobEventPublisher implements ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    public static final String BASE_JOB_AUDIT_TYPE = "com.armedia.acm.quartz.scheduler.job";
    public static final String JOB_TRIGGERED = BASE_JOB_AUDIT_TYPE + ".triggered";
    public static final String JOB_PAUSED = BASE_JOB_AUDIT_TYPE + ".paused";
    public static final String JOB_RESUMED = BASE_JOB_AUDIT_TYPE + ".resumed";
    public static final String JOB_COMPLETED = BASE_JOB_AUDIT_TYPE + ".completed";
    public static final String JOB_FAILED = BASE_JOB_AUDIT_TYPE + ".failed";
    public static final String JOB_DELETED = BASE_JOB_AUDIT_TYPE + ".deleted";
    public static final String JOB_SCHEDULED = BASE_JOB_AUDIT_TYPE + ".scheduled";
    public static final String JOB_RESCHEDULED = BASE_JOB_AUDIT_TYPE + ".rescheduled";

    public void publishJobEvent(AcmJobState jobState, String eventType, String fireInstanceId)
    {
        AcmJobEvent jobEvent = new AcmJobEvent(jobState, eventType, fireInstanceId);
        applicationEventPublisher.publishEvent(jobEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher)
    {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
