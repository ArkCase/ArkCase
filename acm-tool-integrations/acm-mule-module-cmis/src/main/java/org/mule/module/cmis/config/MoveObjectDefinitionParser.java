
package org.mule.module.cmis.config;

import org.mule.module.cmis.processors.MoveObjectMessageProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class MoveObjectDefinitionParser
        extends AbstractDefinitionParser
{

    public BeanDefinition parse(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(MoveObjectMessageProcessor.class.getName());
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        parseConfigRef(element, builder);
        if (hasAttribute(element, "cmisObject-ref"))
        {
            if (element.getAttribute("cmisObject-ref").startsWith("#"))
            {
                builder.addPropertyValue("cmisObject", element.getAttribute("cmisObject-ref"));
            }
            else
            {
                builder.addPropertyValue("cmisObject", (("#[registry:" + element.getAttribute("cmisObject-ref")) + "]"));
            }
        }
        parseProperty(builder, element, "objectId", "objectId");
        parseProperty(builder, element, "sourceFolderId", "sourceFolderId");
        parseProperty(builder, element, "targetFolderId", "targetFolderId");
        parseProperty(builder, element, "username", "username");
        parseProperty(builder, element, "password", "password");
        parseProperty(builder, element, "baseUrl", "baseUrl");
        parseProperty(builder, element, "repositoryId", "repositoryId");
        parseProperty(builder, element, "endpoint", "endpoint");
        parseProperty(builder, element, "connectionTimeout", "connectionTimeout");
        parseProperty(builder, element, "useAlfrescoExtension", "useAlfrescoExtension");
        parseProperty(builder, element, "cxfPortProvider", "cxfPortProvider");
        BeanDefinition definition = builder.getBeanDefinition();
        setNoRecurseOnDefinition(definition);
        attachProcessorDefinition(parserContext, definition);
        return definition;
    }

}
