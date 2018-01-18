
package org.mule.module.cmis.config;

import org.mule.module.cmis.processors.CreateDocumentByPathFromContentMessageProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import javax.annotation.Generated;

@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CreateDocumentByPathFromContentDefinitionParser
        extends AbstractDefinitionParser
{

    public BeanDefinition parse(Element element, ParserContext parserContext)
    {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(CreateDocumentByPathFromContentMessageProcessor.class.getName());
        builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
        parseConfigRef(element, builder);
        parseProperty(builder, element, "folderPath", "folderPath");
        parseProperty(builder, element, "filename", "filename");
        if (hasAttribute(element, "content-ref"))
        {
            if (element.getAttribute("content-ref").startsWith("#"))
            {
                builder.addPropertyValue("content", element.getAttribute("content-ref"));
            }
            else
            {
                builder.addPropertyValue("content", (("#[registry:" + element.getAttribute("content-ref")) + "]"));
            }
        }
        parseProperty(builder, element, "mimeType", "mimeType");
        parseProperty(builder, element, "versioningState", "versioningState");
        parseProperty(builder, element, "objectType", "objectType");
        parseMapAndSetProperty(element, builder, "properties", "properties", "property", new ParseDelegate<String>()
        {

            public String parse(Element element)
            {
                return element.getTextContent();
            }

        });
        parseProperty(builder, element, "force", "force");
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
