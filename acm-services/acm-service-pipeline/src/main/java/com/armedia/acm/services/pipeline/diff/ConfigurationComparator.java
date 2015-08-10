package com.armedia.acm.services.pipeline.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Compare custom Spring bean configurations with built-in configurations.
 * Whenever ArkCase is upgraded, there might be changes in Spring configuration files (which reside inside the WAR
 * archive, but at boot are copied to $HOME/.acm/default-config/spring folder), which must be propagated to custom
 * Spring configuration files.
 * <p>
 * Currently we are checking if there are beans defined in custom Spring configuration files (delivered with custom
 * JAR modules dropped in $HOME/.acm/custom/WEB-INF/lib folder) which are overriding the built-in defined beans.
 * All those built-in beans are written to a DIFF file, and it is up to the customers to merge the differences in
 * their custom files.
 * <p>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 04.08.2015.
 */
public class ConfigurationComparator implements ApplicationContextAware
{
    /**
     * XML Document builder.
     */
    private DocumentBuilder docBuilder;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor.
     * Create XML document builder
     *
     * @throws ParserConfigurationException on XML parser configuration error
     */
    public ConfigurationComparator() throws ParserConfigurationException
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        docBuilder = docBuilderFactory.newDocumentBuilder();
    }

    /**
     * Foe each customized configuration file, make a comparison to appropriate built-in file and write the changes
     * to disk
     *
     * @param applicationContext Spring application context, not used
     * @throws BeansException on error
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        try
        {
            NodeList builtinBeans = getBuiltinBeans();

            // custom Spring configuration files are copied to this folder
            String customFolderPath = System.getProperty("user.home") + "/.acm/custom-config/spring";
            // retrieve all custom bean configurations
            Files.walk(Paths.get(customFolderPath)).forEach(filePath -> {
                if (Files.isRegularFile(filePath))
                {
                    try
                    {
                        // parse built-in version of the configuration file
                        Document custom = docBuilder.parse(Files.newInputStream(filePath));
                        NodeList customBeans = custom.getElementsByTagName("bean");
                        // compare it to built-in beans
                        compare(filePath + ".diff", customBeans, builtinBeans);
                    } catch (SAXException | IOException e)
                    {
                        log.error("Unable to read and parse '{}'", filePath, e);
                    }
                }
            });
        } catch (IOException e)
        {
            log.error("Unable to compare custom to built-in Spring configuration files", e);
        }
    }

    /**
     * Get the list of all beans defined in all built-in configuration files
     *
     * @return list of beans as NodeList
     * @throws IOException on error listing files in folder
     */
    private NodeList getBuiltinBeans() throws IOException
    {
        // FIXME: I did not find a better way of retrieving all the <bean/> elements as a NodeList
        Document allBuiltinBeans = docBuilder.newDocument();
        Element beans = allBuiltinBeans.createElement("beans");
        allBuiltinBeans.appendChild(beans);

        // built-in Spring configuration files are copied to this folder
        String builtinFolderPath = System.getProperty("user.home") + "/.acm/default-config/spring";
        // retrieve all built-in beans from all configurations
        Files.walk(Paths.get(builtinFolderPath)).forEach(filePath -> {
            if (Files.isRegularFile(filePath))
            {
                try
                {
                    // parse built-in version of the configuration file
                    Document builtin = docBuilder.parse(Files.newInputStream(filePath));
                    NodeList builtinBeans = builtin.getElementsByTagName("bean");
                    for (int i = 0; i < builtinBeans.getLength(); i++)
                    {
                        Node builtinBean = builtinBeans.item(i);
                        builtinBean = allBuiltinBeans.importNode(builtinBean, true);
                        beans.appendChild(builtinBean);
                    }
                } catch (SAXException | IOException e)
                {
                    log.error("Cannot parse configuration file '{}'", filePath, e);
                }
            }
        });
        return beans.getChildNodes();
    }

    /**
     * Check if custom Spring configuration overrides some built-in bean and write that built-in bean to a diff file.
     * <p>
     * This will enable easier problem detection in case some desired built-in bean definition is accidentally
     * or deliberately modified and it causes malfunction.
     *
     * @param diffFilename filename where to store the bean that's overridden (spring-custom-config.xml.diff)
     * @param customBeans  list of custom beans defined in a custom Spring configuration (single file)
     * @param builtinBeans list of built-in beans defined in all Spring configuration files
     */
    private void compare(String diffFilename, NodeList customBeans, NodeList builtinBeans)
    {
        // create new document for storing differences
        Document diff = docBuilder.newDocument();
        String note = "\n" +
                "NOTE:the beans below are overridden by a custom Spring configuration with the same name\n" +
                "as this file without the .diff suffix. Please double check if bean declaration is modified\n" +
                "intentionally or there is a need for manual merge.\n";
        Comment comment = diff.createComment(note);
        diff.appendChild(comment);
        Element diffElement = diff.createElement("diff");
        diff.appendChild(diffElement);

        // find which built-in bean is overridden and compare them
        for (int i = 0; i < customBeans.getLength(); i++)
        {
            Element customBean = (Element) customBeans.item(i);
            String customBeanId = customBean.getAttribute("id");

            for (int j = 0; j < builtinBeans.getLength(); j++)
            {
                Element builtinBean = (Element) builtinBeans.item(j);
                String builtinBeanId = builtinBean.getAttribute("id");

                if (builtinBeanId.equals(customBeanId))
                {
                    // bean ids match, now compare the content
                    if (!builtinBean.isEqualNode(customBean))
                    {
                        // different content, might indicate that there is a need for merging
                        // so we are putting it to "diff" document
                        builtinBean = (Element) diff.importNode(builtinBean, true);
                        diffElement.appendChild(builtinBean);
                    }
                    break;
                }
            }
        }

        File diffFile = new File(diffFilename);
        if (diffElement.getChildNodes().getLength() > 0)
        {
            // save the overridden beans in a file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try
            {
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(diff);
                Result result = new StreamResult(diffFile);
                transformer.transform(source, result);
            } catch (TransformerException e)
            {
                log.error("Unable to create diff file '{}'", diffFilename, e);
            }
        }
    }
}
