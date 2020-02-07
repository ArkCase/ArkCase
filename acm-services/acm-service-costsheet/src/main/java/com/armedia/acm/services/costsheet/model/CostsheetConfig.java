package com.armedia.acm.services.costsheet.model;

/*-
 * #%L
 * ACM Service: Costsheet
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

import com.armedia.acm.pluginmanager.service.AcmPluginConfigBean;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.springframework.beans.factory.annotation.Value;

@JsonSerialize(as = CostsheetConfig.class)
public class CostsheetConfig implements AcmPluginConfigBean
{
    @Value("${cost.plugin.root.folder}")
    private String rootFolder;

    @Value("${cost.plugin.search.tree.sort}")
    private String searchTreeSort;

    @JsonProperty("cost.plugin.useApprovalWorkflow")
    @Value("${cost.plugin.useApprovalWorkflow}")
    private Boolean useApprovalWorkflow;

    @JsonProperty("cost.plugin.addCostsheetToBilling")
    @Value("${cost.plugin.addCostsheetToBilling}")
    private Boolean addCostsheetToBilling;

    @JsonProperty("cost.plugin.workflowProcessName")
    @Value("${cost.plugin.workflowProcessName}")
    private String workflowProcessName;

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

    public Boolean getUseApprovalWorkflow()
    {
        return useApprovalWorkflow;
    }

    public void setUseApprovalWorkflow(Boolean useApprovalWorkflow)
    {
        this.useApprovalWorkflow = useApprovalWorkflow;
    }

    public Boolean getAddCostsheetToBilling()
    {
        return addCostsheetToBilling;
    }

    public void setAddCostsheetToBilling(Boolean addCostsheetToBilling)
    {
        this.addCostsheetToBilling = addCostsheetToBilling;
    }

    public String getWorkflowProcessName()
    {
        return workflowProcessName;
    }

    public void setWorkflowProcessName(String workflowProcessName)
    {
        this.workflowProcessName = workflowProcessName;
    }
}
