package com.armedia.acm.services.transcribe.model;

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
public class TranscribeProcessInstanceCreatedDateComparator implements Comparator<ProcessInstance>
{
    @Override
    public int compare(ProcessInstance processInstance1, ProcessInstance processInstance2)
    {
        if (processInstance1 == null && processInstance2 == null)
        {
            return 0;
        }

        if (processInstance1 != null && processInstance2 == null)
        {
            return 1;
        }

        if (processInstance1 == null && processInstance2 != null)
        {
            return -1;
        }

        if (processInstance1.getProcessVariables() == null && processInstance2.getProcessVariables() == null)
        {
            return 0;
        }

        if (processInstance1.getProcessVariables() != null && processInstance2.getProcessVariables() == null)
        {
            return 1;
        }

        if (processInstance1.getProcessVariables() == null && processInstance2.getProcessVariables() != null)
        {
            return -1;
        }

        Date date1 = (Date) processInstance1.getProcessVariables().get("CREATED");
        Date date2 = (Date) processInstance2.getProcessVariables().get("CREATED");

        if (date1 == null && date2 == null)
        {
            return 0;
        }

        if (date1 != null && date2 == null)
        {
            return 1;
        }

        if (date1 == null && date2 != null)
        {
            return -1;
        }

        return date1.compareTo(date2);
    }
}
