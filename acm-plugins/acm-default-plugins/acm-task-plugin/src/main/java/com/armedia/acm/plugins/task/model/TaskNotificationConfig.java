package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import org.springframework.beans.factory.annotation.Value;

public class TaskNotificationConfig
{
    @Value("${due.tasks.notification.enabled}")
    private Boolean dueTasksNotificationEnabled;

    /**
     * Text to be inserted as a subject in the notification email.
     */
    @Value("${upcoming.tasks.notification.subject}")
    private String upcomingTasksNotificationSubject;

    // "The task %s with ID %s is due on %t."
    /**
     * Formatting string to be used for producing text to inserted as a body in the notification email. The formating
     * string accept 3 parameters marked with %1$s, %2$s and %3$s. First parameter is the task title. second is the task
     * ID, and the third is the due date.
     */
    @Value("${upcoming.tasks.notification.body.template}")
    private String upcomingTasksNotificationBodyTemplate;

    // Task overdue notification.
    /**
     * Text to be inserted as a subject in the notification email.
     */
    @Value("${overdue.tasks.notification.subject}")
    private String overdueTasksNotificationSubject;

    // "The task %s with ID %s was due on %t."
    /**
     * Formatting string to be used for producing text to inserted as a body in the notification email. The formating
     * string accept 3 parameters marked with %1$s, %2$s and %3$s. First parameter is the task title. second is the task
     * ID, and the third is the due date.
     */
    @Value("${overdue.tasks.notification.body.template}")
    private String overdueTasksNotificationBodyTemplate;

    public Boolean getDueTasksNotificationEnabled()
    {
        return dueTasksNotificationEnabled;
    }

    public void setDueTasksNotificationEnabled(Boolean dueTasksNotificationEnabled)
    {
        this.dueTasksNotificationEnabled = dueTasksNotificationEnabled;
    }

    public String getUpcomingTasksNotificationSubject()
    {
        return upcomingTasksNotificationSubject;
    }

    public void setUpcomingTasksNotificationSubject(String upcomingTasksNotificationSubject)
    {
        this.upcomingTasksNotificationSubject = upcomingTasksNotificationSubject;
    }

    public String getUpcomingTasksNotificationBodyTemplate()
    {
        return upcomingTasksNotificationBodyTemplate;
    }

    public void setUpcomingTasksNotificationBodyTemplate(String upcomingTasksNotificationBodyTemplate)
    {
        this.upcomingTasksNotificationBodyTemplate = upcomingTasksNotificationBodyTemplate;
    }

    public String getOverdueTasksNotificationSubject()
    {
        return overdueTasksNotificationSubject;
    }

    public void setOverdueTasksNotificationSubject(String overdueTasksNotificationSubject)
    {
        this.overdueTasksNotificationSubject = overdueTasksNotificationSubject;
    }

    public String getOverdueTasksNotificationBodyTemplate()
    {
        return overdueTasksNotificationBodyTemplate;
    }

    public void setOverdueTasksNotificationBodyTemplate(String overdueTasksNotificationBodyTemplate)
    {
        this.overdueTasksNotificationBodyTemplate = overdueTasksNotificationBodyTemplate;
    }
}
