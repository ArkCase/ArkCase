package com.armedia.acm.plugins.admin.model;

import java.util.List;

public class TimesheetConfig {

    List<TimesheetChargeRoleConfigItem> chargeRoleItems;

    public List<TimesheetChargeRoleConfigItem> getChargeRoleItems() {
        return chargeRoleItems;
    }

    public void setChargeRoleItems(List<TimesheetChargeRoleConfigItem> chargeRoleItems) {
        this.chargeRoleItems = chargeRoleItems;
    }
}
