package com.armedia.acm.services.pipeline.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

/**
 * Compare custom Spring bean configurations with built-in configurations.
 * Whenever ArkCase is upgraded, there might be changes in behaviour configuration files (which reside inside the WAR
 * archive, but at boot are copied to $HOME/.acm/custom/spring-builtin-configuration folder), which must be propagated
 * to custom behaviour configuration files.
 * <p>
 * Currently we are only checking if there are newly introduced beans in built-in files which are missing in the custom
 * configurations. All those beans are written to DIFF file, and it is up to the customers to merge/add those beans in
 * their custom files.
 * <p>
 * Created by Petar Ilin <petar.ilin@armedia.com> on 04.08.2015.
 */
public class ConfigurationComparator implements ApplicationContextAware
{
    /**
     * Folder where custom Spring configuration files are stored ($HOME/.acm/custom/spring).
     */
    private File customFolder;

    /**
     * Folder where built-in Spring configuration files are copied ($HOME/.acm/custom/spring-builtin-configuration).
     */
    private File builtinFolder;

    /**
     * Spring configuration filename pattern.
     */
    private String resourcePattern;

    /**
     * Resource pattern resolver.
     */
    private PathMatchingResourcePatternResolver resolver;

    /**
     * Logger instance.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

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
        boolean failed = false;
        if (failed)
        {
            log.debug("Failed to retrieve data");
        }
        // create custom folder, just in case
        if (!customFolder.exists())
        {
            customFolder.mkdirs();
        }

        try
        {
            // get all custom configurations
            Resource[] customSpringConfigurations = resolver.getResources(resourcePattern);
            for (Resource resource : customSpringConfigurations)
            {
                File customConfiguration = resource.getFile();
                log.info("Found custom configuration [{}]", customConfiguration);

                // calculate built-in configuration file path (this is a copy in "spring-builtin-configuration" folder)
                File builtInConfiguration = new File(builtinFolder + File.separator + customConfiguration.getName());

                // calculate differences
                XmlDiff xmlDiff = new XmlDiff();
                xmlDiff.compare(customConfiguration, builtInConfiguration);
            }
        } catch (IOException | SAXException | ParserConfigurationException | TransformerException e)
        {
            log.error("Unable to process custom folder [{}]", customFolder.getPath(), e);
        }

    }

    public File getCustomFolder()
    {
        return customFolder;
    }

    public void setCustomFolder(File customFolder)
    {
        this.customFolder = customFolder;
    }

    public File getBuiltinFolder()
    {
        return builtinFolder;
    }

    public void setBuiltinFolder(File builtinFolder)
    {
        this.builtinFolder = builtinFolder;
    }

    public String getResourcePattern()
    {
        return resourcePattern;
    }

    public void setResourcePattern(String resourcePattern)
    {
        this.resourcePattern = resourcePattern;
    }

    public PathMatchingResourcePatternResolver getResolver()
    {
        return resolver;
    }

    public void setResolver(PathMatchingResourcePatternResolver resolver)
    {
        this.resolver = resolver;
    }
}
