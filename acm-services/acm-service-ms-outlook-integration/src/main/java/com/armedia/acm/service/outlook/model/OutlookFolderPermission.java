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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import microsoft.exchange.webservices.data.core.enumeration.permission.PermissionScope;
import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionLevel;
import microsoft.exchange.webservices.data.core.enumeration.permission.folder.FolderPermissionReadAccess;

/**
 * Created by nebojsha on 09.05.2015.
 */
public class OutlookFolderPermission
{

    private String email;
    private FolderPermissionLevel level;
    private boolean canCreateItems;
    private boolean canCreateSubFolders;
    private boolean folderOwner;
    private boolean folderVisible;
    private boolean folderContact;
    private PermissionScope editItems;
    private PermissionScope deleteItems;
    private FolderPermissionReadAccess readItems;

    public OutlookFolderPermission()
    {
        level = FolderPermissionLevel.Custom;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public FolderPermissionLevel getLevel()
    {
        return level;
    }

    public void setLevel(FolderPermissionLevel level)
    {
        this.level = level;
    }

    public boolean canCreateItems()
    {
        return canCreateItems;
    }

    public void setCanCreateItems(boolean canCreateItems)
    {
        this.canCreateItems = canCreateItems;
    }

    public boolean canCreateSubFolders()
    {
        return canCreateSubFolders;
    }

    public void setCanCreateSubFolders(boolean canCreateSubFolders)
    {
        this.canCreateSubFolders = canCreateSubFolders;
    }

    public boolean isFolderOwner()
    {
        return folderOwner;
    }

    public void setFolderOwner(boolean folderOwner)
    {
        this.folderOwner = folderOwner;
    }

    public boolean isFolderVisible()
    {
        return folderVisible;
    }

    public void setFolderVisible(boolean folderVisible)
    {
        this.folderVisible = folderVisible;
    }

    public boolean isFolderContact()
    {
        return folderContact;
    }

    public void setFolderContact(boolean folderContact)
    {
        this.folderContact = folderContact;
    }

    public PermissionScope getEditItems()
    {
        return editItems;
    }

    public void setEditItems(PermissionScope editItems)
    {
        this.editItems = editItems;
    }

    public PermissionScope getDeleteItems()
    {
        return deleteItems;
    }

    public void setDeleteItems(PermissionScope deleteItems)
    {
        this.deleteItems = deleteItems;
    }

    public FolderPermissionReadAccess getReadItems()
    {
        return readItems;
    }

    public void setReadItems(FolderPermissionReadAccess readItems)
    {
        this.readItems = readItems;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof OutlookFolderPermission))
            return false;
        OutlookFolderPermission other = (OutlookFolderPermission) obj;
        return StringUtils.equals(this.getEmail(), other.getEmail())
                && this.getLevel().equals(other.getLevel());
    }

    @Override
    public int hashCode()
    {
        if (getEmail() == null)
            return super.hashCode();
        if (getLevel() == null)
            return super.hashCode();
        else
            return (getEmail() + getLevel().name()).hashCode();
    }
}
