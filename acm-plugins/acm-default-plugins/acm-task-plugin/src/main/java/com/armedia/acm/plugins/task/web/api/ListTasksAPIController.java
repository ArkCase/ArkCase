package com.armedia.acm.plugins.task.web.api;

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

import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.task.model.AcmApplicationTaskEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import com.armedia.acm.plugins.task.service.TaskEventPublisher;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({ "/api/v1/plugin/task", "/api/latest/plugin/task" })
public class ListTasksAPIController
{
    private TaskDao taskDao;
    private TaskEventPublisher taskEventPublisher;
    private PersonAssociationDao personAssociationDao;

    private Logger log = LogManager.getLogger(getClass());

    @RequestMapping(value = "/forUser/{user:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> tasksForUser(
            @PathVariable("user") String user,
            Authentication authentication,
            HttpSession session) throws AcmListObjectsFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Finding tasks assigned to user '" + user + "'");
        }

        String ipAddress = (String) session.getAttribute("acm_ip_address");

        try
        {
            List<AcmTask> retval = getTaskDao().tasksForUser(user);
            // to do: we also should get back the tasks that owner by this user
            for (AcmTask task : retval)
            {
                AcmApplicationTaskEvent event = new AcmApplicationTaskEvent(task, "searchResult",
                        authentication.getName(), true, ipAddress);
                getTaskEventPublisher().publishTaskEvent(event);

            }

            return retval;
        }
        catch (Exception e)
        {
            log.error("List Tasks Failed: " + e.getMessage(), e);
            throw new AcmListObjectsFailedException("task", e.getMessage(), e);
        }

    }

    @RequestMapping(value = "/forPerson/{personId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTask> tasksForUser(
            @PathVariable("personId") Long personId,
            Authentication authentication,
            HttpSession session) throws AcmListObjectsFailedException {

        if ((personId != null))
        {
            try
            {
                String parentType = "TASK";
                List<Long> taskIds = getPersonAssociationDao().findParentIdByPersonId(parentType, personId);
                log.debug("personList size " + taskIds.size());

                List<AcmTask> taskList = new ArrayList<>();
                for (Long taskId : taskIds) {
                    taskList.add(getTaskDao().findById(taskId));
                }
                return taskList;
            }
            catch (PersistenceException e)
            {
                throw new AcmListObjectsFailedException("p", e.getMessage(), e);
            }

        }

        throw new AcmListObjectsFailedException("wrong input", "patenType or parentId are: ", null);
    }


    public TaskDao getTaskDao()
    {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao)
    {
        this.taskDao = taskDao;
    }

    public TaskEventPublisher getTaskEventPublisher()
    {
        return taskEventPublisher;
    }

    public void setTaskEventPublisher(TaskEventPublisher taskEventPublisher)
    {
        this.taskEventPublisher = taskEventPublisher;
    }

    public PersonAssociationDao getPersonAssociationDao()
    {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao)
    {
        this.personAssociationDao = personAssociationDao;
    }
}
