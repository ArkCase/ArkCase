package com.armedia.acm.plugins.task.model.provider;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.model.TaskTemplateModel;
import com.armedia.acm.spring.SpringContextHolder;

import java.util.Map;

public class TaskTemplateModelProvider implements TemplateModelProvider<TaskTemplateModel>
{
    private SpringContextHolder springContextHolder;

    @Override
    public TaskTemplateModel getModel(Object object)
    {
        AcmTask task = (AcmTask) object;
        AcmObject acmObject = null;

        if(task.getParentObjectType() != null && task.getParentObjectId() != null)
        {
            AcmAbstractDao<AcmEntity> acmEntityDao = getEntityDao(task.getParentObjectType());
            if(acmEntityDao != null)
            {
                acmObject = (AcmObject) acmEntityDao.find(task.getParentObjectId());
            }
        }

        TaskTemplateModel model = new TaskTemplateModel();
        model.setTask(task);
        model.setAcmObject(acmObject);

        return model;
    }

    @Override
    public Class<TaskTemplateModel> getType()
    {
        return TaskTemplateModel.class;
    }

    private AcmAbstractDao<AcmEntity> getEntityDao(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmEntity> dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }

        return null;
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
