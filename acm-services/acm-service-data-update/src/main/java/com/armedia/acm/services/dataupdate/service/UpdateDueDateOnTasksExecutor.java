package com.armedia.acm.services.dataupdate.service;

/*-
 * #%L
 * ACM Service: Data Update Service
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class UpdateDueDateOnTasksExecutor implements AcmDataUpdateExecutor
{
    private TaskDao taskDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private SolrReindexService solrReindexService;

    @Override
    public String getUpdateId()
    {
        return "update_due_date_when_null_on_tasks";
    }

    @Override
    @Async
    public void execute()
    {
        auditPropertyEntityAdapter.setUserId(AcmDataUpdateService.DATA_UPDATE_MODIFIER);

        List<AcmTask> allTasks = getTaskDao().allTasks();

        allTasks.stream().filter(task -> task.getDueDate() == null).forEach(task -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(task.getTaskStartDate());
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            task.setDueDate(calendar.getTime());
            getTaskDao().save(task);
        });
        solrReindexService.reindex(Arrays.asList(AcmTask.class));
    }

    public SolrReindexService getSolrReindexService()
    {
        return solrReindexService;
    }

    public void setSolrReindexService(SolrReindexService solrReindexService)
    {
        this.solrReindexService = solrReindexService;
    }

    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

}
