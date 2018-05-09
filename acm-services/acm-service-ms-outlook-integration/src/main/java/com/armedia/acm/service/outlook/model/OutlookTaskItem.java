package com.armedia.acm.service.outlook.model;

/*-
 * #%L
 * ACM Service: MS Outlook integration
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

import java.util.Date;

/**
 * Created by armdev on 4/20/15.
 */
public class OutlookTaskItem extends OutlookItem
{
    private Date dueDate;
    private Date startDate;
    private Date completeDate;
    private boolean complete;
    private double percentComplete;

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getCompleteDate()
    {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate)
    {
        this.completeDate = completeDate;
    }

    public boolean isComplete()
    {
        return complete;
    }

    public void setComplete(boolean complete)
    {
        this.complete = complete;
    }

    public double getPercentComplete()
    {
        return percentComplete;
    }

    public void setPercentComplete(double percentComplete)
    {
        this.percentComplete = percentComplete;
    }

    @Override
    public String toString()
    {
        return "OutlookTaskItem{" +
                "dueDate=" + dueDate +
                ", startDate=" + startDate +
                ", completeDate=" + completeDate +
                ", complete=" + complete +
                ", percentComplete=" + percentComplete +
                "} " + super.toString();
    }
}
