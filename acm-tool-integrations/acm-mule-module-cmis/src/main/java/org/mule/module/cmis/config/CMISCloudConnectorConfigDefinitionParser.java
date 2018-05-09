
package org.mule.module.cmis.config;

/*-
 * #%L
 * ACM Mule CMIS Connector
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

import org.mule.config.PoolingProfile;
import org.mule.module.cmis.connectivity.CMISCloudConnectorConnectionManager;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CMISCloudConnectorConfigDefinitionParser
        extends AbstractDefinitionParser
{

    public BeanDefinition parse(Element element, ParserContext parserContext)
    {
        parseConfigName(element);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(CMISCloudConnectorConnectionManager.class.getName());
        builder.setScope(BeanDefinition.SCOPE_SINGLETON);
        setInitMethodIfNeeded(builder, CMISCloudConnectorConnectionManager.class);
        setDestroyMethodIfNeeded(builder, CMISCloudConnectorConnectionManager.class);
        parseProperty(builder, element, "username", "username");
        parseProperty(builder, element, "password", "password");
        parseProperty(builder, element, "baseUrl", "baseUrl");
        parseProperty(builder, element, "repositoryId", "repositoryId");
        parseProperty(builder, element, "endpoint", "endpoint");
        parseProperty(builder, element, "connectionTimeout", "connectionTimeout");
        parseProperty(builder, element, "useAlfrescoExtension", "useAlfrescoExtension");
        parseProperty(builder, element, "cxfPortProvider", "cxfPortProvider");
        BeanDefinitionBuilder connectionPoolingProfileBuilder = BeanDefinitionBuilder.rootBeanDefinition(PoolingProfile.class.getName());
        Element connectionPoolingProfileElement = DomUtils.getChildElementByTagName(element, "connection-pooling-profile");
        if (connectionPoolingProfileElement != null)
        {
            parseProperty(connectionPoolingProfileBuilder, connectionPoolingProfileElement, "maxActive");
            parseProperty(connectionPoolingProfileBuilder, connectionPoolingProfileElement, "maxIdle");
            parseProperty(connectionPoolingProfileBuilder, connectionPoolingProfileElement, "maxWait");
            if (hasAttribute(connectionPoolingProfileElement, "exhaustedAction"))
            {
                connectionPoolingProfileBuilder.addPropertyValue("exhaustedAction",
                        PoolingProfile.POOL_EXHAUSTED_ACTIONS.get(connectionPoolingProfileElement.getAttribute("exhaustedAction")));
            }
            if (hasAttribute(connectionPoolingProfileElement, "initialisationPolicy"))
            {
                connectionPoolingProfileBuilder.addPropertyValue("initialisationPolicy", PoolingProfile.POOL_INITIALISATION_POLICIES
                        .get(connectionPoolingProfileElement.getAttribute("initialisationPolicy")));
            }
            if (hasAttribute(connectionPoolingProfileElement, "evictionCheckIntervalMillis"))
            {
                parseProperty(connectionPoolingProfileBuilder, connectionPoolingProfileElement, "evictionCheckIntervalMillis");
            }
            if (hasAttribute(connectionPoolingProfileElement, "minEvictionMillis"))
            {
                parseProperty(connectionPoolingProfileBuilder, connectionPoolingProfileElement, "minEvictionMillis");
            }
            builder.addPropertyValue("connectionPoolingProfile", connectionPoolingProfileBuilder.getBeanDefinition());
        }
        BeanDefinition definition = builder.getBeanDefinition();
        setNoRecurseOnDefinition(definition);
        parseRetryPolicyTemplate("reconnect", element, parserContext, builder, definition);
        parseRetryPolicyTemplate("reconnect-forever", element, parserContext, builder, definition);
        parseRetryPolicyTemplate("reconnect-custom-strategy", element, parserContext, builder, definition);
        return definition;
    }

}
