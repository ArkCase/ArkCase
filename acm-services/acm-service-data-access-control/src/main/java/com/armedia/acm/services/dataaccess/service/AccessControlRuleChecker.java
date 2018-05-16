package com.armedia.acm.services.dataaccess.service;

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

import com.armedia.acm.services.dataaccess.model.AccessControlRules;

import org.springframework.security.core.Authentication;

/**
 * Check if particular user is granted access to a given object.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public interface AccessControlRuleChecker
{
    /**
     * Check if particular user is granted access to a given object.
     *
     * @param authentication
     *            authentication token
     * @param targetId
     *            the identifier for the object instance
     * @param targetType
     *            target type
     * @param permission
     *            required permissions, separated with "|"
     * @param solrDocument
     *            Solr data stored for this object
     * @return true if user is allowed to access this object, false otherwise
     */
    boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, String permission, String solrDocument);

    /**
     * Getter method.
     *
     * @return configured AC rules
     */
    AccessControlRules getAccessControlRules();

    /**
     * Setter method.
     *
     * @param accessControlRules
     *            AC rules
     */
    void setAccessControlRules(AccessControlRules accessControlRules);
}
