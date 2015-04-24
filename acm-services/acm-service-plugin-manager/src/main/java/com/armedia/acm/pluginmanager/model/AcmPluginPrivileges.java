package com.armedia.acm.pluginmanager.model;


import java.io.Serializable;
import java.util.List;

public class AcmPluginPrivileges implements Serializable {

    private static final long serialVersionUID = 5330357171041311491L;
    private String pluginName;
    private List<AcmPluginPrivilege> privileges;
    private List<AcmPluginUrlPrivilege> urlPrivileges;


    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public List<AcmPluginPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<AcmPluginPrivilege> privileges) {
        this.privileges = privileges;
    }

    public List<AcmPluginUrlPrivilege> getUrlPrivileges() {
        return urlPrivileges;
    }

    public void setUrlPrivileges(List<AcmPluginUrlPrivilege> urlPrivileges) {
        this.urlPrivileges = urlPrivileges;
    }

}
