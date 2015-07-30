/**
 *
 */
package com.armedia.acm.plugins.task.service;

import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.service.objecthistory.model.AcmObjectHistoryEvent;
import com.armedia.acm.service.objecthistory.service.AcmAssigneeChangeChecker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author riste.tutureski
 */
public class TaskAssigneeChangeChecker extends AcmAssigneeChangeChecker implements ApplicationListener<AcmObjectHistoryEvent>
{

    @Override
    public void onApplicationEvent(AcmObjectHistoryEvent event)
    {
        super.onApplicationEvent(event);
    }

    @Override
    public Class<?> getTargetClass()
    {
        return AcmTask.class;
    }

    @Override
    public String getObjectType(Object in)
    {
        AcmTask task = (AcmTask) in;

        if (task != null)
        {
            return task.getObjectType();
        }

        return null;
    }

    @Override
    public Long getObjectId(Object in)
    {
        AcmTask task = (AcmTask) in;

        if (task != null)
        {
            return task.getTaskId();
        }

        return null;
    }

    @Override
    public String getObjectTitle(Object in)
    {
        AcmTask task = (AcmTask) in;

        if (task != null)
        {
            return task.getTitle();
        }

        return null;
    }

    @Override
    public String getObjectName(Object in)
    {
        AcmTask task = (AcmTask) in;

        if (task != null)
        {
            SimpleDateFormat formatter = new SimpleDateFormat(DateFormats.TASK_NAME_DATE_FORMAT);

            List<String> nameArray = new ArrayList<>();

            if (task.getDueDate() != null)
            {
                nameArray.add(formatter.format(task.getDueDate()));
            }

            if (task.getId() != null)
            {
                nameArray.add(task.getId().toString());
            }

            return StringUtils.join(nameArray, "_");
        }

        return null;
    }

    @Override
    public String getAssignee(Object in)
    {
        AcmTask task = (AcmTask) in;

        if (task != null)
        {
            return task.getAssignee();
        }

        return null;
    }

    @Override
    public boolean isSupportedObjectType(String objectType)
    {
        return "TASK".equals(objectType);
    }
}
