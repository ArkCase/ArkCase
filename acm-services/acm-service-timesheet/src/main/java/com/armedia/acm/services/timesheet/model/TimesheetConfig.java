package com.armedia.acm.services.timesheet.model;

/*-
 * #%L
 * ACM Service: Timesheet
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

import com.armedia.acm.pluginmanager.service.AcmPluginConfigBean;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = TimesheetConfig.class)
public class TimesheetConfig implements AcmPluginConfigBean
{
    @Value("${time.plugin.root.folder}")
    private String rootFolder;

    @Value("${time.plugin.search.tree.sort}")
    private String searchTreeSort;

    @JsonProperty("time.plugin.useApprovalWorkflow")
    @Value("${time.plugin.useApprovalWorkflow}")
    private Boolean useApprovalWorkflow;

    public String getRootFolder()
    {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder)
    {
        this.rootFolder = rootFolder;
    }

    public void setSearchTreeSort(String searchTreeSort)
    {
        this.searchTreeSort = searchTreeSort;
    }

    public Boolean getUseApprovalWorkflow()
    {
        return useApprovalWorkflow;
    }

    public void setUseApprovalWorkflow(Boolean useApprovalWorkflow)
    {
        this.useApprovalWorkflow = useApprovalWorkflow;
    }

    @JsonIgnore
    @Override
    public String getSearchTreeFilter()
    {
        return "";
    }

    @JsonIgnore
    @Override
    public String getSearchTreeQuery()
    {
        return "";
    }

    @JsonIgnore
    @Override
    public String getSearchTreeSort()
    {
        return searchTreeSort;
    }
}
