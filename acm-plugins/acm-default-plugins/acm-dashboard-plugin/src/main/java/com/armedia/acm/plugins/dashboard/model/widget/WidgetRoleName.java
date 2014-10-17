package com.armedia.acm.plugins.dashboard.model.widget;

/**
 * Created by marjan.stefanoski on 10/1/2014.
 */
public class WidgetRoleName {

    public WidgetRoleName(){
        super();
    }
    public WidgetRoleName(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
