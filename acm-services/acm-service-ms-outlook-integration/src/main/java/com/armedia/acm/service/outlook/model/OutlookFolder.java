package com.armedia.acm.service.outlook.model;

import java.util.List;

/**
 * Created by nebojsha on 09.05.2015.
 */
public class OutlookFolder extends OutlookItem {
    private OutlookFolder parent;
    private String displayName;
    private List<OutlookFolderPermission> permissions;

    public OutlookFolder getParent() {
        return parent;
    }

    public void setParent(OutlookFolder parent) {
        this.parent = parent;
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
