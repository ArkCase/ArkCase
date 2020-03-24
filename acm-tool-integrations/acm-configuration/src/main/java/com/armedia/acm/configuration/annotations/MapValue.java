
package com.armedia.acm.configuration.annotations;

/*-
 * #%L
 * ACM Tool Integrations: Configuration Library
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation at the field or method/constructor parameter level
 * that indicates a default value expression for the affected argument.
 *
 * <p>
 * Used for injection of a property key from configuration
 *
 * <p>
 * A common use case is to assign default hashMap field values using
 * "systemProperties.myProp" style expressions.
 *
 * <p>
 * Note that actual processing of the {@code @HashMapValue} annotation is performed
 * by the ConfigurationContainer and PropertyNavigatior.
 *
 * @author Mario Gjurcheski
 * @see ListValueAspect
 * @see com.armedia.acm.configuration.core.propertysource.PropertyNavigator
 * @see com.armedia.acm.configuration.core.ConfigurationContainer
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapValue
{

    /**
     * The actual value expression: e.g. "propertyKey".
     */
    String value();

    /**
     * Shoud be converted from the root key: e.g. "acmPluginConfigurationPrivileges" in
     * "acmPluginConfigurationPrivileges.acmCasePluginPrivileges.acmCaseModulePrivilege"
     */
    boolean convertFromTheRootKey() default false;

    /**
     * Extract properties from specific configuration (ConfigurationContainer, LdapConfiguration...)
     * 
     * @see com.armedia.acm.configuration.core.ConfigurationContainer
     * @see com.armedia.acm.configuration.core.LabelsConfiguration
     * @see com.armedia.acm.configuration.core.LdapConfigurationContainer
     *
     */
    String configurationName() default "";

}
