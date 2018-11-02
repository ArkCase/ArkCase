/**
 * 
 */
package com.armedia.acm.form.time.model;

/*-
 * #%L
 * ACM Forms: Time
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

import com.armedia.acm.form.config.xml.ApproverItem;
import com.armedia.acm.frevvo.config.FrevvoFormName;
import com.armedia.acm.frevvo.config.FrevvoFormNamespace;
import com.armedia.acm.frevvo.model.FrevvoForm;
import com.armedia.acm.objectonverter.adapter.DateFrevvoAdapter;
import com.armedia.acm.services.timesheet.model.TimesheetConfig;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Date;
import java.util.List;

/**
 * @author riste.tutureski
 *
 */
@XmlRootElement(name = "form_" + FrevvoFormName.TIMESHEET, namespace = FrevvoFormNamespace.TIMESHEET_NAMESPACE)
public class TimeForm extends FrevvoForm
{

    private Long id;
    private String user;
    private List<String> userOptions;
    private Date period;
    private Date periodUI;
    private List<TimeItem> items;
    private String status;
    private List<String> statusOptions;
    private String details;
    private List<ApproverItem> approvers;
    private List<String> totals;
    private TimesheetConfig timesheetConfig;

    @XmlElement(name = "id")
    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @XmlElement(name = "user")
    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    @XmlTransient
    public List<String> getUserOptions()
    {
        return userOptions;
    }

    public void setUserOptions(List<String> userOptions)
    {
        this.userOptions = userOptions;
    }

    @XmlElement(name = "period")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    public Date getPeriod()
    {
        return period;
    }

    public void setPeriod(Date period)
    {
        this.period = period;
    }

    @XmlElement(name = "periodUI")
    @XmlJavaTypeAdapter(value = DateFrevvoAdapter.class)
    public Date getPeriodUI()
    {
        return periodUI;
    }

    public void setPeriodUI(Date periodUI)
    {
        this.periodUI = periodUI;
    }

    @XmlElement(name = "timeTableItem")
    public List<TimeItem> getItems()
    {
        return items;
    }

    public void setItems(List<TimeItem> items)
    {
        this.items = items;
    }

    @XmlElement(name = "status")
    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @XmlTransient
    public List<String> getStatusOptions()
    {
        return statusOptions;
    }

    public void setStatusOptions(List<String> statusOptions)
    {
        this.statusOptions = statusOptions;
    }

    @XmlElement(name = "details")
    public String getDetails()
    {
        return details;
    }

    public void setDetails(String details)
    {
        this.details = details;
    }

    @XmlElement(name = "approverItem")
    public List<ApproverItem> getApprovers()
    {
        return approvers;
    }

    public void setApprovers(List<ApproverItem> approvers)
    {
        this.approvers = approvers;
    }

    @XmlElement(name = "totalTableItem")
    public List<String> getTotals()
    {
        return totals;
    }

    public void setTotals(List<String> totals)
    {
        this.totals = totals;
    }

    @XmlTransient
    public TimesheetConfig getTimesheetConfig()
    {
        return timesheetConfig;
    }

    public void setTimesheetConfig(TimesheetConfig timesheetConfig)
    {
        this.timesheetConfig = timesheetConfig;
    }
}
