package com.armedia.acm.services.pipeline.diff;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

/**
 * XML Spring configuration files comparison.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 04.08.2015.
 */
public class XmlDiff
{
    /**
     * Compare two XML files and write differences (if any).
     * <p>
     * We are using this logic: for each bean defined in a custom configuration (which overrides the "built-in" bean)
     * check if there is an exact match in the built-in configuration. Non-matching beans might indicate (but not
     * necessarily mean) that there was a modification in the built-in configuration which should be propagated
     * to the custom one (eg: with ArkCase upgrade we are delivering modified behaviour which must be also applied
     * to the customized behaviour).
     *
     * @param customConfiguration  custom Spring beans configuration file
     * @param builtinConfiguration built-in Spring beans configuration file
     */
    public void compare(File customConfiguration, File builtinConfiguration) throws ParserConfigurationException, IOException, SAXException, TransformerException
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        // parse custom version of the configuration file
        Document custom = docBuilder.parse(customConfiguration);
        NodeList customBeans = custom.getElementsByTagName("bean");

        // parse built-in version of the configuration file
        Document builtin = docBuilder.parse(builtinConfiguration);
        NodeList builtinBeans = builtin.getElementsByTagName("bean");

        // create new document for storing differences
        Document diff = docBuilder.newDocument();
        Comment comment = diff.createComment("Double check the differences in the beans below, and merge them if necessary");
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

        File diffFile = new File(customConfiguration.getAbsolutePath() + ".diff");
        if (diffElement.getChildNodes().getLength() > 0)
        {
            // save the overridden beans in a file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(diff);
            Result result = new StreamResult(diffFile);
            transformer.transform(source, result);
        } else if (diffFile.exists())
        {
            // delete the "diff" file
            diffFile.delete();
        }
    }
}
