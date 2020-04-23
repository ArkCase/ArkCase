package com.armedia.acm.plugins.task.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.dao.AcmFolderDao;
import com.armedia.acm.plugins.ecm.exception.LinkAlreadyExistException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.model.EcmFilePostUploadEvent;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.impl.ActivitiTaskDao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;

public class UploadFileToTaskListener implements ApplicationListener<EcmFilePostUploadEvent>
{

    private ActivitiTaskDao activitiTaskDao;
    private AcmFolderDao acmFolderDao;
    private AcmTaskService acmTaskService;
    private Logger log = LogManager.getLogger(getClass());

    @Override
    public void onApplicationEvent(EcmFilePostUploadEvent event)
    {
        EcmFile file = event.getFile();

        if (!file.getContainer().getContainerObjectType().equalsIgnoreCase("TASK"))
        {
            return;
        }

        AcmTask acmTask = getActivitiTaskDao().findById(file.getParentObjectId());
        if (!acmTask.isAdhocTask() || acmTask.getParentObjectId() == null)
        {
            return;
        }

        try
        {
            String taskFolderName = getAcmTaskService().getTaskFolderNameInParentObject(acmTask);
            if (getAcmFolderDao().findAnyFolderByName(taskFolderName) == null)
            {
                getAcmTaskService().createTaskFolderStructureInParentObject(acmTask);
            }
        }
        catch (AcmUserActionFailedException | AcmCreateObjectFailedException | AcmObjectNotFoundException | LinkAlreadyExistException e)
        {
            log.error("Exception occurred while trying to create new folder structure in parent object: {}", e.getMessage());
        }
    }

    public ActivitiTaskDao getActivitiTaskDao()
    {
        return activitiTaskDao;
    }

    public void setActivitiTaskDao(ActivitiTaskDao activitiTaskDao)
    {
        this.activitiTaskDao = activitiTaskDao;
    }

    public AcmTaskService getAcmTaskService()
    {
        return acmTaskService;
    }

    public void setAcmTaskService(AcmTaskService acmTaskService)
    {
        this.acmTaskService = acmTaskService;
    }

    public AcmFolderDao getAcmFolderDao() {
        return acmFolderDao;
    }

    public void setAcmFolderDao(AcmFolderDao acmFolderDao) {
        this.acmFolderDao = acmFolderDao;
    }
}
