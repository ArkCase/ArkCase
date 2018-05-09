package com.armedia.acm.services.dataaccess.model;

/*-
 * #%L
 * ACM Service: Data Access Control
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
