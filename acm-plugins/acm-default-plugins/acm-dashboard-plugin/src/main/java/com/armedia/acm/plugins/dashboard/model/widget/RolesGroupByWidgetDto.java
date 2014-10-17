package com.armedia.acm.plugins.dashboard.model.widget;

import java.util.List;

/**
 * Created by marjan.stefanoski on 9/30/2014.
 */
public class RolesGroupByWidgetDto {

    private String widgetName;
    private String name;
    private List<WidgetRoleName> widgetAuthorizedRoles;
    private List<WidgetRoleName> widgetNotAuthorizedRoles;

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    public List<WidgetRoleName> getWidgetAuthorizedRoles() {
        return widgetAuthorizedRoles;
    }

    public void setWidgetAuthorizedRoles(List<WidgetRoleName> widgetAuthorizedRoles) {
        this.widgetAuthorizedRoles = widgetAuthorizedRoles;
    }

    public List<WidgetRoleName> getWidgetNotAuthorizedRoles() {
        return widgetNotAuthorizedRoles;
    }

    public void setWidgetNotAuthorizedRoles(List<WidgetRoleName> widgetNotAuthorizedRoles) {
        this.widgetNotAuthorizedRoles = widgetNotAuthorizedRoles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
