package com.armedia.acm.muletools.mulecontextmanager;

import com.armedia.acm.web.api.MDCConstants;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.transformer.DataType;
import org.mule.config.AnnotationsConfigurationBuilder;
import org.mule.config.ConfigResource;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MuleContextManager implements ApplicationContextAware
{

    private MuleContext muleContext;
    private transient Logger log = LoggerFactory.getLogger(getClass());
    private String muleConfigFilePattern;
    private List<String> specificConfigFiles;

    public void dispatch(String endpoint, Object payload, Map<String, Object> messageProperties) throws MuleException
    {
        MuleMessage request = new DefaultMuleMessage(payload, messageProperties, getMuleContext());
        setMDCProperties(request);
        getMuleClient().dispatch(endpoint, request);
    }

    public void dispatch(String endpoint, Object payload) throws MuleException
    {
        MuleMessage request = new DefaultMuleMessage(payload, getMuleContext());
        setMDCProperties(request);
        getMuleClient().dispatch(endpoint, request);
    }

    public MuleMessage send(String endpoint, Object payload, Map<String, Object> messageProperties) throws MuleException
    {
        MuleMessage request = new DefaultMuleMessage(payload, messageProperties, getMuleContext());
        setMDCProperties(request);

        MuleMessage received = getMuleClient().send(endpoint, request);

        return received;
    }

    public MuleMessage send(String endpoint, Object payload) throws MuleException
    {
        MuleMessage request = new DefaultMuleMessage(payload, getMuleContext());

        setMDCProperties(request);
        MuleMessage received = getMuleClient().send(endpoint, request);

        return received;
    }

    protected MuleClient getMuleClient()
    {
        return getMuleContext().getClient();
    }

    private void setMDCProperties(MuleMessage message)
    {
        message.setOutboundProperty(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, MDC.get(MDCConstants.EVENT_MDC_REQUEST_ID_KEY), DataType.STRING_DATA_TYPE);
        message.setOutboundProperty(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY, MDC.get(MDCConstants.EVENT_MDC_REQUEST_REMOTE_ADDRESS_KEY), DataType.STRING_DATA_TYPE);
        message.setOutboundProperty(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY, MDC.get(MDCConstants.EVENT_MDC_REQUEST_USER_ID_KEY), DataType.STRING_DATA_TYPE);
    }

    private void startMuleContext(ApplicationContext applicationContext) throws MuleException, IOException
    {
        if ( getMuleContext() != null )
        {
            return;
        }

        ConfigResource[] configs = findConfigResources();
        SpringXmlConfigurationBuilder springXmlConfigurationBuilder = new SpringXmlConfigurationBuilder(configs);
        springXmlConfigurationBuilder.setParentContext(applicationContext);

        // ensure Mule processes Mule annotations in Spring beans
        AnnotationsConfigurationBuilder annotationsConfigurationBuilder = new AnnotationsConfigurationBuilder();

        List<ConfigurationBuilder> builders = new ArrayList<>();
        builders.add(springXmlConfigurationBuilder);
        builders.add(annotationsConfigurationBuilder);

        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        MuleContext muleContext = muleContextFactory.createMuleContext(builders, new DefaultMuleContextBuilder());

        // TODO: application context does not need to be kept in separate variable. If removed, changes to the code needs to be done, where this variable is used. We should do this in a separate JIRA issue, as tests should be performed.
        muleContext.getRegistry().registerObject("arkContext", applicationContext);

        if ( log.isDebugEnabled() )
        {
            log.debug("Starting mule context");
        }

        muleContext.start();
        setMuleContext(muleContext);

        if ( log.isDebugEnabled() )
        {
            log.debug("Done.");
        }
    }

    private ConfigResource[] findConfigResources() throws IOException
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Finding Mule flow XML configuration files.");
        }

        if ( getMuleConfigFilePattern() != null )
        {
            return loadConfigFromPattern();
        }
        else if ( getSpecificConfigFiles() != null )
        {
            return loadSpecificConfigFiles();
        }
        else
        {
            throw new IllegalStateException("Either a muleConfigFilePattern or specificConfigFiles must be specified");
        }
    }

    private ConfigResource[] loadSpecificConfigFiles() throws IOException
    {
        ConfigResource[] configs = new ConfigResource[getSpecificConfigFiles().size()];
        for ( int a = 0; a < getSpecificConfigFiles().size(); a++ )
        {
            Resource configResource = new ClassPathResource(getSpecificConfigFiles().get(a));
            configs[a] = new ConfigResource(configResource.getURL());
        }

        return configs;
    }

    private ConfigResource[] loadConfigFromPattern() throws IOException
    {
        PathMatchingResourcePatternResolver pathResolver = new PathMatchingResourcePatternResolver();
        Resource[] muleConfigs = pathResolver.getResources(getMuleConfigFilePattern());
        ConfigResource[] configs = new ConfigResource[muleConfigs.length];

        if ( log.isDebugEnabled() )
        {
            log.debug(muleConfigs.length + " mule configs found.");
        }

        for ( int a = 0; a < muleConfigs.length; a++ )
        {
            Resource muleConfig = muleConfigs[a];
            if ( log.isDebugEnabled() )
            {
                log.debug("Processing mule config " + muleConfig.getFilename());
            }
            configs[a] = new ConfigResource(muleConfig.getURL());
        }
        return configs;
    }

    public void shutdownBean()
    {
        try
        {
            if ( getMuleContext() != null )
            {
                log.debug("Stopping Mule context");
                getMuleContext().stop();
                getMuleContext().dispose();
            }
        }
        catch (MuleException e)
        {
            log.error("Could not stop Mule context: " + e.getMessage(), e);
        }
    }


    public MuleContext getMuleContext()
    {
        return muleContext;
    }

    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        if ( getMuleContext() == null )
        {
            try
            {
                startMuleContext(applicationContext);
            }
            catch (MuleException | IOException e)
            {
                log.error("Could not start Mule context: " + e.getMessage(), e);
                throw new IllegalStateException(e);
            }
        }
    }

    public String getMuleConfigFilePattern()
    {
        return muleConfigFilePattern;
    }

    public void setMuleConfigFilePattern(String muleConfigFilePattern)
    {
        this.muleConfigFilePattern = muleConfigFilePattern;
    }

    public List<String> getSpecificConfigFiles()
    {
        return specificConfigFiles;
    }

    public void setSpecificConfigFiles(List<String> specificConfigFiles)
    {
        this.specificConfigFiles = specificConfigFiles;
    }
}
