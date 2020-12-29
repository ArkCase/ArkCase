package com.armedia.acm.plugins.task.model.provider;

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
