package com.armedia.acm.plugins.person.model;

/*-
 * #%L
 * ACM Default Plugin: Person
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

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class PersonConfig implements AcmPluginConfigBean
{
    @Value("#{'${person.plugin.types}'.split(',')}")
    private List<String> types;

    /**
     * Root folder for all People
     */
    @Value("${person.plugin.folder.root}")
    private String folderRoot;

    /**
     * Person own folder which contains spel expression to generate folder name
     */
    @Value("${person.plugin.folder.own.spel}")
    private String folderOwnSpel;

    @Value("${person.plugin.search.tree.filter}")
    private String searchTreeFilter;

    @Value("${person.plugin.search.tree.sort}")
    private String searchTreeSort;

    @Value("${person.plugin.search.tree.searchQuery}")
    private String searchTreeQuery;

    /**
     * folder where pictures will be kept
     */
    @Value("${person.plugin.folder.pictures}")
    private String picturesFolder;

    public List<String> getTypes()
    {
        return types;
    }

    public void setTypes(List<String> types)
    {
        this.types = types;
    }

    public String getFolderRoot()
    {
        return folderRoot;
    }

    public void setFolderRoot(String folderRoot)
    {
        this.folderRoot = folderRoot;
    }

    public String getFolderOwnSpel()
    {
        return folderOwnSpel;
    }

    public void setFolderOwnSpel(String folderOwnSpel)
    {
        this.folderOwnSpel = folderOwnSpel;
    }

    @Override
    public String getSearchTreeFilter()
    {
        return searchTreeFilter;
    }

    public void setSearchTreeFilter(String searchTreeFilter)
    {
        this.searchTreeFilter = searchTreeFilter;
    }

    @Override
    public String getSearchTreeSort()
    {
        return searchTreeSort;
    }

    public void setSearchTreeSort(String searchTreeSort)
    {
        this.searchTreeSort = searchTreeSort;
    }

    @Override
    public String getSearchTreeQuery()
    {
        return searchTreeQuery;
    }

    public void setSearchTreeQuery(String searchTreeQuery)
    {
        this.searchTreeQuery = searchTreeQuery;
    }

    public String getPicturesFolder()
    {
        return picturesFolder;
    }

    public void setPicturesFolder(String picturesFolder)
    {
        this.picturesFolder = picturesFolder;
    }
}
