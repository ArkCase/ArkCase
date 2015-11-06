package com.armedia.acm.services.dataaccess.model;

import java.util.List;

/**
 * Access Control Rules.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlRules
{
    List<AccessControlRule> accessControlRuleList;

    public List<AccessControlRule> getAccessControlRuleList()
    {
        return accessControlRuleList;
    }

    public void setAccessControlRuleList(List<AccessControlRule> accessControlRuleList)
    {
        this.accessControlRuleList = accessControlRuleList;
    }
}
