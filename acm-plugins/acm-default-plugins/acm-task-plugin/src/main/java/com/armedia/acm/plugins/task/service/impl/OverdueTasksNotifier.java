package com.armedia.acm.plugins.task.service.impl;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.AbstractTaskNotifier;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.activiti.engine.task.TaskQuery;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 10, 2016
 */
public class OverdueTasksNotifier extends AbstractTaskNotifier
{

    // Task overdue notification.
    /**
     * Text to be inserted as a subject in the notification email.
     */
    private String messageSubject;

    // "The task %s with ID %s was due on %t."
    /**
     * Formatting string to be used for producing text to inserted as a body in the notification email. The formating
     * string accept 3 parameters marked with %1$s, %2$s and %3$s. First parameter is the task title. second is the task
     * ID, and the third is the due date.
     */
    private String messageBodyTemplate;

    private UserDao userDao;

    /**
     * @param messageSubject
     *            the messageSubject to set
     */
    public void setMessageSubject(String messageSubject)
    {
        this.messageSubject = messageSubject;
    }

    /**
     * @param messageBodyTemplate
     *            the messageBodyTemplate to set
     */
    public void setMessageBodyTemplate(String messageBodyTemplate)
    {
        this.messageBodyTemplate = messageBodyTemplate;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /*
     * (non-Javadoc)
     * @see gov.edtrm.jsapn.alert.service.AbstractTaskNotifier#tasksDueBetween(org.activiti.engine.task.TaskQuery)
     */
    @Override
    protected TaskQuery tasksDueBetween(TaskQuery query)
    {
        return query.dueBefore(queryEndDate()).dueAfter(queryStartDate());
    }

    /**
     * @return
     */
    protected Date queryStartDate()
    {
        return Date.from(LocalDate.now().minusYears(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return
     */
    protected Date queryEndDate()
    {
        return Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.notification.service.EmailBuilder#buildEmail(java.lang.Object, java.util.Map)
     */
    @Override
    public void buildEmail(AcmTask task, Map<String, Object> properties)
    {
        String email = null;
        if (task.getAssignee() != null)
        {
            AcmUser assignee = userDao.findByUserId(task.getAssignee());
            if (assignee != null)
            {
                email = assignee.getMail();
            }
        }

        properties.put("to", email);
        properties.put("subject", messageSubject);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.notification.service.EmailBodyBuilder#buildEmailBody(java.lang.Object)
     */
    @Override
    public String buildEmailBody(AcmTask task)
    {
        return String.format(messageBodyTemplate, task.getTitle(), task.getId(), task.getDueDate());
    }

}
