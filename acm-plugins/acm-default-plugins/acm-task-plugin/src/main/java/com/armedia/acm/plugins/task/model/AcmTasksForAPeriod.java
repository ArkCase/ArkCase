package com.armedia.acm.plugins.task.model;

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

/**
 * Created by marjan.stefanoski on 8/29/2014.
 */
public enum AcmTasksForAPeriod
{

    ALL("all"), PAST_DUE("pastDue"), DUE_TOMORROW("dueTomorrow"), DUE_IN_7_DAYS("dueInAWeek"), DUE_IN_30_DAYS("dueInAMonth"), NONE("none");

    private String period;

    private AcmTasksForAPeriod(String period)
    {
        this.period = period;
    }

    public static AcmTasksForAPeriod getTasksForPeriodByText(String text)
    {
        for (AcmTasksForAPeriod attribute : values())
        {
            if (attribute.period.equals(text))
            {
                return attribute;
            }
        }
        return AcmTasksForAPeriod.NONE;
    }

    public String getPeriod()
    {
        return period;
    }
}
