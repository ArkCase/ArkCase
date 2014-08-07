package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marst on 8/6/14.
 */
public class DashboardDto {
String userId;
String dashboardConfig;
boolean updated = false;
boolean inserted = false;

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDashboardConfig() {
        return dashboardConfig;
    }

    public void setDashboardConfig(String dashboardConfig) {
        this.dashboardConfig = dashboardConfig;
    }
}
