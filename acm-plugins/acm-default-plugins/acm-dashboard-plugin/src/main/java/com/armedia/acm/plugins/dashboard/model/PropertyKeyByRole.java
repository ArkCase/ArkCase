package com.armedia.acm.plugins.dashboard.model;

/**
 * Created by marjan.stefanoski on 9/22/2014.
 */
public enum PropertyKeyByRole {

    ROLE_CALLCENTER("ROLE_CALLCENTER","acm.role.callcenter"),
    ROLE_INVESTIGATOR("ROLE_INVESTIGATOR","acm.role.investigator"),
    ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR","acm.role.administrator"),
    ROLE_INVESTIGATOR_SUPERVISOR("ROLE_INVESTIGATOR_SUPERVISOR","acm.role.investigator.supervisor"),
    ROLE_ANALYST("ROLE_ANALYST","acm.role.analyst"),
    ACM_ANALYST_DEV("ACM_ANALYST_DEV","acm.role.analyst.dev"),
    ACM_CALLCENTER_DEV("ACM_CALLCENTER_DEV","acm.role.callcenter.dev"),
    ACM_ADMINISTRATOR_DEV("ACM_ADMINISTRATOR_DEV","acm.role.administrator.dev"),
    ACM_SUPERVISOR_DEV("ACM_SUPERVISOR_DEV","acm.role.supervisor.dev"),
    ACM_INVESTIGATOR_DEV("ACM_INVESTIGATOR_DEV","acm.role.investigator.dev"),
    NONE("NONE","acm.role.none");

    private String roleName;
    private String propertyKey;

    PropertyKeyByRole(String roleName, String propertyKey) {
        this.propertyKey = propertyKey;
        this.roleName = roleName;
    }

    public static PropertyKeyByRole getPropertyKeyByRoleName(String roleName) {
        for (PropertyKeyByRole attribute : values()) {
            if (attribute.roleName.equals(roleName)) {
                return attribute;
            }
        }
        return PropertyKeyByRole.NONE;
    }
    public String getRoleName() {
        return roleName;
    }

    public String getPropertyKey() {
        return propertyKey;
    }
}
