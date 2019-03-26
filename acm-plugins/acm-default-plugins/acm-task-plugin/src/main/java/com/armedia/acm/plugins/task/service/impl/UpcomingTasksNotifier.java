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

import com.armedia.acm.plugins.task.service.AbstractTaskNotifier;

import org.activiti.engine.task.TaskQuery;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Oct 10, 2016
 */
public class UpcomingTasksNotifier extends AbstractTaskNotifier
{

    @Override
    protected TaskQuery tasksDueBetween(TaskQuery query)
    {
        return query.dueBefore(queryEndDate()).dueAfter(queryStartDate());
    }

    /**
     * @return
     */
    private Date queryStartDate()
    {
        return Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return
     */
    private Date queryEndDate()
    {
        return Date.from(LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
