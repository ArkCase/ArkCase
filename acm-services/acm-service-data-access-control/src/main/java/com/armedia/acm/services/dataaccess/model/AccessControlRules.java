package com.armedia.acm.services.dataaccess.model;

import java.util.List;
import java.util.Map;

/**
 * Access Control Rules.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public class AccessControlRules
{
    /**
     * Map of Solr field names to object properties names.
     */
    Map<String, String> propertiesMapping;

    List<AccessControlRule> accessControlRuleList;

    public Map<String, String> getPropertiesMapping()
    {
        return propertiesMapping;
    }

    public void setPropertiesMapping(Map<String, String> propertiesMapping)
    {
        this.propertiesMapping = propertiesMapping;
    }

    public List<AccessControlRule> getAccessControlRuleList()
    {
        return accessControlRuleList;
    }

    public void setAccessControlRuleList(List<AccessControlRule> accessControlRuleList)
    {
        this.accessControlRuleList = accessControlRuleList;
    }
}
