package com.armedia.acm.plugins.dashboard.model;

/**
* Created by marjan.stefanoski on 9/12/2014.
*/
public class WidgetRolePrimaryKey {
    private String roleName;

    private Long widgetId;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(Long widgetId) {
        this.widgetId = widgetId;
    }
}
