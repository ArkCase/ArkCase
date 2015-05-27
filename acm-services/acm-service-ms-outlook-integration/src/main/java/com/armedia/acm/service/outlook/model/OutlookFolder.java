package com.armedia.acm.service.outlook.model;

import java.util.List;

/**
 * Created by nebojsha on 09.05.2015.
 */
public class OutlookFolder extends OutlookItem {
    private String parentId;
    private String displayName;
    private List<OutlookFolderPermission> permissions;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<OutlookFolderPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<OutlookFolderPermission> permissions) {
        this.permissions = permissions;
    }
}
