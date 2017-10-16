
package org.mule.module.cmis.config;

import javax.annotation.Generated;
import org.mule.module.cmis.processors.FolderMessageProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class FolderDefinitionParser
    extends AbstractDefinitionParser
{


    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(FolderMessageProcessor.class.getName());
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        parseConfigRef(element, builder);
        if (hasAttribute(element, "folder-ref")) {
            if (element.getAttribute("folder-ref").startsWith("#")) {
                builder.addPropertyValue("folder", element.getAttribute("folder-ref"));
            } else {
                builder.addPropertyValue("folder", (("#[registry:"+ element.getAttribute("folder-ref"))+"]"));
            }
        }
        parseProperty(builder, element, "folderId", "folderId");
        parseProperty(builder, element, "get", "get");
        parseProperty(builder, element, "depth", "depth");
        parseProperty(builder, element, "filter", "filter");
        parseProperty(builder, element, "orderBy", "orderBy");
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
