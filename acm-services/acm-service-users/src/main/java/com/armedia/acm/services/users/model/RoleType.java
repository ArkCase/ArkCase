package com.armedia.acm.services.users.model;

/**
 * Created by marjan.stefanoski on 29.10.2014.
 */
public enum RoleType {
    LDAP_GROUP("LDAP_GROUP"),
    APPLICATION_ROLE("APPLICATION_ROLE");

    private String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
