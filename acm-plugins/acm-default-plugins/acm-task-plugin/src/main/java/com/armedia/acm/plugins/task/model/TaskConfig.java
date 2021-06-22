package com.armedia.acm.plugins.task.model;

/*-
 * #%L
 * ACM Default Plugin: Tasks
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
import com.armedia.acm.plugins.ecm.service.SupportsFileTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

public class TaskConfig implements SupportsFileTypes, AcmPluginConfigBean
{
    @JsonProperty("task.plugin.search.tree.filter")
    @Value("${task.plugin.search.tree.filter}")
    private String searchTreeFilter;

    @JsonProperty("task.plugin.search.tree.sort")
    @Value("${task.plugin.search.tree.sort}")
    private String searchTreeSort;

    @JsonProperty("task.plugin.search.tree.searchQuery")
    @Value("${task.plugin.search.tree.searchQuery}")
    private String searchTreeSearchQuery;

    @JsonProperty("task.plugin.fileTypes")
    @Value("${task.plugin.fileTypes}")
    private String supportedFileTypes;

    @JsonProperty("task.plugin.sendCompleteEmail")
    @Value("${task.plugin.sendCompleteEmail}")
    private Boolean sendCompleteEmail;

    @JsonProperty("task.plugin.email.folder.relative.path")
    @Value("${task.plugin.email.folder.relative.path}")
    private String emailFolderRelativePath;

    @JsonProperty("task.plugin.email.regex.task_id")
    @Value("${task.plugin.email.regex.task_id}")
    private String emailRegexTaskId;

    @JsonProperty("task.plugin.email.regex.object_type")
    @Value("${task.plugin.email.regex.object_type}")
    private String emailRegexObjectType;

    @JsonProperty("task.plugin.email.handler.enabled")
    @Value("${task.plugin.email.handler.enabled}")
    private Boolean emailHandlerEnabled;

    public void setSearchTreeFilter(String searchTreeFilter)
    {
        this.searchTreeFilter = searchTreeFilter;
    }

    public void setSearchTreeSort(String searchTreeSort)
    {
        this.searchTreeSort = searchTreeSort;
    }

    public void setSearchTreeSearchQuery(String searchTreeSearchQuery)
    {
        this.searchTreeSearchQuery = searchTreeSearchQuery;
    }

    public String getSupportedFileTypes()
    {
        return supportedFileTypes;
    }

    public void setSupportedFileTypes(String supportedFileTypes)
    {
        this.supportedFileTypes = supportedFileTypes;
    }

    public Boolean getSendCompleteEmail()
    {
        return sendCompleteEmail;
    }

    public void setSendCompleteEmail(Boolean sendCompleteEmail)
    {
        this.sendCompleteEmail = sendCompleteEmail;
    }

    @Override
    public Set<String> getFileTypes()
    {
        return getFileTypes(supportedFileTypes);
    }

    @Override
    public String getSearchTreeFilter()
    {
        return searchTreeFilter;
    }

    @Override
    public String getSearchTreeQuery()
    {
        return searchTreeSearchQuery;
    }

    @Override
    public String getSearchTreeSort()
    {
        return searchTreeSort;
    }

    public String getEmailFolderRelativePath()
    {
        return emailFolderRelativePath;
    }

    public void setEmailFolderRelativePath(String emailFolderRelativePath)
    {
        this.emailFolderRelativePath = emailFolderRelativePath;
    }

    public String getEmailRegexTaskId()
    {
        return emailRegexTaskId;
    }

    public void setEmailRegexTaskId(String emailRegexTaskId)
    {
        this.emailRegexTaskId = emailRegexTaskId;
    }

    public String getEmailRegexObjectType()
    {
        return emailRegexObjectType;
    }

    public void setEmailRegexObjectType(String emailRegexObjectType)
    {
        this.emailRegexObjectType = emailRegexObjectType;
    }

    public Boolean getEmailHandlerEnabled()
    {
        return emailHandlerEnabled;
    }

    public void setEmailHandlerEnabled(Boolean emailHandlerEnabled)
    {
        this.emailHandlerEnabled = emailHandlerEnabled;
    }
}
