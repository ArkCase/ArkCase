package com.armedia.acm.service.outlook.model;

import microsoft.exchange.webservices.data.enumeration.FolderPermissionLevel;
import microsoft.exchange.webservices.data.enumeration.FolderPermissionReadAccess;
import microsoft.exchange.webservices.data.enumeration.PermissionScope;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by nebojsha on 09.05.2015.
 */
public class OutlookFolderPermission {
    private String email;
    private FolderPermissionLevel level;
    private boolean canCreateItems;
    private boolean canCreateSubFolders;
    private boolean isFolderOwner;
    private boolean isFolderVisible;
    private boolean isFolderContact;
    private PermissionScope editItems;
    private PermissionScope deleteItems;
    private FolderPermissionReadAccess readItems;

    public OutlookFolderPermission() {
        level = FolderPermissionLevel.Custom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FolderPermissionLevel getLevel() {
        return level;
    }

    public void setLevel(FolderPermissionLevel level) {
        this.level = level;
    }

    public boolean isCanCreateItems() {
        return canCreateItems;
    }

    public void setCanCreateItems(boolean canCreateItems) {
        this.canCreateItems = canCreateItems;
    }

    public boolean isCanCreateSubFolders() {
        return canCreateSubFolders;
    }

    public void setCanCreateSubFolders(boolean canCreateSubFolders) {
        this.canCreateSubFolders = canCreateSubFolders;
    }

    public boolean isFolderOwner() {
        return isFolderOwner;
    }

    public void setFolderOwner(boolean isFolderOwner) {
        this.isFolderOwner = isFolderOwner;
    }

    public boolean isFolderVisible() {
        return isFolderVisible;
    }

    public void setFolderVisible(boolean isFolderVisible) {
        this.isFolderVisible = isFolderVisible;
    }

    public boolean isFolderContact() {
        return isFolderContact;
    }

    public void setFolderContact(boolean isFolderContact) {
        this.isFolderContact = isFolderContact;
    }

    public PermissionScope getEditItems() {
        return editItems;
    }

    public void setEditItems(PermissionScope editItems) {
        this.editItems = editItems;
    }

    public PermissionScope getDeleteItems() {
        return deleteItems;
    }

    public void setDeleteItems(PermissionScope deleteItems) {
        this.deleteItems = deleteItems;
    }

    public FolderPermissionReadAccess getReadItems() {
        return readItems;
    }

    public void setReadItems(FolderPermissionReadAccess readItems) {
        this.readItems = readItems;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OutlookFolderPermission))
            return false;
        OutlookFolderPermission other = (OutlookFolderPermission) obj;
        return StringUtils.equals(this.getEmail(), other.getEmail())
                && this.getLevel().equals(other.getLevel());
    }

    @Override
    public int hashCode() {
        if (getEmail() == null)
            return super.hashCode();
        if (getLevel() == null)
            return super.hashCode();
        else
            return (getEmail() + getLevel().name()).hashCode();
    }
}
