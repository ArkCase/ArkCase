package com.armedia.acm.services.mediaengine.model;

/*-
 * #%L
 * ACM Service: Media engine
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

import org.activiti.engine.runtime.ProcessInstance;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/15/2018
 */
public class MediaEngineProcessInstanceCreatedDateComparator implements Comparator<ProcessInstance>
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
